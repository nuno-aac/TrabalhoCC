import java.io.IOException;
import java.util.ArrayList;

public class Main {
    //  TO DO
    //  CRIAR COMPARADOR NA TABELA
    //  CRIAR HEADER
    //  EXTRA: ORDEM
    //  EXTRA: INTEGRIDADE
    public static void main(String[] args) {
        AnonGW anongw = new AnonGW(args);
        try{
            anongw.gwStart();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}