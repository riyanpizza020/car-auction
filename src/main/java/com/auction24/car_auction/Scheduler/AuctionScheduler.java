package com.auction24.car_auction.Scheduler;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {
    @Autowired
    private AuctionRepository auctionRepository;

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void manageAuctions() {

        LocalDateTime now = LocalDateTime.now();

        // upcoming to live
        List<Auction> readyToStart = auctionRepository.findByStatusAndStartTimeBefore("UPCOMING", now);

        for (Auction auction : readyToStart) {
            auction.setStatus("LIVE");
            auctionRepository.save(auction);
            System.out.println("Auction STARTED: " + auction.getId());
        }
        // live to closed
        List<Auction> expired = auctionRepository.findByStatusAndEndTimeBefore("LIVE", now);

        for (Auction auction : expired) {
            auction.setStatus("CLOSED");
            auctionRepository.save(auction);
            System.out.println("Auction closed:" + auction.getId() + " | Winner: " + auction.getHighestBidderId() + " | Bid: " + auction.getHighestBid());
        }
    }
}

