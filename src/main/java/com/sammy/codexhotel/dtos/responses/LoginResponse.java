package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.UserRole;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private long expiresInMs;
}
