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
    CryptoHelper ch;

    WorkerTCP(Socket clSocket, String peerAddress, Table t, int sID, DatagramSocket udpSocket, CryptoHelper chelper){
        clientSocket = clSocket;
	this.anonSocket = anonSocket;
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
            //outToClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
	ch = chelper;
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
            byte[] resultArray = new byte[result];
	    for (int j = 0; j < result; j++) {
                System.out.print((char) bytesFromClient[j]);
		resultArray[j] = bytesFromClient[j];
            }
	    
            //
            //ADD HEADER
            AnonPacket packet = new AnonPacket(resultArray, sessionID, sessionID,0,true);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(packet);
            oo.close();

            byte[] bytePacket = bStream.toByteArray();
	        System.out.println("bytepacket " + bytePacket.length);
		
	    try{
	        byte[] encryptedPacket = ch.runEncrypts(bytePacket);
		DatagramPacket dp = new DatagramPacket(encryptedPacket, encryptedPacket.length, udpAddress, 6666); // SEND REQUEST TO PEER
                anonSocket.send(dp);
	    }
	    catch(Exception e){
		System.out.println("Something went wrong encrypts request udp");
	    }		    

	        System.out.println("Request packet enviado");

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
