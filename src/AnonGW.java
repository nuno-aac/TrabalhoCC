import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class AnonGW {
    String clientInput;
    private ServerSocket welcomeSocket;
    private Socket goodbyeSocket;

    public AnonGW(){
        try {
            welcomeSocket = new ServerSocket(12345);
            goodbyeSocket = new Socket("127.0.0.1", 12345);
        } catch(IOException e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
    public void gwStart() throws Exception {

        while (welcomeSocket != null) {
            Socket clientSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(goodbyeSocket.getOutputStream());
            clientInput = inFromClient.readLine();
            outToServer.println("Received: " + clientInput);
        }

    }
}
