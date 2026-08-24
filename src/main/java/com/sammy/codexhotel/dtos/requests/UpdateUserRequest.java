package com.sammy.codexhotel.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    @Pattern(regexp = "^(\\+234|0)(70|8[01]|9[01])[0-9]{8}$", message = "Invalid Nigerian phone number")
    private String phoneNumber;
    @Email(message = "Invalid email address")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    // No role field: a guest may PATCH their own profile, so accepting a role here would be a
    // self-promotion path the moment anyone wired it into Mappers.mapUpdate. Role changes belong
    // to a dedicated, ADMIN-only endpoint.
}