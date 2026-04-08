package com.auction24.car_auction.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Disable CSRF (not needed for REST APIs)
                .csrf(csrf -> csrf.disable())

                // Stateless sessions (JWT handles auth, not sessions)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL access rules
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC — no login needed
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cars").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/cars").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers("/api/cars/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions/**").hasRole("ADMIN")
                        .requestMatchers("/api/auctions/admin").hasRole("ADMIN")
                        .requestMatchers("/api/users/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")

                        // USER ONLY — placing bids
                        .requestMatchers(HttpMethod.POST, "/api/auctions/*/bid").hasRole("USER")

                        // AUTHENTICATED — any logged in user
                        .anyRequest().authenticated()
                )

                // Add JWT filter before Spring's default filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}