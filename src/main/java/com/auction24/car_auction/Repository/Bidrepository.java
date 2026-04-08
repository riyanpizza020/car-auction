package com.auction24.car_auction.Repository;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Bidrepository extends MongoRepository<Bid,String> {
    List<Bid> findByAuctionIdOrderByTimestampDesc(String auctionId);
    List<Bid> findByUserId(String userId);
    long countByAuctionId(String auctionId);
}
