package com.auction24.car_auction.Service;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Car;
import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Repository.AuctionRepository;
import com.auction24.car_auction.Repository.CarRepository;
import com.auction24.car_auction.Repository.UserRepository;
import com.auction24.car_auction.dto.AuctionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${auction.duration:5}")
    private int auctionDuration;

    //creating auction
    public Auction createAuction(Auction auction, String adminId) {
        carRepository.findById(auction.getCarId()).orElseThrow(() -> new RuntimeException("Car not found"));

        // IMPROVED: Check if car already has active auction
        List<Auction> existing = auctionRepository.findByCarId(auction.getCarId());
        boolean alreadyActive = existing.stream()
                .anyMatch(a -> "LIVE".equals(a.getStatus()) || "UPCOMING".equals(a.getStatus()));
        if (alreadyActive) {
            throw new RuntimeException("This car already has an active auction");
        }

        if (auction.getStartTime() != null) {
            auction.setStatus("UPCOMING");
        } else {
            auction.setStartTime(LocalDateTime.now());
            auction.setStatus("LIVE");
        }
        auction.setDurationMinutes(auctionDuration);
        //end time
       auction.setEndTime(auction.getStartTime().plusMinutes(auction.getDurationMinutes()));

        auction.setHighestBid(auction.getStartingPrice());
        auction.setSecondHighestBid(0);//
        auction.setSecondHighestBidderId(null);
        auction.setHighestBidderId(null);
        auction.setCreatedBy(adminId);
        return auctionRepository.save(auction);
    }
    public List<Auction>getAllAuctions() {
       return auctionRepository.findAll();
    }
    //live or not
    public List<Auction> getAuctionsbyStatus(String status){
        return auctionRepository.findByStatus(status);
    }
    public Auction getAuctionById(String id) {
        return auctionRepository.findById(id).orElseThrow(()->new RuntimeException("not found"));
    }
    public void deleteAuction(String id, String adminId) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Auction not found"));
        if (!adminId.equals(auction.getCreatedBy())) {
            throw new RuntimeException("Not authorized to delete this auction");
        }
        // Can delete ANY auction — UPCOMING, LIVE, or CLOSED
        auctionRepository.delete(auction);
    }
    public List<Auction> getAuctionsByAdmin(String adminId) {
        return auctionRepository.findByCreatedBy(adminId);
    }

    // ============ NEW: Methods returning AuctionResponse with car + bidder names ============

    private AuctionResponse toResponse(Auction auction) {
        Car car = carRepository.findById(auction.getCarId()).orElse(null);
        String highestName = lookupName(auction.getHighestBidderId());
        String secondName = lookupName(auction.getSecondHighestBidderId());
        return AuctionResponse.fromAuction(auction, car, highestName, secondName);
    }

    private String lookupName(String userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).map(User::getName).orElse(null);
    }

    public List<AuctionResponse> getAllAuctionsWithCar() {
        return auctionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AuctionResponse> getAuctionsByStatusWithCar(String status) {
        return auctionRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AuctionResponse getAuctionByIdWithCar(String id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        return toResponse(auction);
    }

    public List<AuctionResponse> getWinsWithCar(String userId) {
        return auctionRepository.findByHighestBidderIdAndStatus(userId, "CLOSED").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AuctionResponse> getAdminAuctionsWithCar(String adminId) {
        return auctionRepository.findByCreatedBy(adminId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
