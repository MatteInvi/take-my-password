package myproject.takemypassword.take_my_password.controller.API;

import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;
import myproject.takemypassword.take_my_password.DTO.AuthResponse;
import myproject.takemypassword.take_my_password.DTO.LoginRequest;
import myproject.takemypassword.take_my_password.Service.EmailService;
import myproject.takemypassword.take_my_password.Service.JwtService;
import myproject.takemypassword.take_my_password.model.AuthToken;
import myproject.takemypassword.take_my_password.model.Role;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.AuthTokenRepository;
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
    EmailService emailService;

    
     public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            // Tenta di autenticare l'utente
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            // Se le credenziali sono errate, lancia un'eccezione
            return ResponseEntity.status(401).body("Credenziali non valide");
        }

        // Se l'autenticazione ha successo, carica i dettagli dell'utente
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

        //Generazione token JWT automatico dopo la registrazione
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
        return ResponseEntity.ok("Registrazione completata con successo");
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
            return ResponseEntity.status(400).body("Token scaduto");
        }

        //Prendo l'utente associato al token
        User user = authToken.get().getUser();
        user.setVerified(true);

        // Salvo l'utente aggiornato
        userRepository.save(user);
        return ResponseEntity.ok("Registrazione confermata con successo");
    }


}