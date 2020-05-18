import java.net.InetAddress;
import java.net.Socket;

public class TableEntry {
    Socket clientSocket;
    InetAddress peer;
    int sessionID;
    int numPackets;
    PacketStorage packets;

    public TableEntry(Socket clSocket, InetAddress peerAdress, int sessionId){
        clientSocket = clSocket;
        peer = peerAdress;
        sessionID = sessionId;
        numPackets = 0;
        packets = new PacketStorage();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public synchronized void addPacket(AnonPacket p){
        packets.addPacket(p);
    }

    public synchronized PacketStorage getPackets(){
        return packets;
    }
}
