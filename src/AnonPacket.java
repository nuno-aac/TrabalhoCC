import java.io.Serializable;

public class AnonPacket implements Serializable {
    byte[] data;
    int numPacket;
    int sourceSessionID;
    int destSessionID;
    boolean isLast;

    public AnonPacket(byte[] packetData, Integer sourceSession, Integer destSession, int num, boolean last){
        data = packetData;
        sourceSessionID = sourceSession;
        destSessionID = destSession;
        numPacket = num;
        isLast = last;
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

    public boolean isLast(){
        return isLast;
    }
}
