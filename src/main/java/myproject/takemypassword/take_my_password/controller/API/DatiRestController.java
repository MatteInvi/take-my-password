package myproject.takemypassword.take_my_password.controller.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.DatiRepository;
import myproject.takemypassword.take_my_password.repository.UserRepository;

@RestController
@RequestMapping("/api/archive")
public class DatiRestController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatiRepository datiRepository;

    @GetMapping
    public ResponseEntity<?> index(Authentication authentication) {
        List<DatoAccesso> datiAccesso = new ArrayList<DatoAccesso>();
        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());

        datiAccesso = datiRepository.findByUser(userLogged.get());

        return ResponseEntity.ok(datiAccesso);
    }
}
