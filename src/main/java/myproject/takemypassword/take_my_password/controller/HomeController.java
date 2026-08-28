package myproject.takemypassword.take_my_password.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import myproject.takemypassword.take_my_password.repository.DatiRepository;

@Controller
@RequestMapping
public class HomeController {

    private final DatiRepository datiRepository;

    public HomeController(DatiRepository datiRepository) {
        this.datiRepository = datiRepository;
    }

    @GetMapping("/")
    public String home(){
        return "pages/homepage";
    }

        @GetMapping("/login")
    public String login() {

        return "pages/login";
    }


}
