package com.auction24.car_auction.Service;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Repository.AuctionRepository;
import com.auction24.car_auction.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;

    public User getUserById(String userId) {//get by the id
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    public List<Auction> getWinningHistory(String userId) {
        return auctionRepository.findByHighestBidderIdAndStatus(userId, "CLOSED");
    }
}
