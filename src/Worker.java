import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Worker implements Runnable {
    Socket clientSocket, serverSocket;
    ArrayList<Byte> requestFromClient, responseFromServer;
    //TCP
    DataInputStream inFromClient, inFromServer;
    DataOutputStream outToServer, outToClient;
    //UDP

    Worker(Socket clSocket, Socket svSocket){
        clientSocket = clSocket;
        serverSocket = svSocket;
        try {
            inFromClient = new DataInputStream(clientSocket.getInputStream());
            inFromServer = new DataInputStream(serverSocket.getInputStream());
            outToServer = new DataOutputStream(serverSocket.getOutputStream());
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        int result, i;
        byte[] currentByte = new byte[1];
        byte[] bytesFromClient = new byte[1024];
        byte[] fileArray;
        try {
            result = inFromClient.read(bytesFromClient, 0, 1024); // GET REQUEST FROM CLIENT
            for (int j = 0; j < result; j++) {
                System.out.print((char) bytesFromClient[j]);
            }
            outToServer.write(bytesFromClient, 0, result); // SEND REQUEST TO SERVER
            outToServer.flush();
            i = 0;// SEND REQUEST TO SERVER
            responseFromServer = new ArrayList<>(); // GET RESPONSE FROM SERVER
            while ((result = inFromServer.read(currentByte, 0, 1)) > -1) {
                responseFromServer.add(currentByte[0]);
                System.out.print((char) currentByte[0]);
            }
            fileArray = new byte[responseFromServer.size()];
            for (byte b : responseFromServer) {
                fileArray[i] = b;
                i++;
            }
            outToClient.write(fileArray, 0, responseFromServer.size());/// SEND RESPONSE TO CLIENT
            outToClient.flush();
            inFromClient.close();
            outToServer.close();
            inFromServer.close();
            outToClient.close();
            clientSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
