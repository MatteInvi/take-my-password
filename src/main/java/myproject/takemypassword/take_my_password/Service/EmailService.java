package myproject.takemypassword.take_my_password.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.mail.internet.MimeMessage;
import myproject.takemypassword.take_my_password.model.AuthToken;
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
            String confirmationUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/auth/confirm-email")
                    .queryParam("token", token.getToken())
                    .toUriString();

            helper.setTo(user.getEmail());
            helper.setSubject("Conferma la tua email");
            String html = String.format(
                    """
                            <html>

                            <body style="background-color: rosybrown; padding:20px;">
                                <main style="margin:0 auto; width: 50%%; position: relative; top: 50%%;">
                                    <h1 style="">Conferma la tua registrazione</h1>
                                    <p>Clicca sul seguente link per confermare la registrazione a MyEvents</p>
                                    <a style="background-color: royalblue; text-align: center; color: black; font-size: x-large; border-radius: 10px; padding: 5px; text-decoration: none; display: block; width: 30%%;"
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
}
