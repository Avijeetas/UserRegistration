package com.example.demo.appuser;

import com.example.demo.registration.token.ConfirmationTokenService;
import com.example.demo.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenService confirmationTokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).
                orElseThrow(()-> new UsernameNotFoundException(String.format(AppConstants.USER_NOT_FOUND_MSG, email)));
    }

    public AppUser signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail())
                .filter(AppUser::isEnabled).isPresent();

        if(userExists){
            throw new IllegalStateException(String.format(AppConstants.USER_ALREADY_EXISTS_MSG, appUser.getEmail()));
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        AppUser createdUser = appUserRepository.save(appUser);
        confirmationTokenService.confirmationTokenGenerate(appUser);
        return createdUser;
    }

    public void enableAppUser(String email){
        appUserRepository.enableAppUser(email);
    }
    public Optional<AppUser> findByEmail(String email){
        return appUserRepository.findByEmail(email).filter(AppUser::isEnabled);
    }
    public AppUser disableUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if(!userExists){
            throw new IllegalStateException(String.format(AppConstants.USER_ALREADY_EXISTS_MSG, appUser.getEmail()));
        }

        return appUserRepository.save(appUser);

    }

}
