package myproject.takemypassword.take_my_password.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    DatiRepository datiRepository;

    @GetMapping("/")
    public String search(Model model, @RequestParam(required = false) String query) {
        List<DatoAccesso> datiAccesso = new ArrayList();
        if (query != null && !query.isEmpty()) {
            datiAccesso = datiRepository.findByUsernameContainingIgnoreCase(query);
        }
        model.addAttribute("dati", datiAccesso);

        return "archivio/search";
    }

}
