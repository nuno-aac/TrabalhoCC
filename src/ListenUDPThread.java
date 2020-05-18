import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ListenUDPThread implements Runnable{
    String serverSocket;
    byte buf[];
    Table table;
    ArrayList<Thread> workers;
    DatagramSocket anonSocket;

    public ListenUDPThread(String targetServer, Table t, DatagramSocket udpSocket){
        serverSocket = targetServer;
        table = t;
        anonSocket = udpSocket;
        buf = new byte[1024];
        workers = new ArrayList<>();
    }
    @Override
    public void run() {
	DatagramPacket dp;
        while(true) {
            try {
                dp = new DatagramPacket(buf, buf.length);
                anonSocket.receive(dp);
		        System.out.println("packet recebido");

                Thread t = new Thread(new WorkerUDP(serverSocket, anonSocket, dp, table));
                workers.add(t);
                t.start();
		buf = new byte[1024];
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }
}
