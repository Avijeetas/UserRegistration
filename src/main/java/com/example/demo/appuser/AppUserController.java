package com.example.demo.appuser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class AppUserController {

    @RequestMapping(path = "/home")
    public String login(Model model){
        return "index";
    }
}
