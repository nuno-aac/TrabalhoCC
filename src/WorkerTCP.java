import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class WorkerTCP implements Runnable {
    Socket clientSocket;
    DatagramSocket anonSocket;
    InetAddress udpAddress;
    int sessionID;
    //TCP
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    //TABLE
    Table table;
    byte buf[] = new byte[1024];

    WorkerTCP(Socket clSocket, String peerAddress, Table t, int sID){
        clientSocket = clSocket;
        table = t;
        sessionID = sID;
        try {
            udpAddress = InetAddress.getByName(peerAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            anonSocket = new DatagramSocket(6666);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            inFromClient = new DataInputStream(clientSocket.getInputStream());
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void closeStreams() throws IOException{
        inFromClient.close();
        outToClient.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        int result, i;
        i = 0;
        byte[] currentByte = new byte[1];
        byte[] bytesFromClient = new byte[4096];
        byte[] fileArray;
        try {
            String clientIP = clientSocket.getInetAddress().toString();
            table.addToTable(sessionID, new TableEntry(clientSocket,udpAddress,sessionID));
            result = inFromClient.read(bytesFromClient, 0, 1024); // GET REQUEST FROM CLIENT
            for (int j = 0; j < result; j++) {
                System.out.print((char) bytesFromClient[j]);
            }
            //ADD HEADER
            DatagramPacket dp = new DatagramPacket(bytesFromClient, result, udpAddress, 6666); //MUDARRRRRRR // SEND REQUEST TO PEER
            anonSocket.send(dp);

            closeStreams();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
