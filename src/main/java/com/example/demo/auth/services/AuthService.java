package com.example.demo.auth.services;

import com.example.demo.CreateRequest;
import com.example.demo.registration.email.EmailService;
import com.example.demo.utils.AppConstants;
import com.example.demo.AuthResponse;
import com.example.demo.LoginRequest;
import com.example.demo.RegisterRequest;
import com.example.demo.auth.entities.UserRole;
import com.example.demo.auth.entities.User;
import com.example.demo.auth.repositories.UserRepository;
import com.example.demo.utils.EmailUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
   private final RedisService redisService;
   private final EmailUtils emailUtils;
   private final EmailService emailService;

   @Value("${app.server}/${server.port}")
   private String baseUrl;
   @Value("${otp.template}")
   private String template;

   @Value("${otp.length}")
   private int length;

   @Value("${otp.timeout-minute}")
   private int timeoutMinute;
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

   private String generateCode(int length){
      return RandomStringUtils.randomNumeric(length);
   }
   public void create(CreateRequest createRequest) {
       // todo create a otp
      String keyInRedis = getCodeKey(createRequest.getUsername());
      String code = redisService.get(keyInRedis);
      if(code == null)
         code = generateCode(length);
      log.info("generated otp code user {} : {}", createRequest.getUsername(), code);
      // save in redis
      redisService.save(keyInRedis, code, timeoutMinute, TimeUnit.MINUTES);
      sendEmail(createRequest.getName(), createRequest.getEmail(), code);
   }
   private void sendEmail(String toFirstName, String email, String otp) {
      try {
         String message = String.format(template, otp, timeoutMinute);
         String body = emailUtils.buildEmail(toFirstName, message);
         emailService.send(email, body);
         log.info(String.format(AppConstants.EMAIL_SUCCESS_MSG, email));
      } catch (Exception e) {
         log.error("Failed to send email to " + email, e);
      }
   }
   public void verifyOtp(String username, @NotNull String otp) {
      String key = getCodeKey(username);
      String generatedOtp = redisService.get(key);

      if (generatedOtp == null) {
         log.info("OTP not found for {}", username);
         return;
      }

      if (otp.equals(generatedOtp)) {
         log.info("OTP verified for {}", username);
         redisService.delete(key);
      }
   }
   private String getCodeKey(String username) {
      return String.format("code:%s", username);
   }
}
