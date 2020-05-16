import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    WorkerUDP(Socket svSocket, DatagramSocket udpSocket, DatagramPacket p, Table t){
        serverSocket = svSocket;
        anonSocket = udpSocket;
        table = t;

    }

    private void closeStreams() throws IOException{
        inStream.close();
        outStream.close();
        serverSocket.close();
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
            InetAddress sourceAnonAdress = null;
            int destSessionID = 0;
            TableEntry entry = table.getFromTable(destSessionID);
            if(entry == null) {
                try {
                    inStream = new DataInputStream(serverSocket.getInputStream());
                    outStream = new DataOutputStream(serverSocket.getOutputStream());
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                outStream.write(packet.getData(), 0, packet.getData().length);// SEND REQUEST TO SERVER
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
                //ADD HEADER
                DatagramPacket dp = new DatagramPacket(fileArray, result, sourceAnonAdress, 6666); //MUDARRRRRRR // SEND RESPONSE TO PEER
                anonSocket.send(dp);
            } else {
                try {
                    inStream = new DataInputStream(entry.getClientSocket().getInputStream());
                    outStream = new DataOutputStream(entry.getClientSocket().getOutputStream());
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                outStream.write(packet.getData(), 0, packet.getData().length);// SEND REQUEST TO SERVER
                outStream.flush();
            }


            closeStreams();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
