import java.io.Serializable;

public class AnonPacket implements Serializable {
    byte[] data;
    int numPacket;
    int sourceSessionID;
    int destSessionID;

    public AnonPacket(byte[] packetData, int sourceSession, Integer destSession, int num){
        data = packetData;
        sourceSessionID = sourceSession;
        destSessionID = destSession;
        numPacket = num;
    }

    public byte[] getData() {
        return data;
    }

    public int getDestSessionID() {
        return destSessionID;
    }

    public int getSourceSessionID() {
        return sourceSessionID;
    }

    public int getNumPacket() {
        return numPacket;
    }
}
