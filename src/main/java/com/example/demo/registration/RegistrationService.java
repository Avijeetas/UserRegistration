package com.example.demo.registration;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private EmailValidator emailValidator;
    public String register(RegistrationRequest req) {

        boolean isValidEmail=emailValidator.
                test(req.getEmail());
        if(!isValidEmail)   throw new IllegalStateException(("email not found"));

        return  "works";
    }
}
