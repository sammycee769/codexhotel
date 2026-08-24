package com.sammy.codexhotel.services;

import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.models.UserRole;
import com.sammy.codexhotel.data.repositories.UserRepo;
import com.sammy.codexhotel.dtos.requests.LoginRequest;
import com.sammy.codexhotel.dtos.requests.RegisterUserRequest;
import com.sammy.codexhotel.dtos.requests.UpdateUserRequest;
import com.sammy.codexhotel.dtos.responses.LoginResponse;
import com.sammy.codexhotel.dtos.responses.RegisterUserResponse;
import com.sammy.codexhotel.dtos.responses.UserResponse;
import com.sammy.codexhotel.exceptions.CannotChangeRoleException;
import com.sammy.codexhotel.exceptions.UserAlreadyExistsException;
import com.sammy.codexhotel.exceptions.UserNotFoundException;
import com.sammy.codexhotel.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest){
        validateUserDoesNotExist(registerUserRequest.getEmail());
        validateUserDoesNotExistByPhoneNumber(registerUserRequest.getPhoneNumber());
        User user = map(registerUserRequest);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        userRepository.save(user);
        return map(registerUserRequest, user);
    }

    /**
     * Delegates credential checking to the AuthenticationManager, then issues a JWT.
     * A bad password surfaces as BadCredentialsException for the controller to map to 401.
     */
    public LoginResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        return map(user, jwtService.generateToken(user), jwtService.getExpirationMs());
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

    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User existingUser = findUserById(userId);
        validateEmailUpdate(existingUser, request.getEmail());
        validatePhoneUpdate(existingUser, request.getPhoneNumber());
        mapUpdate(existingUser, request);
        userRepository.save(existingUser);

        return mapToUser(existingUser);
    }

    /**
     * Assigns a role to a user. ADMIN-only at the controller. An admin cannot change their own
     * role: it would let them demote themselves out of the console mid-session, and if they were
     * the only admin it would leave the system with no way to promote anyone ever again.
     */
    public UserResponse changeRole(String actingUserId, String targetUserId, UserRole newRole) {
        if (actingUserId.equals(targetUserId)) {
            throw new CannotChangeRoleException("You cannot change your own role");
        }
        User target = findUserById(targetUserId);
        target.setRole(newRole);
        userRepository.save(target);
        return mapToUser(target);
    }

    public void deleteUser(String userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }


    private void validateEmailUpdate(User existingUser, String newEmail) {
        if (newEmail != null && !existingUser.getEmail().equals(newEmail)) {
            validateUserDoesNotExist(newEmail);
        }
    }

    private void validatePhoneUpdate(User existingUser, String newPhone) {
        if (newPhone != null && !newPhone.equals(existingUser.getPhoneNumber())) {
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
        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        if(existingUser !=null && existingUser.isPresent()){
            throw new UserAlreadyExistsException(phoneNumber + " is already registered");
        }
    }
}
