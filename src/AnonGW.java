import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class AnonGW {
    private ServerSocket welcomeSocket;
    private Socket goodbyeSocket;
    private String targetServer;
    ArrayList<String> peers;
    ArrayList<Thread> workers;
    private static final int PORT_NUM = 80;

    boolean parseArgs(String[] args){
        targetServer = null;
        peers = new ArrayList<>();
        for(int i = 1; i < args.length; i++){
            switch(args[i]){
                case "-sv" :
                    targetServer = args[i+1];
                    i++;
                    break;
                case "-peers" :
                    while(!args[i + 1].equals("-sv") && !args[i + 1].equals("-peers")){
                        peers.add(args[i+1]);
                        i++;
                    }
                    break;
                default:
                    System.out.println("Argumento desconhecido: " + args[i]);
            }
        }
        return (targetServer != null && peers.size() != 0);
    }

    public AnonGW(String[] args){
        if(parseArgs(args)) {
            try {
                welcomeSocket = new ServerSocket(PORT_NUM);
                goodbyeSocket = new Socket(targetServer, PORT_NUM);
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        } else {
            System.out.println("Argumentos inválidos!");
        }
    }
    public void gwStart() throws Exception {
        while (welcomeSocket != null) {
            Socket clientSocket = welcomeSocket.accept();
            Thread t = new Thread(new Worker(clientSocket, goodbyeSocket));
            workers.add(t);
            t.start();
        }
    }
}