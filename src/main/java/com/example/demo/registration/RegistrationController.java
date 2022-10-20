package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    public final RegistrationService registrationService;
    @PostMapping
    public String register(@RequestBody RegistrationRequest req){
        return registrationService.register(req);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }

    @PostMapping(path = "sendEmailAgain")
    public String sendEmailAgain(@RequestParam("email") String email){

        return registrationService.TokenToSendAgain(email);
    }
}
