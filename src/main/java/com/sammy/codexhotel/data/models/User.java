package com.sammy.codexhotel.data.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
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

}
