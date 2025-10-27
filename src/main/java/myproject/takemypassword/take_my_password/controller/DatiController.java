package myproject.takemypassword.take_my_password.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping("/archive")
public class DatiController {

    @Autowired
    DatiRepository datiRepository;

    @GetMapping
    public String home(Model model){

        return "archivio/home";
    }

    @GetMapping("/show/{id}")
    public String show(Model model, @PathVariable Integer id) {
       Optional<DatoAccesso> datoOptional = datiRepository.findById(id);
        model.addAttribute("dato",datoOptional.get());
        return "archivio/show";
    }

    @GetMapping("/create")
    public String create(Model model) {

        return "archivio/create";
    }

    @PostMapping("/create")
    public String save() {
        
        return "redirect:/dati";
    }

    @GetMapping("/edit/{id}")
    public String edit() {

        return "archivio/edit";
    }

    @PostMapping("/edti/{id}")
    public String update() {

        return "redirect:/index";
    }

    @PostMapping("delete/{id}")
    public String delete() {
        return "redirect:/index";
    }
}
