import javax.swing.*;
import java.io.*;
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

    WorkerTCP(Socket clSocket, String peerAddress, Table t, int sID, DatagramSocket udpSocket){
        clientSocket = clSocket;
        table = t;
        sessionID = sID;
        try {
            udpAddress = InetAddress.getByName(peerAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        anonSocket = udpSocket;
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
            table.addToTable(sessionID, new TableEntry(clientSocket,udpAddress,sessionID));// ADD SOCKET TO TABLE

            result = inFromClient.read(bytesFromClient, 0, 1024); // GET REQUEST FROM CLIENT
            for (int j = 0; j < result; j++) {
                System.out.print((char) bytesFromClient[j]);
            }
            //ADD HEADER
            AnonPacket packet = new AnonPacket(bytesFromClient, sessionID, sessionID,0);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(packet);
            oo.close();

            byte[] bytePacket = bStream.toByteArray();

            DatagramPacket dp = new DatagramPacket(bytePacket, bytePacket.length, udpAddress, 6666); //// SEND REQUEST TO PEER
            anonSocket.send(dp);

            closeStreams();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
