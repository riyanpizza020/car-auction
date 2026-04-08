package com.auction24.car_auction.Controller;

import com.auction24.car_auction.Entities.User;
import com.auction24.car_auction.Service.AuctionService;
import com.auction24.car_auction.Service.BidService;
import com.auction24.car_auction.Service.UserAuthService;
import com.auction24.car_auction.Service.UserService;
import com.auction24.car_auction.dto.ApiResponse;
import com.auction24.car_auction.dto.LoginRequest;
import com.auction24.car_auction.dto.LoginResponse;
import com.auction24.car_auction.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private BidService bidService;

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userAuthService.register(request);
            return ResponseEntity.ok(new ApiResponse("Registered successfully", true, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userAuthService.login(request);
            return ResponseEntity.ok(new ApiResponse("Login successful", true, response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }


    // GET /api/users/me — get MY profile from JWT token
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            String userId = authentication.getName();
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse("Profile fetched", true, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    // GET /api/users/{id} — owner or admin only
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id, Authentication authentication) {
        if (!canAccess(authentication, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Forbidden", false));
        }
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(new ApiResponse("User found", true, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }


    // GET /api/users — admin only (also locked at SecurityConfig)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("Users fetched", true, users));
    }

    // GET /api/users/role?role=USER — filter by role (ADMIN only)
    @GetMapping("/role")
    public ResponseEntity<?> getUsersByRole(@RequestParam String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(new ApiResponse("Users fetched", true, users));
    }

    // GET /api/users/{id}/wins — owner or admin only
    @GetMapping("/{id}/wins")
    public ResponseEntity<?> getWinningHistory(@PathVariable String id, Authentication authentication) {
        if (!canAccess(authentication, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Forbidden", false));
        }
        return ResponseEntity.ok(new ApiResponse("Wins fetched", true,
                auctionService.getWinsWithCar(id)));
    }

    // GET /api/users/{id}/bids — owner or admin only
    @GetMapping("/{id}/bids")
    public ResponseEntity<?> getBidHistory(@PathVariable String id, Authentication authentication) {
        if (!canAccess(authentication, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Forbidden", false));
        }
        return ResponseEntity.ok(new ApiResponse("Bids fetched", true,
                bidService.getUserBidsGrouped(id)));
    }

    // Helper: only the user themselves or an admin can access
    private boolean canAccess(Authentication auth, String targetUserId) {
        if (auth == null) return false;
        if (auth.getName().equals(targetUserId)) return true;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
