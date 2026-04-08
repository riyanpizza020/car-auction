package com.auction24.car_auction.Service;

import com.auction24.car_auction.Entities.Auction;
import com.auction24.car_auction.Entities.Car;
import com.auction24.car_auction.Repository.AuctionRepository;
import com.auction24.car_auction.Repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private AuctionRepository auctionRepository;

    public Car addCar(Car car, String adminId) {
        if (carRepository.findByregistrationNumber(car.getRegistrationNumber()).isPresent()) {
            throw new RuntimeException("Car with this registeration number Already exists");
        }
        car.setAddedBy(adminId);
        car.setCreatedAt(LocalDateTime.now());
        return carRepository.save(car); //save the car
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(String id) {
        return carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not Found"));
    }

    // IMPROVED: Can't delete car while in active auction
    public void deleteCar(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        List<Auction> auctions = auctionRepository.findByCarId(id);
        boolean hasActive = auctions.stream()
                .anyMatch(a -> a.getStatus().equals("LIVE") || a.getStatus().equals("UPCOMING"));

        if (hasActive) {
            throw new RuntimeException("Cannot delete car while auction is active");
        }

        carRepository.delete(car);
    }

    public List<Car> getCarsByAdmin(String adminId) {
        return carRepository.findByAddedBy(adminId);
    }
}

