package myproject.takemypassword.take_my_password.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import myproject.takemypassword.take_my_password.repository.AuthTokenRepository;
import myproject.takemypassword.take_my_password.repository.ResetTokenRepository;

import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {

    private final ResetTokenRepository ResetTokenRepository;

    private final AuthTokenRepository authTokenRepository;

    public TokenCleanupTask(ResetTokenRepository ResetTokenRepository, AuthTokenRepository authTokenRepository) {
        this.ResetTokenRepository = ResetTokenRepository;
        this.authTokenRepository = authTokenRepository;
    }

   
    @Scheduled(cron = "*/30 * * * * *")
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCountResetToken = ResetTokenRepository.deleteByExpiryDateBefore(now);
        int deletedCountAuthToken = authTokenRepository.deleteByExpiryDateBefore(now);
        System.out.println("Rimossi " + deletedCountAuthToken + " token di autenticazione scaduti.");
        System.out.println("Rimossi " + deletedCountResetToken + " token di reset scaduti.");
    }
}