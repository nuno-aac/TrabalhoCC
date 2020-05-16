import java.net.InetAddress;
import java.net.Socket;

public class TableEntry {
    Socket clientSocket;
    InetAddress peer;
    int sessionID;
    int numPackets;

    public TableEntry(Socket clSocket, InetAddress peerAdress, int sessionId){
        clientSocket = clSocket;
        peer = peerAdress;
        sessionID = sessionId;
        numPackets = 0;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
