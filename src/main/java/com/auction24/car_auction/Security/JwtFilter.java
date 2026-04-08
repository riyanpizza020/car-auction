package com.auction24.car_auction.Security;

import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Get the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Step 3: Extract token (remove "Bearer " prefix)
            String token = authHeader.substring(7);

            try {
                // Step 4: Extract userId from token
                String userId = jwtUtil.extractUserId(token);

                // Step 5: Check if user exists and token is valid
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null && jwtUtil.validateToken(token, userId)) {

                        // Step 6: Extract role and create authentication
                        String role = jwtUtil.extractRole(token);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userId,     // principal = userId
                                        null,       // credentials
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority("ROLE_" + role)
                                        )
                                );

                        // Step 7: Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Invalid token — do nothing, request will be rejected by SecurityConfig
            }
        }

        // Step 8: Continue the filter chain
        filterChain.doFilter(request, response);
    }
}