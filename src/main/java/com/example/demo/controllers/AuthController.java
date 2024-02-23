package com.example.demo.controllers;

import com.example.demo.AuthResponse;
import com.example.demo.LoginRequest;
import com.example.demo.RefreshTokenRequest;
import com.example.demo.RegisterRequest;
import com.example.demo.auth.dto.UserDto;
import com.example.demo.auth.entities.RefreshToken;
import com.example.demo.auth.entities.User;
import com.example.demo.auth.repositories.UserRepository;
import com.example.demo.auth.services.AuthService;
import com.example.demo.auth.services.RefreshTokenService;
import com.example.demo.auth.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


}
