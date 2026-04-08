package com.auction24.car_auction.Repository;

import com.auction24.car_auction.Entities.Car;
import com.auction24.car_auction.Entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CarRepository extends MongoRepository<Car,String> {
    Optional<Car> findByregistrationNumber(String registrationNumber);
    List<Car> findByAddedBy(String adminId);
}
