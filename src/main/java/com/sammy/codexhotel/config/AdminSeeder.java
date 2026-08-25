package com.sammy.codexhotel.config;

import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.models.UserRole;
import com.sammy.codexhotel.data.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Self-registration always produces a GUEST, so the very first staff account has to come from
 * somewhere else. On startup, if no ADMIN exists yet, one is created from codexhotel.admin.*
 * properties. Once an ADMIN is present this is a no-op, so restarts never duplicate it and a
 * changed password in the properties file will not overwrite a live account.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${codexhotel.admin.name}")
    private String adminName;

    @Value("${codexhotel.admin.email}")
    private String adminEmail;

    @Value("${codexhotel.admin.password}")
    private String adminPassword;

    @Value("${codexhotel.admin.phone-number}")
    private String adminPhoneNumber;

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdministrator() {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        if (!admins.isEmpty()) {
            log.info("Administrator already present ({}), skipping seed", admins.get(0).getEmail());
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.warn("Cannot seed administrator: {} is already registered as a non-admin account", adminEmail);
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPhoneNumber(adminPhoneNumber);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        log.info("Seeded administrator account {} — change the password after first login", adminEmail);
    }
}
