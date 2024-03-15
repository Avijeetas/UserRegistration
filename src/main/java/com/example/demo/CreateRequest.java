package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateRequest {
    private String name;
    private String email;
    private String username;
}
