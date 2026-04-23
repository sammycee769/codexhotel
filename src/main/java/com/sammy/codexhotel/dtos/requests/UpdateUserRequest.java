package com.sammy.codexhotel.dtos.requests;

import com.sammy.codexhotel.data.models.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    @Pattern(regexp = "^(\\+234|0)(70|8[01]|9[01])[0-9]{8}$", message = "Invalid Nigerian phone number")
    private String phoneNumber;
    private UserRole role;
    @Email(message = "Invalid email address")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;
}