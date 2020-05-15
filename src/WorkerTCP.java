import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class WorkerTCP implements Runnable {
    Socket clientSocket, serverSocket;
    DatagramSocket anonSocket;
    ArrayList<Byte> responseFromServer;
    //TCP
    DataInputStream inFromClient, inFromServer;
    DataOutputStream outToServer, outToClient;
    //UDP
    byte buf[] = new byte[1024];

    WorkerTCP(Socket clSocket, Socket svSocket){
        clientSocket = clSocket;
        serverSocket = svSocket;
        try {
            anonSocket = new DatagramSocket(80);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            inFromClient = new DataInputStream(clientSocket.getInputStream());
            inFromServer = new DataInputStream(serverSocket.getInputStream());
            outToServer = new DataOutputStream(serverSocket.getOutputStream());
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void closeStreams() throws IOException{
        inFromClient.close();
        outToServer.close();
        inFromServer.close();
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
            result = inFromClient.read(bytesFromClient, 0, 1024); // GET REQUEST FROM CLIENT
            for (int j = 0; j < result; j++) {
                System.out.print((char) bytesFromClient[j]);
            }

            InetAddress address = InetAddress.getByName("10.3.3.1"); //MUDARRRRRRR
            DatagramPacket dp = new DatagramPacket(bytesFromClient, result, address, 80);
            anonSocket.send(dp);
            //outToServer.write(bytesFromClient, 0, result); // SEND REQUEST TO SERVER
            //outToServer.flush();


            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            anonSocket.receive(packet);
            /*responseFromServer = new ArrayList<>(); // GET RESPONSE FROM SERVER
            while ((result = inFromServer.read(currentByte, 0, 1)) > -1) {
                responseFromServer.add(currentByte[0]);
                System.out.print((char) currentByte[0]);
            }
            fileArray = new byte[responseFromServer.size()];
            for (byte b : responseFromServer) {
                fileArray[i] = b;
                i++;
            }*/

            outToClient.write(buf, 0, responseFromServer.size());// SEND RESPONSE TO CLIENT
            outToClient.flush();

            closeStreams();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
