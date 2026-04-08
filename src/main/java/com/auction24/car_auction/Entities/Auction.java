package com.auction24.car_auction.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Document(collection = "auction")
public class Auction {
    @Id
    private String id;

    @Version
    private Long version;

    private String carId;
    private double startingPrice;
    private double highestBid;
    private double secondHighestBid;
    private String secondHighestBidderId;
    private String highestBidderId;
    private String status;
    private int durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createdBy;
}
