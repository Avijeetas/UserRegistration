package com.example.demo.registration.token;

import com.example.demo.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }
    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token){
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
    public int expireTokenAtByToken(String token){
        return confirmationTokenRepository.expireTokenAtByToken(token, LocalDateTime.now());
    }
    public int expireTokenAtByUserId(UUID uId){
        return confirmationTokenRepository.expireTokenAtByUserId(uId, LocalDateTime.now());
    }
    public String confirmationTokenGenerate(AppUser appUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken= new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        saveConfirmationToken(
                confirmationToken);

        return token;
    }
}
