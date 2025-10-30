package myproject.takemypassword.take_my_password.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import myproject.takemypassword.take_my_password.Service.PasswordService;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    PasswordService passwordService;

    @GetMapping
    public String home(){

        return "password/home";
    }


    @GetMapping("/generate")
    public String generatePassword(@RequestParam(required = false) int length, Model model) {
        
        try {
            String password = passwordService.generatePassword(length);
            model.addAttribute("password", password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "password/create";
    }
}