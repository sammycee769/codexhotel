package com.sammy.codexhotel.security;

import com.sammy.codexhotel.data.models.User;
import com.sammy.codexhotel.data.models.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapts the domain {@link User} to Spring Security. Exposes {@code userId} so
 * {@code @PreAuthorize} expressions can compare the principal against a path variable
 * and keep guests scoped to their own reservations, notifications and profile.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user.getUserId();
    }

    public User getUser() {
        return user;
    }

    public UserRole getRole() {
        return user.getRole();
    }

    /**
     * True for accounts that act on behalf of the hotel rather than for themselves, and so may
     * reach records they do not own.
     */
    public boolean isStaff() {
        UserRole role = user.getRole();
        return role == UserRole.ADMIN || role == UserRole.RECEPTIONIST;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = user.getRole() == null ? UserRole.GUEST : user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
