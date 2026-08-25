package com.sammy.codexhotel.config;

import com.sammy.codexhotel.security.CustomUserDetailsService;
import com.sammy.codexhotel.security.JwtAuthenticationEntryPoint;
import com.sammy.codexhotel.security.JwtAuthenticationFilter;
import com.sammy.codexhotel.security.RestAccessDeniedHandler;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        // Spring Security 7 injects the UserDetailsService through the constructor;
        // the old no-arg + setUserDetailsService form has been removed.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // A container ERROR dispatch re-enters this chain, but the JWT filter is a
                        // OncePerRequestFilter and does not re-run, so the context is empty by then.
                        // Without this, every 400 forwarded to /error would come back as a 401 and
                        // the frontend would read a validation failure as an expired session.
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.ASYNC).permitAll()

                        // --- Public: auth, room browsing and price quotes for the landing page ---
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available", "/api/rooms/available/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/calculate").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- Staff: declared before the ADMIN wildcards below, which would otherwise
                        //     swallow /api/rooms/maintenance/* and /api/rooms/available/* ---
                        .requestMatchers(HttpMethod.GET, "/api/rooms/all").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/maintenance/*").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/available/*").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/all").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/complete/*").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/api/users/all").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("ADMIN", "RECEPTIONIST")

                        // --- Admin only ---
                        .requestMatchers(HttpMethod.POST, "/api/rooms/add").hasRole("ADMIN")
                        // Scoped to ADMIN rather than staff at large because only an admin can add
                        // or renumber a room, so only an admin ever opens the form it feeds.
                        .requestMatchers(HttpMethod.GET, "/api/rooms/next-number/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reports/generate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/role/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/delete/*").hasRole("ADMIN")

                        // Everything else needs a token; per-record ownership is enforced
                        // with @PreAuthorize on the controller methods.
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
