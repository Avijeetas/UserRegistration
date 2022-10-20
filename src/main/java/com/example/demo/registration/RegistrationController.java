package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping( value = {"/registration", "api/v1/registration", "/registration/"})
@AllArgsConstructor
public class RegistrationController {
    public final RegistrationService registrationService;
    @PostMapping
    public String register(@RequestBody RegistrationRequest req){
        try{
            registrationService.register(req);
            return "successful";
        } catch (Exception e){
            e.printStackTrace();
        }
        return "failure";

    }

    @GetMapping(path = "confirm")
    public String confirm(Model model, @RequestParam("token") String token){
        try{
            registrationService.confirmToken(token);


            return "redirect:/index";
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:/error";
    }


    @PostMapping(path = "sendEmailAgain")
    public String sendEmailAgain(@RequestParam("email") String email){

        return registrationService.TokenToSendAgain(email);
    }
}
