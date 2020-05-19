import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ListenTCPThread implements Runnable{
    ServerSocket welcomeSocket;
    ArrayList<String> peers;
    ArrayList<Thread> workers;
    Table table;
    int sessionID;
    DatagramSocket anonSocket;
    CryptoHelper ch;

    public ListenTCPThread(ServerSocket serverSocket, ArrayList<String> peerList, Table t, DatagramSocket udpSocket, CryptoHelper chelper){
        welcomeSocket = serverSocket;
        anonSocket = udpSocket;
        peers = peerList;
        table = t;
        workers = new ArrayList<>();
	ch = chelper;
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

            Thread t = new Thread(new WorkerTCP(clientSocket,peers.get(0), table, sessionID, anonSocket,ch));
            sessionID++;
            workers.add(t);
            t.start();
        }
    }
}
