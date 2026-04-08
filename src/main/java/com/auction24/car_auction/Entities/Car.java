package com.auction24.car_auction.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "cars")
public class Car {
    @Id
    private String id;
    private String make;
    private String model;
    private int year;
    private String imageUrl;
    private String registrationNumber;
    private String Color;
    private String description;
    private String fuelType;
    private double mileage;
    private String addedBy;
    private LocalDateTime createdAt;
}
