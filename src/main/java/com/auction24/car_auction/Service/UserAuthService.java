package com.auction24.car_auction.Service;

import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Repository.UserRepository;
import com.auction24.car_auction.Security.JwtUtil;
import com.auction24.car_auction.dto.LoginRequest;
import com.auction24.car_auction.dto.LoginResponse;
import com.auction24.car_auction.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class UserAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    // saving the new user here

    public User register(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email ALready Exist");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    //login ka jo response

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Inncorrect password");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);
        return response;
    }

}
