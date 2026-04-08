package com.auction24.car_auction.Controller;

import com.auction24.car_auction.Entities.Bid;
import com.auction24.car_auction.Service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    @Autowired
    private BidService bidService;

    @GetMapping("/user")
    public ResponseEntity<List<Bid>> getBidsByUser(@RequestParam String userId) {
        return ResponseEntity.ok(bidService.getBidsByUser(userId));
    }
    @GetMapping("/auction")
    public ResponseEntity<List<Bid>> getBidsByAuction(@RequestParam String auctionId) {
        return ResponseEntity.ok(bidService.getBidsByAuction(auctionId));
    }
}
