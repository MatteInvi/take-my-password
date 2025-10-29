package myproject.takemypassword.take_my_password.Service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class PasswordService {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}<>?/";
    private static final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La lunghezza deve essere maggiore di zero.");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
