package myproject.takemypassword.take_my_password.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    DatiRepository datiRepository;

    @GetMapping("/")
    public String home(){
        return "pages/homepage";
    }


}
