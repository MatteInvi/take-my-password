package myproject.takemypassword.take_my_password.controller.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import myproject.takemypassword.take_my_password.Service.EncryptionService;
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

        @Autowired
        EncryptionService encryptionService;

        @GetMapping
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
        public ResponseEntity<Page<DatoAccesso>> index(@PageableDefault(size = 10) Pageable pageable,
                        @RequestParam(required = false) String searchTerm,
                        Authentication authentication) {

                // Prendo l'utente loggato
                User userLogged = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Errore: Utente autenticato non trovato. Si prega di effettuare nuovamente il login."));

                // Prendo la pagina di dati dell'utente loggato
                Page<DatoAccesso> datiPage;

                // 1. Logica Condizionale: Esegui la ricerca se searchTerm è presente
                if (searchTerm != null && !searchTerm.isBlank()) {
                        datiPage = datiRepository.findByUserAndPlatformContainingIgnoreCase(userLogged, searchTerm,
                                        pageable);
                } else {
                        datiPage = datiRepository.findByUser(userLogged, pageable);
                }
                // Decripto username e password di ogni dato
                Page<DatoAccesso> decryptedPage = datiPage.map(datoAccesso -> {
                        datoAccesso.setUsername(encryptionService.decrypt(datoAccesso.getUsername()));
                        datoAccesso.setPassword(encryptionService.decrypt(datoAccesso.getPassword()));
                        datoAccesso.setAnnotation(datoAccesso.getAnnotation());
                        datoAccesso.setPlatform(datoAccesso.getPlatform());
                        datoAccesso.setId(datoAccesso.getId());
                        datoAccesso.setUser(null);
                        return datoAccesso;
                });

                // Ritorno il json con la lista
                return ResponseEntity.ok(decryptedPage);
        }

        @PostMapping("/create")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
        public ResponseEntity<DatoAccesso> create(@Valid @RequestBody DatoAccesso nuoveCredenziali,
                        Authentication authentication) {

                // Prendo l'utente loggato
                User userLogged = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Errore: Utente autenticato non trovato. Si prega di effettuare nuovamente il login."));

                // Setto lo user della credenziale all'utente loggato
                nuoveCredenziali.setUser(userLogged);

                // Crypto username e password
                nuoveCredenziali.setPassword(encryptionService.encrypt(nuoveCredenziali.getPassword()));
                nuoveCredenziali.setUsername(encryptionService.encrypt(nuoveCredenziali.getUsername()));

                // Salvo la nuova credenziale
                DatoAccesso credenzialiSalvate = datiRepository.save(nuoveCredenziali);

                // Ritorno status e json con la nuova credenziale
                return new ResponseEntity<>(credenzialiSalvate, HttpStatus.CREATED);

        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
        public ResponseEntity<DatoAccesso> update(@PathVariable Integer id,
                        @Valid @RequestBody DatoAccesso credenzialiModificate, Authentication authentication) {

                // Prendo l'utente loggato
                User userLogged = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Errore: Utente autenticato non trovato. Si prega di effettuare nuovamente il login."));

                // Controllo se la credenziale esiste
                DatoAccesso credenziale = datiRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Credenziali con id " + id + " non trovata!"));

                // Controllo che sia assegnata all'utente loggato
                if (!credenziale.getUser().equals(userLogged)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "Non sei autorizzato a modificare questa credenziale!");
                }

                // Modifico i dati modificabili
                credenziale.setPassword(encryptionService.encrypt(credenzialiModificate.getPassword()));
                credenziale.setUsername(encryptionService.encrypt(credenzialiModificate.getUsername()));
                credenziale.setAnnotation(credenzialiModificate.getAnnotation());
                credenziale.setPlatform(credenzialiModificate.getPlatform());

                // Salvo con i nuovi dati
                DatoAccesso credenzialeModificata = datiRepository.save(credenziale);

                // Restituisco il json con le credenziali modificate
                return ResponseEntity.ok(credenzialeModificata);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
        public ResponseEntity<Void> delete(
                        @PathVariable Integer id, Authentication authentication) {
                // Prendo l'utente loggato
                User userLogged = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Errore: Utente autenticato non trovato. Si prega di effettuare nuovamente il login."));

                // Controllo se la credenziale esiste tramite l'id
                DatoAccesso credenziale = datiRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Credenziali con ID " + id + " non trovata!"));

                // Controllo se la credenziale è assegnata a l'utente loggato
                if (!credenziale.getUser().equals(userLogged)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Non sei autorizzato ad eliminare questa credenziale.");
                }

                // Elimino la credenziale
                datiRepository.delete(credenziale);

                // Ritorno no_content
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

}
