package myproject.takemypassword.take_my_password.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import myproject.takemypassword.take_my_password.model.DatoAccesso;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping("/archive")
public class DatiController {

    @Autowired
    DatiRepository datiRepository;

    @GetMapping
    public String home(Model model) {

        return "archivio/home";
    }

    @GetMapping("/show/{id}")
    public String show(Model model, @PathVariable Integer id) {

        Optional<DatoAccesso> datoOptional = datiRepository.findById(id);

        model.addAttribute("dato", datoOptional.get());
        return "archivio/show";
    }

    @GetMapping("/create")
    public String create(Model model){
        DatoAccesso dato = new DatoAccesso();
        model.addAttribute("dato", dato);
        return "archivio/create";
    }

    @PostMapping("/create")
    public String save(@Valid @ModelAttribute ("dato") DatoAccesso datoForm, BindingResult bindingResult,RedirectAttributes redirectAttributes)  {
        if (bindingResult.hasErrors()) {
            return "archivio/create";
        }
        datiRepository.save(datoForm);
        redirectAttributes.addFlashAttribute("success", "Dato salvato con successo!");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        DatoAccesso dato = datiRepository.findById(id).get();
        model.addAttribute("dato", dato);
        return "archivio/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@Valid @ModelAttribute ("dato") DatoAccesso datoForm, BindingResult bindingResult,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){
            return "archivio/edit";
        }
        datiRepository.save(datoForm);
        redirectAttributes.addFlashAttribute("success", "Dato modificato con successo!");
        return "redirect:/";
    }

    @PostMapping("delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        datiRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Dato eliminato con successo!");
        return "redirect:/";
    }
}
