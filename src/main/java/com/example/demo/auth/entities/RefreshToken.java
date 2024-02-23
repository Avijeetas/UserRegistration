package com.example.demo.auth.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;


@Entity

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Please under refresh token value")
    private String refreshToken;

    @Column(nullable = false)
    private Instant expirationTime;
    @OneToOne
    private User user;
}