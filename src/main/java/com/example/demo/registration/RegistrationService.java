package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;



    public String register(RegistrationRequest req) {

        boolean isValidEmail=emailValidator.
                test(req.getEmail());
        if(!isValidEmail)   throw new IllegalStateException(("email not found"));

        return appUserService.signUpUser(
                new AppUser(
                        req.getFirstName(),
                        req.getLastName(),
                        req.getEmail(),
                        req.getPassword(),
                        AppUserRole.USER

                )
        ) ;
    }
}
