package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.UserRole;
import lombok.Data;

@Data
public class RegisterUserResponse {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
}
