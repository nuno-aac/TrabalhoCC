import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ListenTCPThread implements Runnable{
    ServerSocket welcomeSocket;
    DatagramSocket anonSocket;
    ArrayList<String> peers;
    ArrayList<Thread> workers;
    Table table;
    int sessionID;

    public ListenTCPThread(ServerSocket serverSocket, ArrayList<String> peerList, Table t, DatagramSocket udpSocket){
        welcomeSocket = serverSocket;
        anonSocket = udpSocket;
        peers = peerList;
        table = t;
        workers = new ArrayList<>();
    }
    @Override
    public void run() {
        sessionID = 0;
        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = welcomeSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread t = new Thread(new WorkerTCP(clientSocket,peers.get(0), table, sessionID, anonSocket));
            sessionID++;
            workers.add(t);
            t.start();
        }
    }
}
