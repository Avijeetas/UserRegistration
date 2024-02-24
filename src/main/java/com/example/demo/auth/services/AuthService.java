package com.example.demo.auth.services;

import com.example.demo.utils.AppConstants;
import com.example.demo.AuthResponse;
import com.example.demo.LoginRequest;
import com.example.demo.RegisterRequest;
import com.example.demo.auth.entities.UserRole;
import com.example.demo.auth.entities.User;
import com.example.demo.auth.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-17-2024
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final RefreshTokenService refreshTokenService;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
   private final UserDetailsService userDetailsService;
    public AuthResponse register(RegisterRequest registerRequest) {
      var user = User
              .builder()
              .name(registerRequest.getName())
              .email(registerRequest.getEmail())
              .password(passwordEncoder.encode(registerRequest.getPassword()))
              .username(registerRequest.getUsername())
              .role(UserRole.USER)
              .build();

      User saveUser = userRepository.save(user);

      log.info("User has been created with user email {}",user.getEmail());
      return generateAuthResponse(saveUser);
   }

   public AuthResponse generateAuthResponse(User saveUser) {
      var accessToken = jwtService.generateToken(saveUser);
      var refreshToken = refreshTokenService.createRefreshToken(saveUser.getEmail());
      return AuthResponse
              .builder()
              .accessToken(accessToken)
              .refreshToken(refreshToken.getRefreshToken())
              .build();
   }

   public AuthResponse login(LoginRequest loginRequest){
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getEmail(),
                      loginRequest.getPassword()));

      var user = userRepository.findByUsername(loginRequest.getEmail()).orElseThrow(()->
              new UsernameNotFoundException(AppConstants.USER_NOT_FOUND_WITH_EMAIL));
      log.info("User has been logged in with user email {}",user.getEmail());
      return generateAuthResponse(user);
   }

   public void logout(HttpServletRequest request){
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
         SecurityContextHolder.clearContext();
      }
   }

   public boolean validateUser(String jwt) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      String username = jwtService.extractUsername(jwt);
      if(authentication != null && authentication.isAuthenticated()){
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          return jwtService.isTokenValid(jwt, userDetails);
      }
      return false;
   }

   public void registerAll(List<RegisterRequest> registerRequests) {
      for (RegisterRequest request : registerRequests) {
         register(request);
      }
   }



}
