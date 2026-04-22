package com.sammy.codexhotel.data.repositories;

import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.models.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phoneNumber);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
}
