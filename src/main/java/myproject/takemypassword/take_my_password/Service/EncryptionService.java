package myproject.takemypassword.take_my_password.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {
    


   
    private SecretKeySpec secretKey;

    public EncryptionService(@Value("${app.encryption.secret-key}") String secretKeyString) {
        // La chiave deve essere gestita in modo sicuro
        this.secretKey = new SecretKeySpec(secretKeyString.getBytes(), "AES");
    }

    // --- CRIPTAZIONE ---
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Gestione dell'errore appropriata
            throw new RuntimeException("Errore durante la crittografia", e);
        }
    }

    // --- DECRIPTAZIONE ---
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            // Gestione dell'errore appropriata
            throw new RuntimeException("Errore durante la decrittografia", e);
        }
    }
}