import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class AnonGW {
    private ServerSocket welcomeSocket;
    private String targetServer;
    ArrayList<String> peers;
    Table table;
    private static final int PORT_NUM = 80;

    private boolean parseArgs(String[] args){
        targetServer = null;
        peers = new ArrayList<>();
        for(int i = 0; i < args.length; i++){
            switch(args[i]){
                case "-sv" :
                    System.out.println("Found -sv it's " + args[i+1]);
                    targetServer = args[i+1];
                    i ++;
                    break;
                case "-peers" :
                    while(i+1 != args.length && !args[i + 1].equals("-sv") && !args[i + 1].equals("-peers")){
                        System.out.println("Found -peer it's " + args[i+1]);
                        peers.add(args[i+1]);
                        i++;
                    }
                    break;
                default:
                    System.out.println("Argumento desconhecido: " + args[i]);
                    break;
            }
        }
        System.out.println("Target Server: > " + targetServer + " <");
        System.out.println("Peers: > " + peers + " <");
        return (targetServer != null && peers.size() != 0);
    }

    public AnonGW(String[] args){
        if(parseArgs(args)) {
            try {
                welcomeSocket = new ServerSocket(PORT_NUM);
                table = new Table();
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        } else {
            System.out.println("Argumentos inválidos!");
        }
    }
    public void gwStart() throws Exception {
        DatagramSocket anonSocket = new DatagramSocket(6666);
        ListenTCPThread tcp = new ListenTCPThread(welcomeSocket,peers,table,anonSocket);
        ListenUDPThread udp = new ListenUDPThread(targetServer,table,anonSocket);
        Thread tcpThread = new Thread(tcp);
        Thread udpThread = new Thread(udp);
        tcpThread.start();
        udpThread.start();
    }
}