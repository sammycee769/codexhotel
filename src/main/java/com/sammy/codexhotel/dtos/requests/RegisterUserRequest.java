package com.sammy.codexhotel.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "^(\\+234|0)(70|8[01]|9[01])[0-9]{8}$",
            message = "Invalid Nigerian phone number")
    private String phoneNumber;

    // No role field by design: self-registration always yields a GUEST. Staff accounts are
    // seeded (see AdminSeeder) or promoted by an admin, so role cannot be injected here.
}
