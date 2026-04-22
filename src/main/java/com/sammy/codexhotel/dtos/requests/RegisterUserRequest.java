package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Pattern(regexp = "^(\\+234|0)(70|8[01]|9[01])[0-9]{8}$",
            message = "Invalid Nigerian phone number")
    private String phoneNumber;

    private UserRole role;
}
