package com.sammy.codexhotel.controllers;

import com.sammy.codexhotel.dtos.requests.RegisterUserRequest;
import com.sammy.codexhotel.dtos.requests.UpdateUserRequest;
import com.sammy.codexhotel.dtos.responses.ApiResponse;
import com.sammy.codexhotel.dtos.responses.RegisterUserResponse;
import com.sammy.codexhotel.dtos.responses.UserResponse;
import com.sammy.codexhotel.exceptions.UserAlreadyExistsException;
import com.sammy.codexhotel.exceptions.UserNotFoundException;
import com.sammy.codexhotel.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String userId) {
        try {
            UserResponse response = userService.getUserById(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true,"user found", response));
        }catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,e.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Users retrieved successfully", users));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/update/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            UserResponse response = userService.updateUser(userId,updateUserRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "user updated successfully", response));
        }catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(true, "user deleted successfully", null));
        }catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false,e.getMessage(), null));
        }
    }
}
