package com.example.demo.auth.entities;

import com.example.demo.utils.AppConstants;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

/**
 * @author Avijeet
 * @project movieApi
 * @github avijeetas
 * @date 02-11-2024
 **/
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Builder
public class User implements UserDetails {
   public static final String EMAIL_CAN_NOT_BE_BLANK = "Email can not be blank";
   public static final String THE_PASSWORD_MUST_HAVE_AT_LEAST_8_CHARACTERS = "The password must have at least 8 characters";
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;
   @NotBlank(message = AppConstants.NAME_SHOULD_NOT_BE_NULL_OR_BLANK)
   private String name;
   @NotBlank(message = AppConstants.USER_NAME_SHOULD_NOT_BE_NULL_OR_BLANK)
   private String username;

   @NotBlank(message = "Password can not be blank")
   @Size(min=8, message = THE_PASSWORD_MUST_HAVE_AT_LEAST_8_CHARACTERS)
   private String password;

   @NotBlank(message = EMAIL_CAN_NOT_BE_BLANK)
   @Column(unique = true)
   @Email(message = "Please enter email in proper format")
   private String email;

   @Setter
   @OneToOne(mappedBy = "user")
   private RefreshToken refreshToken;

   @Enumerated(EnumType.STRING)
   private UserRole role;


   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority(role.name()));
   }

   @Override
   public String getPassword() {
      return password;
   }

   @Override
   public String getUsername() {
      return email;
   }

   @Override
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

}
