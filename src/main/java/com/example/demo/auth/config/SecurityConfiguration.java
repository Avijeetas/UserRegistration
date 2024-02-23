package com.example.demo.auth.config;

import com.example.demo.auth.services.AuthFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
   private final AuthenticationProvider authenticationProvider;
   private final AuthFilterService authFilterService;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
      httpSecurity
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth->

                      auth.requestMatchers("/api/v1/**")
                              .permitAll()
                              .anyRequest()
                              .authenticated())
              .sessionManagement(session -> session
                      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(authenticationProvider)
              .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
      return  httpSecurity.build();
   }

}
