package com.auction24.car_auction.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class UserBidResponse {
    // Auction info
    private String auctionId;
    private String auctionStatus;
    private double highestBid;
    private String winnerName;

    // Car info
    private String carMake;
    private String carModel;
    private int carYear;
    private String carImageUrl;
    private boolean carDeleted;

    // User's bids in this auction
    private List<BidDetail> bids;
    private int totalBids;
    private boolean wonByUser;

    @Data
    public static class BidDetail {
        private String id;
        private double amount;
        private LocalDateTime timestamp;
    }
}
