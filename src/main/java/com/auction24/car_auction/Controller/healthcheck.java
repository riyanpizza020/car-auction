package com.auction24.car_auction.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthcheck {
    @GetMapping("/health-check")
    public String healthcheck(){
        return "ok";
    }
}
