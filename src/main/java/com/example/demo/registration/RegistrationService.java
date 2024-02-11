package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import com.example.demo.registration.email.EmailService;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import com.example.demo.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService  {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;

    private final EmailService emailService;
    private final EmailUtils emailUtils;
    @Value("${app.server}")
    private String baseUrl;

    @Transactional
    public AppUser register(RegistrationRequest req) {
        validateEmail(req.getEmail());
        AppUser newUser = createUser(req);
        sendRegistrationEmail(newUser);
        return newUser;
    }

    private void validateEmail(String email) {
        String emailInLC = email.toLowerCase();
        if (!emailValidator.test(emailInLC)) {
            throw new IllegalStateException("Email is not valid");
        }
    }

    private AppUser createUser(RegistrationRequest req) {
        String emailInLC = req.getEmail().toLowerCase();
        AppUser appUser = new AppUser(
                req.getFirstName(),
                req.getLastName(),
                emailInLC,
                req.getPassword(),
                AppUserRole.USER,
                Boolean.FALSE,
                Boolean.FALSE
        );
        return appUserService.signUpUser(appUser);
    }

    private void sendRegistrationEmail(AppUser newUser) {
        String token = TokenToSend(newUser);
        String firstName = newUser.getFirstName();
        sendEmail(firstName, newUser.getEmail(), token);
    }

    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = getConfirmationToken(token);
        validateTokenNotConfirmed(confirmationToken);
        validateTokenNotExpired(confirmationToken);
        confirmationTokenService.setConfirmedAt(token);
        enableAppUser(confirmationToken.getAppUser().getEmail());
    }

    private ConfirmationToken getConfirmationToken(String token) {
        return confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
    }

    private void validateTokenNotConfirmed(ConfirmationToken token) {
        if (token.getConfirmedAt() != null) {
            throw new IllegalStateException("Token already confirmed");
        }
    }

    private void validateTokenNotExpired(ConfirmationToken token) {
        LocalDateTime expiresAt = token.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }
    }

    private void enableAppUser(String email) {
        appUserService.enableAppUser(email);
    }


    private String TokenToSend(AppUser user) {
        return confirmationTokenService.confirmationTokenGenerate(user);
    }

    public void TokenToSendAgain(String email) {
        AppUser user = appUserService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String token = TokenToSend(user);
        confirmationTokenService.expireTokenAtByUserId(user.getId());
        sendEmail(user.getFirstName(), email, token);
        log.info("Email sent successfully to: {}", email);
    }
    private void sendEmail(String toFirstName, String email, String token) {
        String link = baseUrl+"/registration/confirm?token=" + token;
        String body = emailUtils.buildEmail(toFirstName, link);
        emailService.send(email, body);
        log.info("Email sent to {}, email address {}", toFirstName, email);
    }

    public void rollBack(RegistrationRequest reqUser) {
        AppUser user = appUserService.findByEmail(reqUser.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setEnabled(Boolean.FALSE);
        user.setLocked(Boolean.TRUE);
        user = appUserService.disableUser(user);
        confirmationTokenService.expireTokenAtByUserId(user.getId());
        log.info("User {}, info have been rollback", user.getId());
    }
}
