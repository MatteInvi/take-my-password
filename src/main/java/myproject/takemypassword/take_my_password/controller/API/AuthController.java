package myproject.takemypassword.take_my_password.controller.API;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import myproject.takemypassword.take_my_password.DTO.AuthResponse;
import myproject.takemypassword.take_my_password.DTO.LoginRequest;
import myproject.takemypassword.take_my_password.DTO.ResetPasswordDTO;
import myproject.takemypassword.take_my_password.Service.EmailService;
import myproject.takemypassword.take_my_password.Service.JwtService;
import myproject.takemypassword.take_my_password.Service.PasswordResetService;
import myproject.takemypassword.take_my_password.model.AuthToken;
import myproject.takemypassword.take_my_password.model.ResetToken;
import myproject.takemypassword.take_my_password.model.Role;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.AuthTokenRepository;
import myproject.takemypassword.take_my_password.repository.ResetTokenRepository;
import myproject.takemypassword.take_my_password.repository.RoleRepository;
import myproject.takemypassword.take_my_password.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthTokenRepository authTokenRepository;

    @Autowired
    ResetTokenRepository resetTokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordResetService passwordResetService;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            // Tenta di autenticare l'utente
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            // Se le credenziali sono errate, lancia un'eccezione
            response.put("error", "Credenziali non valide");
            return ResponseEntity.status(401).body(response);
        }

        // Se l'autenticazione ha successo, carica i dettagli dell'utente
        if (!userRepository.findByEmail(loginRequest.username()).get().isVerified()) {
            response.put("error", "Email non verificata");
            return ResponseEntity.status(401).body(response);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.username());

        // Genera il token JWT
        final String jwt = jwtService.generateToken(userDetails);

        // Restituisci il token nella risposta
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User registerRequest, BindingResult bindingResult) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(400).body("Email già registrata");
        }
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(400).body("Dati di registrazione non validi");
        }

        Role roleUser = new Role();
        for (Role role : roleRepository.findAll()) {
            if (role.getName().equals("USER")) {
                roleUser = role;
            }
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        registerRequest.setRoles(Set.of(roleUser));
        userRepository.save(registerRequest);

        // Generazione token JWT automatico dopo la registrazione
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setUser(registerRequest);
        // Imposta la data di scadenza del token (ad esempio, 24 ore da ora)
        authToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        // Salva il token nel database
        authTokenRepository.save(authToken);

        // Inviamo mail all'utente passando i dati del form compilato(per recuperare la
        // mail) e il token generato
        try {
            emailService.sendVerificationEmail(registerRequest, authToken);
        } catch (Exception e) {
            System.err.println(e);
        }
        return ResponseEntity.status(200).body("Registrazione completata con successo");
    }

    // Sezione di conferma registrazione
    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {

        // Andiamo a prendere il token dalla repository seguendo il link inviato
        // all'utente
        Optional<AuthToken> authToken = authTokenRepository.findByToken(token);

        // Se non è scaduto(24h) passiamo a prendere l'utente associato al token e
        // settare il suo stato come verificato
        if (authToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            authTokenRepository.deleteByUserId(authToken.get().getUser().getId());
            return ResponseEntity.status(400).body("Token scaduto");
        }

        // Prendo l'utente associato al token
        User user = authToken.get().getUser();
        user.setVerified(true);
        authTokenRepository.deleteByUserId(authToken.get().getUser().getId());

        // Salvo l'utente aggiornato
        userRepository.save(user);
        return ResponseEntity.ok("Registrazione confermata con successo");
    }

    // Sezione di reset password
    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body("Email non trovata");
        }

        User user = userOptional.get();

        ResetToken token = passwordResetService.generateResetToken(user);

        // Invia email di reset password
        try {
            emailService.sendPasswordResetEmail(user, token);
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(500).body("Errore nell'invio dell'email");
        }

        return ResponseEntity.ok("Email di reset password inviata con successo");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmResetPassword(@RequestParam("token") String token,
            @RequestBody ResetPasswordDTO requestBody) {
        Optional<ResetToken> resetTokenOptional = resetTokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty()) {
            return ResponseEntity.status(400).body("Token non valido");
        }

        ResetToken resetToken = resetTokenOptional.get();

        // Verifica se il token è scaduto
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token scaduto");
        }

        String newPassword = requestBody.getNewPassword();

        // Aggiorna la password dell'utente
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Rimuovi il token di reset password dopo l'uso
        resetTokenRepository.deleteByUserId(user.getId());

        return ResponseEntity.ok("Password resettata con successo");
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String baererToken,
            @Valid @RequestBody User updateRequest, BindingResult bindingResult) {

        if (baererToken == null || !baererToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token mancante o non valido");
        }

        String token = baererToken.substring(7);

        if (token.isEmpty() || jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(401).body("Sessione scaduto o non autorizzata");

        }

        try {

            String email = jwtService.extractUsername(token);

            Optional<User> existingUserOptinal = userRepository.findByEmail(email);

            User existingUser = existingUserOptinal.get();

            if (!existingUser.getId().equals(updateRequest.getId())) {
                return ResponseEntity.status(403).body("Non hai i permessi per modificare l'utente");
            }

            if (!existingUser.getEmail().equals(updateRequest.getEmail()) &&
                    userRepository.existsByEmail(updateRequest.getEmail())) {
                return ResponseEntity.status(400).body("Email già occupata da un altro utente");
            }

            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(400).body("Dati non validi");
            }

            existingUser.setName(updateRequest.getName());
            existingUser.setSurname(updateRequest.getSurname());
            existingUser.setEmail(updateRequest.getEmail());
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));

            userRepository.save(existingUser);

            return ResponseEntity.ok("Profilo aggiornato con successo");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token non valido");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String bearerToken) {

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token mancante");
        }

        String token = bearerToken.substring(7);

        try {

            String email = jwtService.extractUsername(token);

            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(401).body("Token scaduto");
            }

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(404).body("Utente non trovato");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token non valdio");
        }

    }

}