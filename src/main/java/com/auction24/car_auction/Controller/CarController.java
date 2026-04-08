package com.auction24.car_auction.Controller;

import com.auction24.car_auction.Entities.Car;
import com.auction24.car_auction.Service.CarService;
import com.auction24.car_auction.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    // POST /api/cars — add car (ADMIN only, adminId from JWT)
    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody Car car, Authentication authentication) {
        try {
            String adminId = authentication.getName();
            Car saved = carService.addCar(car, adminId);
            return ResponseEntity.ok(new ApiResponse("Car added successfully", true, saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
    // GET /api/cars — get all cars (PUBLIC)
    @GetMapping
    public ResponseEntity<?> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(new ApiResponse("Cars fetched", true, cars));
    }
    // GET /api/cars/{id} — get car by id (PUBLIC)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable String id) {
        try {
            Car car = carService.getCarById(id);
            return ResponseEntity.ok(new ApiResponse("Car found", true, car));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    // DELETE /api/cars/{id} — delete car (ADMIN only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable String id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.ok(new ApiResponse("Car deleted successfully", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    // GET /api/cars/admin — get cars added by this admin (ADMIN only)
    @GetMapping("/admin")
    public ResponseEntity<?> getCarsByAdmin(Authentication authentication) {
        String adminId = authentication.getName();
        List<Car> cars = carService.getCarsByAdmin(adminId);
        return ResponseEntity.ok(new ApiResponse("Admin cars fetched", true, cars));
    }
}
