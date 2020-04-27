import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        AnonGW anongw = new AnonGW();
        try{
            anongw.gwStart();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}