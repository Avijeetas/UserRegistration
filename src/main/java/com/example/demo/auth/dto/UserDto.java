package com.example.demo.auth.dto;

import com.example.demo.auth.entities.RefreshToken;
import com.example.demo.auth.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-17-2024
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String username;

    private String email;

    private RefreshToken refreshToken;

    private UserRole role;

}
