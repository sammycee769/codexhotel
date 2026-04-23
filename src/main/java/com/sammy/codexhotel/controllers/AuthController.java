package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.dtos.requests.RegisterUserRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.dtos.responses.RegisterUserResponse;
import com.sammy.codexhotel.exceptions.UserAlreadyExistsException;
import com.sammy.codexhotel.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            RegisterUserResponse registerUserResponse = userService.registerUser(registerUserRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true,"User created successfully",registerUserResponse));
        }catch (UserAlreadyExistsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false , e.getMessage(), null));
        }
    }
}
