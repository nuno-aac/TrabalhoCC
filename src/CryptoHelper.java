import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoHelper {
    private static Cipher cipher = null;

    SecretKey secretKey;

    public CryptoHelper() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
        // keysize must be equal to 112 or 168 for this provider
        keyGenerator.init(168);
        this.secretKey = keyGenerator.generateKey();
        cipher = Cipher.getInstance("DESede");
    }

    public byte[] runEncrypts(byte[] msg) throws Exception{

        System.out.println("Plain Text Before Encryption: " + this.turnToString(msg));

        byte[] encryptedBytes = encrypt(msg, secretKey);

        System.out.println("Encrypted Text After Encryption: " + this.turnToString(encryptedBytes));

        return encryptedBytes;
    }

    public byte[] runDecrypts(byte[] encryptedBytes) throws Exception{

        byte[] decryptedBytes = decrypt(encryptedBytes, secretKey);
        System.out.println("Decrypted Text After Decryption: " + this.turnToString(decryptedBytes));
        return decryptedBytes;
    }

    static byte[] encrypt(byte[] plainTextByte, SecretKey secretKey)
            throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainTextByte);
        return encryptedBytes;
    }

    static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey)
            throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return decryptedBytes;
    }

    public String turnToString(byte[] message){
        String r = "";
        for(int i = 0; i < message.length; i++){
            r += (char) message[i];
        }
        return r;
    }

    public byte[] turnToByte(String message){
        byte[] r = new byte[message.length()];
        for(int i = 0; i < message.length(); i++){
            r[i] = (byte) message.charAt(i);
        }
        return r;
    }

}