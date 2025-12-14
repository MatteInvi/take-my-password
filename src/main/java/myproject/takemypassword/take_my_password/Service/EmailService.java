package myproject.takemypassword.take_my_password.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import myproject.takemypassword.take_my_password.model.AuthToken;
import myproject.takemypassword.take_my_password.model.ResetToken;
import myproject.takemypassword.take_my_password.model.User;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    // Email di invio token di verifica
    public void sendVerificationEmail(User user, AuthToken token) {
        // Implementazione dell'invio email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String confirmationUrl = "https://take-my-password-react-app.onrender.com/confirm?token=" + token.getToken();

            helper.setTo(user.getEmail());
            helper.setSubject("Conferma la tua email");
            String html = String.format(
                    """
                            <html>

                            <body style="background-color: lightgray; padding:20px;">
                                <main style="margin:0 auto; width: 50%%; position: relative; top: 50%%;">
                                    <h1 style="">Conferma la tua registrazione</h1>
                                    <p>Clicca sul seguente link per confermare la registrazione a Take My Password</p>
                                    <a style="background-color: orange; text-align: center; color: black; font-size: x-large; border-radius: 10px; padding: 5px; text-decoration: none; display: block; width: 30%%;"
                                        href="%s">Conferma</a>
                                </main>
                            </body>

                            </html>

                                        """,
                    confirmationUrl);

            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    // Email di reset password
    public void sendPasswordResetEmail(User user, ResetToken resetToken) {
        // Implementazione dell'invio email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String resetUrl = "https://take-my-password-react-app.onrender.com/reset-password/confirm?token=" + resetToken.getToken();

            helper.setTo(user.getEmail());
            helper.setSubject("Reset della tua password");
            String html = String.format(
                    """
                            <html>

                            <body style="background-color: lightgray; padding:20px;">
                                <main style="margin:0 auto; width: 50%%; position: relative; top: 50%%;">
                                    <h1 style="">Reset della tua password</h1>
                                    <p>Clicca sul seguente link per resettare la tua password</p>
                                    <a style="background-color: orange; text-align: center; color: black; font-size: x-large; border-radius: 10px; padding: 5px; text-decoration: none; display: block; width: 30%%;"
                                        href="%s">Reset Password</a>
                                </main>
                            </body>

                            </html>

                                        """,
                    resetUrl);

            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
