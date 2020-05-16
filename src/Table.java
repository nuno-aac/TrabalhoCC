import java.util.HashMap;

public class Table {
    private HashMap<Integer, TableEntry> table;

    public Table(){
        table = new HashMap<>();
    }

    public synchronized void addToTable(int key, TableEntry entry){
        table.put(key, entry);
    }

    public synchronized TableEntry getFromTable(int key){
        return table.get(key);
    }

    public synchronized void removeFromTable(int key){
        table.remove(key);
    }
}


