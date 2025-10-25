package myproject.takemypassword.take_my_password.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping("/dati")
public class DatiController {

    @Autowired
    DatiRepository datiRepository;

    @GetMapping
    public String index(Model model) {
        List<DatoAccesso> datiAccesso = datiRepository.findAll();
        model.addAttribute("dati", datiAccesso);

        return "archivio/index";
    }

    @GetMapping("/show")
    public String show(Model model) {

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
