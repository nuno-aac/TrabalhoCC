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
    CryptoHelper ch;

    WorkerUDP(String svIP, DatagramSocket udpSocket, DatagramPacket p, Table t, CryptoHelper chelper){
        try {
            serverSocket = new Socket(svIP,80);
        } catch (IOException e) {
            e.printStackTrace();
        }
        anonSocket = udpSocket;
	packet = p;
        table = t;
	ch = chelper;
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
        for (byteNum = 0; byteNum < fileArray.length; byteNum += 512) {
            fragment = new byte[512];
            fragmentSize = Math.min(fileArray.length - byteNum, 512);
            for (int j = 0; j < fragmentSize; j++) {
                fragment[j] = fileArray[byteNum + j];
            }
            res.add(fragment);
        }
        System.out.println("Fragmented " + fileArray.length + " bytes into " +  res.size() + "parts");
        return res;
    }

    private void handleRequest(AnonPacket anonPacket) throws IOException {
        int result, i;
        i = 0;
        byte[] currentByte = new byte[1];
        byte[] bytesFromClient = new byte[4096];
        byte[] fileArray;
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
            if(fragmented.size() - 1 != numPacket){
                System.out.println(("Sending Packet " + numPacket));
                anonP = new AnonPacket(fragmented.get(numPacket),0,anonPacket.getSourceSessionID(),numPacket, false);
            }else{
                System.out.println(("Sending last Packet " + numPacket));
                anonP = new AnonPacket(fragmented.get(numPacket),0,anonPacket.getSourceSessionID(),numPacket, true);
            }
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(anonP);
            oo.close();

            byte [] bytePacket = bStream.toByteArray();

	    try{
		byte[] fragmentEncrypted = ch.runEncrypts(bytePacket);

                DatagramPacket dp = new DatagramPacket(fragmentEncrypted, fragmentEncrypted.length, packet.getAddress(), 6666); //MUDARRRRRRR // SEND RESPONSE TO PEER
                anonSocket.send(dp);
	    }
	    catch(Exception e){
		System.out.println("Something went wrong encrypts response udp");
	    }
        }
        closeStreams();
    }

    private void handleResponse(TableEntry entry, AnonPacket anonPacket) throws IOException {
        try {
            outStream = new DataOutputStream(entry.getClientSocket().getOutputStream());
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        entry.addPacket(anonPacket);
        if(entry.getPackets().isFullyReceived()) {
            System.out.println("I have all response packets!");
            outStream.write(entry.getPackets().getData(), 0, entry.getPackets().getData().length);// SEND RESPONSE TO CLIENT
            outStream.flush();
            closeStreamsClient(entry.getClientSocket());
            table.removeFromTable(anonPacket.getDestSessionID());
        }
    }

    @Override
    public void run() {
        try {
	    byte[] responseBytes = new byte[packet.getLength()];
	    for(int i = 0; i < packet.getLength(); i++){
	    	responseBytes[i] = packet.getData()[i];
	    }
	    byte[] decryptedPacket = ch.runDecrypts(responseBytes);
            //IR BUSCAR A HEADER ENDEREÇO UDP DE ANON
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(decryptedPacket));
            AnonPacket anonPacket = (AnonPacket) iStream.readObject();
	        System.out.println("Received response packet number" + anonPacket.getNumPacket());
            iStream.close();
            InetAddress sourceAnonAdress = packet.getAddress();
            int destSessionID = anonPacket.getDestSessionID();
            TableEntry entry = table.getFromTable(destSessionID);
	    
            if(entry == null) {
                handleRequest(anonPacket);
            } else {
                handleResponse(entry,anonPacket);
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        } catch (Exception e){
	    System.out.println(e.toString());
	}
    }
}
