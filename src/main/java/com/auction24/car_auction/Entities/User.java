package com.auction24.car_auction.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;

    @JsonIgnore    // password NEVER sent in any API response
    private String password;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
}