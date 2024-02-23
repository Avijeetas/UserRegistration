package com.example.demo.auth.repositories;


import com.example.demo.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
