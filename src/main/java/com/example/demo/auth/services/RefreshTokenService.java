package com.example.demo.auth.services;

import com.example.demo.utils.AppConstants;
import com.example.demo.auth.entities.RefreshToken;
import com.example.demo.auth.entities.User;
import com.example.demo.auth.repositories.RefreshTokenRepository;
import com.example.demo.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/
@Service
public class RefreshTokenService {
   private final UserRepository userRepository;

   private final RefreshTokenRepository refreshTokenRepository;

   @Value("${project.refreshTokenValidity}")
   private Integer refreshTokenValidity;
    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username ){
      User user = userRepository.findByUsername(username)
              .orElseThrow(()->new UsernameNotFoundException(
                      String.format(AppConstants.USER_NOT_FOUND_WITH_EMAIL, username)));

       RefreshToken refreshToken = user.getRefreshToken();
       if(refreshToken==null){
          return generateRefreshToken(user);
       }
       return refreshToken;
    }

   public RefreshToken generateRefreshToken(User user) {
      String newRefreshToken = UUID.randomUUID().toString();

      RefreshToken refreshToken =  RefreshToken.builder()
              .refreshToken(newRefreshToken)
              .expirationTime(Instant.now().plusSeconds(refreshTokenValidity))
              .user(user)
              .build();


      refreshTokenRepository.save(refreshToken);

      return refreshToken;
   }

   public RefreshToken verifyRefreshToken(String token){

        RefreshToken rft = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(()->new RuntimeException("Refresh token not found"));
        if(rft.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(rft);
            throw new RuntimeException("Refresh Token expired");
        }
        return rft;
   }
}
