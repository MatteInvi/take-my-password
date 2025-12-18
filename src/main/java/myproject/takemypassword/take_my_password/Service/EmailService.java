package myproject.takemypassword.take_my_password.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import myproject.takemypassword.take_my_password.model.AuthToken;
import myproject.takemypassword.take_my_password.model.ResetToken;
import myproject.takemypassword.take_my_password.model.User;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    private final Resend resend;

    public EmailService(@Value("${resend.mail.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    // Email di invio token di verifica
    public void sendVerificationEmail(User user, AuthToken token) {
        // Implementazione dell'invio email

        String confirmationUrl = "https://take-my-password-react-app.onrender.com/confirm?token="
                + token.getToken();

        String html = String.format(
                """
                        <html>

                        <body style="background-color: lightgray; padding:20px;">
                            <main style="margin:0 auto; width: 50%%; position: relative; top: 50%%;">
                                <h1 style="">Conferma la tua registrazione</h1>
                                <p>Clicca sul seguente link per confermare la registrazione a Take My Password</p>
                                <a style="background-color: orange; text-align: center; color: black; font-size: x-large; border-radius: 10px; padding: 5px; text-decoration: none; display: block; width: 30%%; min-width:110px;"
                                    href="%s">Conferma</a>
                            </main>
                        </body>

                        </html>

                                    """,
                confirmationUrl);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("TakeMyPassword <takemypassword@matteoinvidia.it>")
                .to(user.getEmail())
                .subject("Conferma indirizzo email")
                .html(html.toString())
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    // Email di reset password
    public void sendPasswordResetEmail(User user, ResetToken resetToken) {

        // Implementazione dell'invio email
        String resetUrl = "https://take-my-password-react-app.onrender.com/reset-password/confirm?token="
                + resetToken.getToken();

        String html = String.format(
                """
                        <html>

                        <body style="background-color: lightgray; padding:20px;">
                            <main style="margin:0 auto; width: 50%%; position: relative; top: 50%%;">
                                <h1 style="">Reset della tua password</h1>
                                <p>Clicca sul seguente link per resettare la tua password</p>
                                <a style="background-color: orange; text-align: center; color: black; font-size: x-large; border-radius: 10px; padding: 5px; text-decoration: none; display: block; width: 30%%; min-width:110px;"
                                    href="%s">Reset Password</a>
                            </main>
                        </body>

                        </html>

                                    """,
                resetUrl);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("TakeMyPassword <takemypassword@matteoinvidia.it>")
                .to(user.getEmail())
                .subject("Reset della tua password")
                .html(html.toString())
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());

        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
