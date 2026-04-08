package com.auction24.car_auction.Service;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Bid;
import com.auction24.car_auction.Entities.Car;
import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Repository.AuctionRepository;
import com.auction24.car_auction.Repository.Bidrepository;
import com.auction24.car_auction.Repository.CarRepository;
import com.auction24.car_auction.Repository.UserRepository;
import com.auction24.car_auction.dto.UserBidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BidService {
    @Autowired
    private Bidrepository bidrepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;


    public Bid placeBid(String auctionId, String userId, double amount) {

        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("auction not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        // Admin cannot bid
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Admin cannot place bids");
        }

        // live or not
        if (!"LIVE".equals(auction.getStatus())) {
            throw new RuntimeException("auction is not live. Status: " + auction.getStatus());
        }

        // started or not
        if (LocalDateTime.now().isBefore(auction.getStartTime())) {
            throw new RuntimeException("auction not started yet");
        }
        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new RuntimeException("auction is closed");
        }
        if (userId.equals(auction.getHighestBidderId())) {
            throw new RuntimeException("you are already the highest bidder");
        }
        // bid high enough or not
        if (amount <= auction.getHighestBid()) {
            throw new RuntimeException("Bid must be higher than " + auction.getHighestBid());
        }
        if (auction.getHighestBidderId() != null) {
            auction.setSecondHighestBid(auction.getHighestBid());
            auction.setSecondHighestBidderId(auction.getHighestBidderId());
        }
        auction.setHighestBid(amount);
        auction.setHighestBidderId(userId);

        try {
            auctionRepository.save(auction);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Someone bid at the same time. Try again.");
        }

        // Save bid record
        Bid bid = new Bid();
        bid.setAuctionId(auctionId);
        bid.setUserId(userId);
        bid.setUserName(user.getName());
        bid.setAmount(amount);
        bid.setTimestamp(LocalDateTime.now());
        return bidrepository.save(bid);
    }
    public List<Bid> getBidsByAuction(String auctionId) {
        return bidrepository.findByAuctionIdOrderByTimestampDesc(auctionId);
    }
    public List<Bid> getBidsByUser(String userId) {
        return bidrepository.findByUserId(userId);
    }
    public long countBids(String auctionId) {
        return bidrepository.countByAuctionId(auctionId);
    }

    // Grouped bids by auction with car details
    public List<UserBidResponse> getUserBidsGrouped(String userId) {
        List<Bid> allBids = bidrepository.findByUserId(userId);

        // Group bids by auctionId
        Map<String, List<Bid>> grouped = allBids.stream()
                .collect(Collectors.groupingBy(Bid::getAuctionId));

        List<UserBidResponse> responses = new ArrayList<>();

        for (Map.Entry<String, List<Bid>> entry : grouped.entrySet()) {
            String auctionId = entry.getKey();
            List<Bid> auctionBids = entry.getValue();

            UserBidResponse response = new UserBidResponse();
            response.setAuctionId(auctionId);

            // Get auction details
            Auction auction = auctionRepository.findById(auctionId).orElse(null);
            if (auction != null) {
                response.setAuctionStatus(auction.getStatus());
                response.setHighestBid(auction.getHighestBid());

                // Resolve winner name
                if (auction.getHighestBidderId() != null) {
                    userRepository.findById(auction.getHighestBidderId())
                            .ifPresent(u -> response.setWinnerName(u.getName()));
                }
                response.setWonByUser(userId.equals(auction.getHighestBidderId())
                        && "CLOSED".equals(auction.getStatus()));

                // Get car details
                Car car = carRepository.findById(auction.getCarId()).orElse(null);
                if (car != null) {
                    response.setCarMake(car.getMake());
                    response.setCarModel(car.getModel());
                    response.setCarYear(car.getYear());
                    response.setCarImageUrl(car.getImageUrl());
                    response.setCarDeleted(false);
                } else {
                    response.setCarMake("Deleted");
                    response.setCarModel("Car");
                    response.setCarDeleted(true);
                }
            }

            // Convert bids to BidDetail (newest first)
            List<UserBidResponse.BidDetail> bidDetails = auctionBids.stream()
                    .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    .map(bid -> {
                        UserBidResponse.BidDetail detail = new UserBidResponse.BidDetail();
                        detail.setId(bid.getId());
                        detail.setAmount(bid.getAmount());
                        detail.setTimestamp(bid.getTimestamp());
                        return detail;
                    })
                    .collect(Collectors.toList());

            response.setBids(bidDetails);
            response.setTotalBids(bidDetails.size());
            responses.add(response);
        }

        return responses;
    }
}
