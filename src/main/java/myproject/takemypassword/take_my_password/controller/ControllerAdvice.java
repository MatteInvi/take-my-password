package myproject.takemypassword.take_my_password.controller;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.UserRepository;



@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @Autowired
    UserRepository userRepository;

    @ModelAttribute
    public void addLoggedUser(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> utenteLoggato = userRepository.findByEmail(authentication.getName());
            model.addAttribute("userLogged", utenteLoggato.get());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex, 
            WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getStatusCode());
        body.put("message", ex.getReason());

        // Restituisce il corpo con lo status code corretto definito nell'eccezione (es. 404 NOT FOUND)
        return new ResponseEntity<>(body, ex.getStatusCode());
    }



}
