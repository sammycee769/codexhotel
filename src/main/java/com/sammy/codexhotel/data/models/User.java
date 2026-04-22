package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String userId;
    @NotBlank(message = "Username is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "invalid email address")
    private String email;
    @NotBlank(message ="Password is required")
    private String password;
    @Pattern(regexp = "^(\\+234|0)(70|8[01]|9[01])[0-9]{8}$", message = "Invalid Nigerian phone number")
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override
    public String getUsername(){
        return email;
    }
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
