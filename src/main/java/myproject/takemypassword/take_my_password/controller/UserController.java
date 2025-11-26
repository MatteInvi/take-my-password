package myproject.takemypassword.take_my_password.controller;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import myproject.takemypassword.take_my_password.model.Role;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.RoleRepository;
import myproject.takemypassword.take_my_password.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/show/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Optional<User> utente = userRepository.findById(id);
        model.addAttribute("utente", utente.get());
        return "utenti/show";
    }

    @GetMapping("/register")
    public String register(Model model) {

         User utente = new User();
         model.addAttribute("utente", utente);
         return "utenti/create";


    }

    @PostMapping("/register")
    public String save(@Valid @ModelAttribute("utente") User formUser, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (userRepository.existsByEmail(formUser.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email già registrata!");
            return "utenti/create";
        }

        if (bindingResult.hasErrors()) {
            return "utenti/create";
        }

        Role roleUser = new Role();

        for (Role role : roleRepository.findAll()) {
            if (role.getName().equals("USER")) {
                roleUser = role;
            }
        }
        formUser.setRoles(Set.of(roleUser));
        formUser.setPassword(passwordEncoder.encode(formUser.getPassword()));
        userRepository.save(formUser);
        redirectAttributes.addFlashAttribute("success", "Registrazione avvenuta con successo!");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {            
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")
                        || userOptional.get().getEmail().equals(authentication.getName())) {
                    model.addAttribute("utente", userOptional.get());
                    return "utenti/edit";
                }

            }
        } else if(!userOptional.isPresent()) {
            model.addAttribute("error", "Utente non trovato!");
            return "pages/error";
        }

        model.addAttribute("error", "Non sei autorizzato a vedere questa pagina!");
        return "pages/error";

    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("utente") User formUser, BindingResult bindingResult, @PathVariable Integer id, RedirectAttributes redirectAttributes, Authentication authentication, Model model){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {            
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")
                        || userOptional.get().getEmail().equals(authentication.getName())) {
                    if (bindingResult.hasErrors()){
                        return "utenti/edit";
                    }
                    formUser.setRoles(userOptional.get().getRoles());
                    formUser.setPassword(passwordEncoder.encode(userOptional.get().getPassword()));
                    userRepository.save(formUser);

                    redirectAttributes.addFlashAttribute("success", "Dati modificati con successo!");
                    return "redirect:/user/show/" + userOptional.get().getId() ;
                }

            }
        } else if(!userOptional.isPresent()) {
            model.addAttribute("error", "Utente non trovato!");
            return "pages/error";
        }

        model.addAttribute("error", "Non sei autorizzato a vedere questa pagina!");
        return "pages/error";


        

    }
}
