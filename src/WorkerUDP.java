import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class WorkerUDP implements Runnable {
    Socket serverSocket;
    DatagramSocket anonSocket;
    ArrayList<Byte> responseFromServer;
    DatagramPacket packet;
    Table table;
    //TCP
    DataInputStream inStream;
    DataOutputStream outStream;
    //UDP
    byte buf[] = new byte[1024];

    WorkerUDP(String svIP, DatagramSocket udpSocket, DatagramPacket p, Table t){
        try {
            serverSocket = new Socket(svIP,80);
        } catch (IOException e) {
            e.printStackTrace();
        }
        anonSocket = udpSocket;
	packet = p;
        table = t;

    }

    private void closeStreams() throws IOException{
        inStream.close();
        outStream.close();
        serverSocket.close();
    }

    private void closeStreamsClient(Socket s) throws IOException{
        s.close();
    }

    private ArrayList<byte[]> fragmentResponse(byte[] fileArray) {
        ArrayList<byte[]> res = new ArrayList<>();
        int byteNum;
        int fragmentSize;
        byte[] fragment;
        byte[] bytePacket;
        for (byteNum = 0; byteNum < fileArray.length; byteNum += 512) {
            fragment = new byte[512];
            fragmentSize = Math.min(fileArray.length - byteNum, 512);
            for (int j = 0; j < fragmentSize; j++) {
                fragment[j] = fileArray[byteNum + j];
            }
            res.add(fragment);
        }
        return res;
    }

    @Override
    public void run() {
        int result, i;
        i = 0;
        byte[] currentByte = new byte[1];
        byte[] bytesFromClient = new byte[4096];
        byte[] fileArray;
        try {
            //IR BUSCAR A HEADER ENDEREÇO UDP DE ANON
	    for(int j = 0; j < packet.getData().length; j++){
	        System.out.print((char) packet.getData()[j]);
	    }
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            AnonPacket anonPacket = (AnonPacket) iStream.readObject();
            iStream.close();
	        System.out.println(anonPacket.getData());
            InetAddress sourceAnonAdress = packet.getAddress();
            int destSessionID = anonPacket.getDestSessionID();
            TableEntry entry = table.getFromTable(destSessionID);

            if(entry == null) {
                try {
                    inStream = new DataInputStream(serverSocket.getInputStream());
                    outStream = new DataOutputStream(serverSocket.getOutputStream());
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                outStream.write(anonPacket.getData(), 0, anonPacket.getData().length);// SEND REQUEST TO SERVER
                outStream.flush();

                responseFromServer = new ArrayList<>(); // GET RESPONSE FROM SERVER
                while ((result = inStream.read(currentByte, 0, 1)) > -1) {
                    responseFromServer.add(currentByte[0]);
                    System.out.print((char) currentByte[0]);
                }
                fileArray = new byte[responseFromServer.size()];
                for (byte b : responseFromServer) {
                    fileArray[i] = b;
                    i++;
                }
                //FRAGMENT RESPONSE
                ArrayList<byte[]> fragmented = fragmentResponse(fileArray);
                for(int numPacket = 0; numPacket < fragmented.size(); numPacket++){
                    AnonPacket anonP;
                    if(fragmented.size() - 1 != numPacket)
                        anonP = new AnonPacket(fragmented.get(numPacket),0,anonPacket.getSourceSessionID(),numPacket, false);
                    else
                        anonP = new AnonPacket(fragmented.get(numPacket),0,anonPacket.getSourceSessionID(),numPacket, true);

                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    ObjectOutput oo = new ObjectOutputStream(bStream);
                    oo.writeObject(anonP);
                    oo.close();

                    byte [] bytePacket = bStream.toByteArray();

                    DatagramPacket dp = new DatagramPacket(bytePacket, bytePacket.length, packet.getAddress(), 6666); //MUDARRRRRRR // SEND RESPONSE TO PEER
                    anonSocket.send(dp);
                }
		        closeStreams();
            } else {
                try {
                    outStream = new DataOutputStream(entry.getClientSocket().getOutputStream());
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                entry.addPacket(anonPacket);
                if(entry.getPackets().isFullyReceived()) {
                    outStream.write(entry.getPackets().getData(), 0, entry.getPackets().getData().length);// SEND REQUEST TO SERVER
                    outStream.flush();
                    closeStreamsClient(entry.getClientSocket());
                    table.removeFromTable(destSessionID);
                }
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
