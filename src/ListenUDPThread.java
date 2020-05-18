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
	this.anonSocket = anonSocket;
    }
    @Override
    public void run() {
        while(true) {
            try {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                anonSocket.receive(dp);
		System.out.println("packet recebido");

                Thread t = new Thread(new WorkerUDP(serverSocket, anonSocket, dp, table));
                workers.add(t);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }
}
