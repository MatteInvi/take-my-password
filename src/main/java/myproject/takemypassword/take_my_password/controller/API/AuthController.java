package myproject.takemypassword.take_my_password.controller.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import myproject.takemypassword.take_my_password.DTO.AuthResponse;
import myproject.takemypassword.take_my_password.DTO.LoginRequest;
import myproject.takemypassword.take_my_password.Service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtService jwtService;

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
}