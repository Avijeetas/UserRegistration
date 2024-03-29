package com.example.demo.controllers;

import com.example.demo.*;
import com.example.demo.auth.dto.UserDto;
import com.example.demo.auth.entities.RefreshToken;
import com.example.demo.auth.entities.User;
import com.example.demo.auth.repositories.UserRepository;
import com.example.demo.auth.services.AuthService;
import com.example.demo.auth.services.RefreshTokenService;
import com.example.demo.auth.services.UserService;
import com.example.demo.utils.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.CacheRequest;
import java.util.List;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserRepository userRepository;
   // private final RegistrationService registrationService;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService
            ,UserService userService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("create")
    public ResponseEntity<Void> create(@RequestBody CreateRequest createRequest){
        authService.create(createRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("verify/{username}/{otp}")
    public ResponseEntity<Void> verify(@PathVariable String username, @PathVariable String otp){
        authService.verifyOtp(username, otp);
        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("refresh")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        return ResponseEntity
                .ok(authService.generateAuthResponse(user));

    }

    @PutMapping("all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> updateUserInfos(){
        return ResponseEntity
                .ok(userService.getAllEnabledUsers());
    }
    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteRecords(){
        userRepository.deleteAll();
        return ResponseEntity.ok("Done");
    }

    @GetMapping("validate")
    public ResponseEntity<Boolean> validateUser(@RequestParam String token) {
        boolean isValid = authService.validateUser(token);

        // Set appropriate HTTP status based on the validation result
        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(isValid);
    }
    @GetMapping("fetch")
    public ResponseEntity<UserDetails> fetchUser(@RequestParam String username) {
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                String.format(AppConstants.USER_NOT_FOUND_WITH_EMAIL, username)));
        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }
}
