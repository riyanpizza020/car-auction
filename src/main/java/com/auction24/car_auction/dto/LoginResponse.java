package com.auction24.car_auction.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private String token;
}
