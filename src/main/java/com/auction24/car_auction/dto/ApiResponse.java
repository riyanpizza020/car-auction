package com.auction24.car_auction.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private boolean success;
    private Object data;

    // Without data
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.data = null;
    }

    // With data
    public ApiResponse(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }
}
