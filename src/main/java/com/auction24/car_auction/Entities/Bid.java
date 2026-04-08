package com.auction24.car_auction.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "bid")
public class Bid {
    @Id
    private String id;
    private String auctionId;
    private String userId;
    private String userName;
    private double amount;
    private LocalDateTime timestamp;
}
