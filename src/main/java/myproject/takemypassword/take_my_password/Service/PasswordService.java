package myproject.takemypassword.take_my_password.Service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class PasswordService {

    private static final String maiusc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String minusc = "abcdefghijklmnopqrstuvwxyz";
    private static final String numbers = "0123456789";
    private static final String simbols = "!@#$%^&*()-_=+[]{}<>?/";

    private static final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La lunghezza deve essere di almeno 8 caratteri");
        }

        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length;) {
            if (i < length) {
                int index = random.nextInt(maiusc.length());
                password.append(maiusc.charAt(index));
                i++;
            }
            if (i < length) {
                int index = random.nextInt(minusc.length());
                password.append(minusc.charAt(index));
                i++;
            }
            if (i < length) {
                int index = random.nextInt(numbers.length());
                password.append(numbers.charAt(index));
                i++;
            }
            if (i < length) {
                int index = random.nextInt(simbols.length());
                password.append(simbols.charAt(index));
                i++;
            }

        }

        return password.toString();
    }
}
