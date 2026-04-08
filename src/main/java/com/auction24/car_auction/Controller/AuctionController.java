package com.auction24.car_auction.Controller;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Bid;
import com.auction24.car_auction.Service.AuctionService;
import com.auction24.car_auction.Service.BidService;
import com.auction24.car_auction.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BidService bidService;

    // POST /api/auctions — create auction (ADMIN only, adminId from JWT)
    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody Auction auction, Authentication authentication) {
        try {
            String adminId = authentication.getName();
            Auction saved = auctionService.createAuction(auction, adminId);
            return ResponseEntity.ok(new ApiResponse("Auction created", true, saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllAuctions() {
        return ResponseEntity.ok(new ApiResponse("Auctions fetched", true,
                auctionService.getAllAuctionsWithCar()));
    }

    // GET /api/auctions/status?status=LIVE (PUBLIC)
    @GetMapping("/status")
    public ResponseEntity<?> getAuctionsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(new ApiResponse("Auctions fetched", true,
                auctionService.getAuctionsByStatusWithCar(status)));
    }

    // GET /api/auctions/{id} (PUBLIC)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(new ApiResponse("Auction found", true,
                    auctionService.getAuctionByIdWithCar(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    // DELETE /api/auctions/{id} — delete auction (ADMIN only, adminId from JWT)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuction(@PathVariable String id, Authentication authentication) {
        try {
            String adminId = authentication.getName();
            auctionService.deleteAuction(id, adminId);
            return ResponseEntity.ok(new ApiResponse("Auction deleted", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    // GET /api/auctions/admin — admin's auctions (ADMIN only)
    @GetMapping("/admin")
    public ResponseEntity<?> getAuctionsByAdmin(Authentication authentication) {
        String adminId = authentication.getName();
        return ResponseEntity.ok(new ApiResponse("Admin auctions fetched", true,
                auctionService.getAdminAuctionsWithCar(adminId)));
    }

    // GET /api/auctions/{id}/bids — bid history (PUBLIC)
    @GetMapping("/{id}/bids")
    public ResponseEntity<?> getBidHistory(@PathVariable String id) {
        List<Bid> bids = bidService.getBidsByAuction(id);
        return ResponseEntity.ok(new ApiResponse("Bids fetched", true, bids));
    }

    // POST /api/auctions/{id}/bid — place bid (USER only, userId from JWT)
    @PostMapping("/{id}/bid")
    public ResponseEntity<?> placeBid(
            @PathVariable String id,
            @RequestParam double amount,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            Bid bid = bidService.placeBid(id, userId, amount);
            return ResponseEntity.ok(new ApiResponse("Bid placed successfully", true, bid));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
    // GET /api/auctions/{id}/bids/count (PUBLIC)
    @GetMapping("/{id}/bids/count")
    public ResponseEntity<?> countBids(@PathVariable String id) {
        long count = bidService.countBids(id);
        return ResponseEntity.ok(new ApiResponse("Bid count", true, count));
    }
}
