package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.repositories.UserRepo;
import com.sammy.codexhotel.dtos.requests.RegisterUserRequest;
import com.sammy.codexhotel.dtos.responses.RegisterUserResponse;
import com.sammy.codexhotel.dtos.responses.UserResponse;
import com.sammy.codexhotel.exceptions.UserAlreadyExistsException;
import com.sammy.codexhotel.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sammy.codexhotel.utils.Mappers.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest){
        validateUserDoesNotExist(registerUserRequest.getEmail());
        validateUserDoesNotExistByPhoneNumber(registerUserRequest.getPhoneNumber());
        User user = map(registerUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return map(registerUserRequest, user);
    }

    public UserResponse getUserById(String userId){
        User user=findUserById(userId);
        return mapToUser(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            responses.add(mapToUser(user));
        }
        return responses;
    }

    public UserResponse updateUser(String userId, RegisterUserRequest request) {
        User existingUser = findUserById(userId);
        validateEmailUpdate(existingUser, request.getEmail());
        validatePhoneUpdate(existingUser, request.getPhoneNumber());
        mapUpdate(existingUser, request);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(existingUser);

        return mapToUser(existingUser);
    }

    public void deleteUser(String userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }


    private void validateEmailUpdate(User existingUser, String newEmail) {
        if (!existingUser.getEmail().equals(newEmail)) {
            validateUserDoesNotExist(newEmail);
        }
    }

    private void validatePhoneUpdate(User existingUser, String newPhone) {
        if (!existingUser.getPhoneNumber().equals(newPhone)) {
            validateUserDoesNotExistByPhoneNumber(newPhone);
        }
    }

    public User findUserById(String id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    private void validateUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
    }

    private void validateUserDoesNotExistByPhoneNumber(String phoneNumber){
        Optional<User> existingUser = userRepository.findByPhone(phoneNumber);
        if(existingUser !=null && existingUser.isPresent()){
            throw new UserAlreadyExistsException(phoneNumber + " is already registered");
        }
    }
}
