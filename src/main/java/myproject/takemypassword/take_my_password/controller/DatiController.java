package myproject.takemypassword.take_my_password.controller;


import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import jakarta.validation.Valid;
import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.model.User;
import myproject.takemypassword.take_my_password.repository.DatiRepository;
import myproject.takemypassword.take_my_password.repository.RoleRepository;
import myproject.takemypassword.take_my_password.repository.UserRepository;

@Controller
@RequestMapping("/archive")
public class DatiController {

    @Autowired
    DatiRepository datiRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String query,@RequestParam (defaultValue = "0") int page, Authentication authentication) {
        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());
        Page<DatoAccesso> datiAccesso;

        if (query != null && !query.isEmpty()) {
             datiAccesso = datiRepository.findByUserAndPlatformContainingIgnoreCase(userLogged.get(), query, org.springframework.data.domain.PageRequest.of(page, 10)); // 10 dati per pagina
        } else {
             datiAccesso = datiRepository.findByUser(userLogged.get(), org.springframework.data.domain.PageRequest.of(page, 10)); // 10 dati per pagina
                
        }

        model.addAttribute("pagina", datiAccesso);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", datiAccesso.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("dati", datiAccesso);

        return "archivio/home";
    }

    @GetMapping("/create")
    public String create(Model model) {
        DatoAccesso dato = new DatoAccesso();
        model.addAttribute("dato", dato);
        return "archivio/create";
    }

    @PostMapping("/create")
    public String save(@Valid @ModelAttribute("dato") DatoAccesso datoForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Authentication authentication) {
        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());
        if (bindingResult.hasErrors()) {
            return "archivio/create";
        }
        datoForm.setUser(userLogged.get());
        datiRepository.save(datoForm);
        redirectAttributes.addFlashAttribute("success", "Dato salvato con successo!");
        return "redirect:/archive";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, Authentication authentication) {

        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());
        DatoAccesso dato = datiRepository.findById(id).get();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN") || dato.getUser().equals(userLogged.get())) {
                model.addAttribute("dato", dato);
                return "archivio/edit";
            }

        }
        model.addAttribute("error", "Non sei autorizzato ad accedere a questa pagina!");
        return "pages/error";

    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute("dato") DatoAccesso datoForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, @PathVariable Integer id, Authentication authentication,
            Model model) {

        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());
        DatoAccesso dato = datiRepository.findById(id).get();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN") || dato.getUser().equals(userLogged.get())) {

                if (bindingResult.hasErrors()) {
                    return "archivio/edit";
                }
                datoForm.setUser(dato.getUser());
                datiRepository.save(datoForm);
                redirectAttributes.addFlashAttribute("success", "Dato modificato con successo!");
                return "redirect:/archive";

            }
        }

        model.addAttribute("error", "Non sei autorizzato a modificare questo dato!");
        return "pages/error";

    }

    @PostMapping("delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes,
            Authentication authentication, Model model) {
        Optional<User> userLogged = userRepository.findByEmail(authentication.getName());
        DatoAccesso dato = datiRepository.findById(id).get();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN") || dato.getUser().equals(userLogged.get())) {

                datiRepository.delete(dato);
                redirectAttributes.addFlashAttribute("success", "Dato eliminato con successo!");
                return "redirect:/archive";
            }
        }

        model.addAttribute("error", "Non sei autorizzato a eliminare questo dato!");
        return "pages/error";
    }
}
