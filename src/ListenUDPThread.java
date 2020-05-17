import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ListenUDPThread implements Runnable{
    String serverSocket;
    byte buf[];
    Table table;
    ArrayList<Thread> workers;

    public ListenUDPThread(String targetServer, Table t){
        serverSocket = targetServer;
        table = t;
        buf = new byte[1024];
        workers = new ArrayList<>();
    }
    @Override
    public void run() {
        while(true) {
            try {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                DatagramSocket anonSocket = new DatagramSocket(6666);
                anonSocket.receive(dp);

                Thread t = new Thread(new WorkerUDP(serverSocket, new DatagramSocket(dp.getSocketAddress()), dp, table));
                workers.add(t);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }
}
