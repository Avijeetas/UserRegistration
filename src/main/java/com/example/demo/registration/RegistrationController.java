package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Slf4j
@Controller
@RequestMapping( value = {"/registration", "api/v1/registration", "/registration/"})
@AllArgsConstructor
public class RegistrationController {
    public final RegistrationService registrationService;
    @PostMapping
    public ResponseEntity<AppUser> register(@RequestBody RegistrationRequest req){
        try{
            return  new ResponseEntity<>(registrationService.register(req), HttpStatus.CREATED);
        } catch (Exception e){
            log.warn("User registration failed {}",req);
            registrationService.rollBack(req);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping(path = "confirm")
    public String confirm(Model model, @RequestParam("token") String token){
        try{
            registrationService.confirmToken(token);


            return "redirect:/index";
        }
        catch (Exception e){
            log.info("Error in confirmToken {}", e.getMessage());
        }

        return "redirect:/error";
    }


    @PostMapping(path = "sendEmailAgain")
    public ResponseEntity<String> sendEmailAgain(@RequestParam String email) {
        registrationService.TokenToSendAgain(email);

        return ResponseEntity.ok(String.format(AppConstants.EMAIL_SUCCESS_MSG, email));
    }
}
