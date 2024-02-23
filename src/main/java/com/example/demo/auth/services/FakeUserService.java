package com.example.demo.auth.services;

import com.example.demo.auth.entities.User;
import com.example.demo.auth.entities.UserRole;
import com.example.demo.auth.repositories.UserRepository;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 02-23-2024
 **/
@Service
@Slf4j
public class FakeUserService {
   private  final UserRepository userRepository;

    public FakeUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createFakeUsers(int count){
       Faker faker = new Faker(new Locale.Builder().setLanguage("en").setRegion("US").build());
       List<User> users = new ArrayList<>();

       for (int i = 0; i < count; i++) {
          var user = User
                  .builder()
                  .name(faker.name().fullName())
                  .username(faker.name().username())
                  .password("12345678")
                  .email(faker.internet().emailAddress())
                  .role(UserRole.USER)
                  .build();
          log.info("user {}: username {} email {}", i, user.getUsername(), user.getEmail());
          users.add(user);
       }

       userRepository.saveAll(users);
    }
}
