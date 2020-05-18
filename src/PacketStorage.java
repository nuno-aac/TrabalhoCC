import java.util.HashMap;

public class PacketStorage {
    HashMap<Integer, AnonPacket> packetList;
    AnonPacket lastPacket;

    public PacketStorage(){
        packetList = new HashMap<>();
        lastPacket = null;
    }

    public synchronized boolean isFullyReceived(){
        if(lastPacket == null) return false;
        return lastPacket.getNumPacket() == packetList.size();
    }

    public synchronized void addPacket(AnonPacket packet){
        if(packet.isLast()) lastPacket = packet;
        else packetList.put(packet.getNumPacket(), packet);
    }

    public synchronized byte[] getData(){
        byte[] res = null;
        byte[] currentPacket;
        if(isFullyReceived()) {
            res = new byte[(packetList.size() + 1) * 512];
            for (int i = 0; i < packetList.size(); i++) {
                currentPacket = packetList.get(i).getData();
                for (int j = 0; j < currentPacket.length; j++) {
                    res[j + i * 512] = currentPacket[j];
                }
            }
            currentPacket = lastPacket.getData();
            for (int j = 0; j < currentPacket.length; j++) {
                res[j + packetList.size() * 512] = currentPacket[j];
            }
        }
        return res;
    }
}

