package com.example.demo.auth.services;

import com.example.demo.RegisterRequest;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 02-23-2024
 **/
@Service
@Slf4j
public class FakeUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    public FakeUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public void createFakeUsers(int count) {
        Faker faker = new Faker(new Locale.Builder().setLanguage("en").setRegion("US").build());

        List<User> users = IntStream.range(0, count)
                .mapToObj(i -> User.builder()
                        .name(faker.name().fullName())
                        .username(faker.name().username())
                        .password(passwordEncoder.encode("12345678"))
                        .email(faker.internet().emailAddress())
                        .role(UserRole.USER)
                        .build())
                .peek(user -> log.info("Created user: username={}, email={}", user.getUsername(), user.getEmail()))
                .toList();

        List<RegisterRequest> registerRequests = users.stream()
                .map(this::convertToRegisterReq)
                .collect(Collectors.toList());

        authService.registerAll(registerRequests);
    }

    private RegisterRequest convertToRegisterReq(User user) {
        return new RegisterRequest(user.getName(), user.getUsername(), user.getEmail(), "12345678");
    }
}
