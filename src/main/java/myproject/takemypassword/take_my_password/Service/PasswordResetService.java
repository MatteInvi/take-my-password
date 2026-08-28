package myproject.takemypassword.take_my_password.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import myproject.takemypassword.take_my_password.model.ResetToken;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.ResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

final ResetTokenRepository resetTokenRepository;

    PasswordResetService(ResetTokenRepository resetTokenRepository) {
        this.resetTokenRepository = resetTokenRepository;
    }

    @Transactional
    public ResetToken generateResetToken(User user) {


        // 1️⃣ Elimina eventuale token precedente
        try{

         resetTokenRepository.deleteByUserId(user.getId());
        } catch (Exception e){
           new Error("Errore durante la cancellazione del token precedente: " + e.getMessage());
        }


        // 2️⃣ Genera un nuovo token
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        // 3️⃣ Salva e ritorna
        return resetTokenRepository.save(resetToken);
    }
}
