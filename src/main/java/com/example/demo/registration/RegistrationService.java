package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;



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

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if(confirmationToken.getConfirmedAt()!=null){
            throw new IllegalStateException(("token not found"));
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if(expiresAt.isBefore(LocalDateTime.now())){
            throw  new IllegalStateException(("token expired"));
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken
                        .getAppUser()
                                .getEmail());
        return "confirmed";
    }
}
