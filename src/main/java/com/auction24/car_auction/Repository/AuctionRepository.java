package com.auction24.car_auction.Repository;

import com.auction24.car_auction.Entities.Auction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends MongoRepository<Auction,String> {
    List<Auction> findByStatus(String status);
    List<Auction> findByStatusAndEndTimeBefore(String status, LocalDateTime time);
    List<Auction> findByStatusAndStartTimeBefore(String status, LocalDateTime time);
    List<Auction> findByHighestBidderIdAndStatus(String userId, String status);
    List<Auction> findByCreatedBy(String adminId);
    List<Auction> findByCarId(String carId);
}

