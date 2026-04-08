package com.auction24.car_auction.dto;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Car;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuctionResponse {
    // Auction fields
    private String id;
    private String carId;
    private double startingPrice;
    private double highestBid;
    private double secondHighestBid;
    private String highestBidderId;
    private String highestBidderName;
    private String secondHighestBidderId;
    private String secondHighestBidderName;
    private String status;
    private int durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createdBy;

    // Car fields (from car document)
    private String carMake;
    private String carModel;
    private int carYear;
    private String carColor;
    private String carFuelType;
    private double carMileage;
    private String carImageUrl;
    private boolean carDeleted;

    public static AuctionResponse fromAuction(Auction auction, Car car,
                                              String highestBidderName,
                                              String secondHighestBidderName) {
        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setCarId(auction.getCarId());
        response.setStartingPrice(auction.getStartingPrice());
        response.setHighestBid(auction.getHighestBid());
        response.setSecondHighestBid(auction.getSecondHighestBid());
        response.setHighestBidderId(auction.getHighestBidderId());
        response.setHighestBidderName(highestBidderName);
        response.setSecondHighestBidderId(auction.getSecondHighestBidderId());
        response.setSecondHighestBidderName(secondHighestBidderName);
        response.setStatus(auction.getStatus());
        response.setDurationMinutes(auction.getDurationMinutes());
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setCreatedBy(auction.getCreatedBy());

        if (car != null) {
            response.setCarMake(car.getMake());
            response.setCarModel(car.getModel());
            response.setCarYear(car.getYear());
            response.setCarColor(car.getColor());
            response.setCarFuelType(car.getFuelType());
            response.setCarMileage(car.getMileage());
            response.setCarImageUrl(car.getImageUrl());
            response.setCarDeleted(false);
        } else {
            response.setCarMake("Deleted");
            response.setCarModel("Car");
            response.setCarYear(0);
            response.setCarDeleted(true);
        }

        return response;
    }
}
