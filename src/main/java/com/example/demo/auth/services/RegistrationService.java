//package com.example.demo.auth.services;
//
//import com.example.demo.appuser.AppUser;
//import com.example.demo.appuser.AppUserRole;
//import com.example.demo.appuser.AppUserService;
//import com.example.demo.registration.EmailValidator;
//import com.example.demo.registration.RegistrationRequest;
//import com.example.demo.registration.email.EmailService;
//import com.example.demo.registration.token.ConfirmationToken;
//import com.example.demo.registration.token.ConfirmationTokenService;
//import com.example.demo.utils.AppConstants;
//import com.example.demo.utils.EmailUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class RegistrationService  {
//
//    private final AppUserService appUserService;
//    private final EmailValidator emailValidator;
//    private final ConfirmationTokenService confirmationTokenService;
//
//    private final EmailService emailService;
//    private final EmailUtils emailUtils;
//    @Value("${app.server}/${server.port}")
//    private String baseUrl;
//
//    @Transactional
//    public AppUser register(RegistrationRequest req) {
//        validateEmail(req.getEmail());
//        AppUser newUser = createUser(req);
//        sendRegistrationEmail(newUser);
//        return newUser;
//    }
//
//    private void validateEmail(String email) {
//        String emailInLC = email.toLowerCase();
//        if (!emailValidator.test(emailInLC)) {
//            throw new IllegalStateException("Email is not valid");
//        }
//    }
//
//    private AppUser createUser(RegistrationRequest req) {
//        String emailInLC = req.getEmail().toLowerCase();
//        AppUser appUser = new AppUser(
//                req.getFirstName(),
//                req.getLastName(),
//                emailInLC,
//                req.getPassword(),
//                AppUserRole.USER,
//                Boolean.FALSE,
//                Boolean.FALSE
//        );
//        return appUserService.signUpUser(appUser);
//    }
//
//    private void sendRegistrationEmail(AppUser newUser) {
//        String token = TokenToSend(newUser);
//        String firstName = newUser.getFirstName();
//        sendEmail(firstName, newUser.getEmail(), token);
//    }
//
//    public void confirmToken(String token) {
//        ConfirmationToken confirmationToken = getConfirmationToken(token);
//        validateTokenNotConfirmed(confirmationToken);
//        validateTokenNotExpired(confirmationToken);
//        confirmationTokenService.setConfirmedAt(token);
//        enableAppUser(confirmationToken.getAppUser().getEmail());
//    }
//
//    private ConfirmationToken getConfirmationToken(String token) {
//        return confirmationTokenService.getToken(token)
//                .orElseThrow(() -> new IllegalStateException(AppConstants.TOKEN_NOT_FOUND));
//    }
//
//    private void validateTokenNotConfirmed(ConfirmationToken token) {
//        if (token.getConfirmedAt() != null) {
//            throw new IllegalStateException(AppConstants.TOKEN_ALREADY_CONFIRMED);
//        }
//    }
//
//    private void validateTokenNotExpired(ConfirmationToken token) {
//        LocalDateTime expiresAt = token.getExpiresAt();
//        if (expiresAt.isBefore(LocalDateTime.now())) {
//            throw new IllegalStateException(AppConstants.TOKEN_EXPIRED);
//        }
//    }
//
//    private void enableAppUser(String email) {
//        appUserService.enableAppUser(email);
//    }
//
//
//    private String TokenToSend(AppUser user) {
//        return confirmationTokenService.confirmationTokenGenerate(user);
//    }
//
//    public void TokenToSendAgain(String email) {
//        AppUser user = appUserService.findByEmail(email)
//                .orElseThrow(() -> new IllegalStateException(String.format(AppConstants.USER_ALREADY_EXISTS_MSG, email)));
//
//        String token = TokenToSend(user);
//        confirmationTokenService.expireTokenAtByUserId(user.getId());
//        sendEmail(user.getFirstName(), email, token);
//        log.info(String.format(AppConstants.EMAIL_SUCCESS_MSG, email));
//    }
//    private void sendEmail(String toFirstName, String email, String token) {
//        String link = String.format("%s/registration/confirm?token=%s",baseUrl,token);
//        String body = emailUtils.buildEmail(toFirstName, link);
//        emailService.send(email, body);
//        log.info(String.format(AppConstants.EMAIL_SUCCESS_MSG, email));
//    }
//
//    public void rollBack(RegistrationRequest reqUser) {
//        AppUser user = appUserService.findByEmail(reqUser.getEmail())
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                String.format(AppConstants.USER_ALREADY_EXISTS_MSG, reqUser.getEmail())));
//
//        user.setEnabled(Boolean.FALSE);
//        user.setLocked(Boolean.TRUE);
//        user = appUserService.disableUser(user);
//        confirmationTokenService.expireTokenAtByUserId(user.getId());
//        log.info("User {}, info have been rollback", user.getId());
//    }
//}
