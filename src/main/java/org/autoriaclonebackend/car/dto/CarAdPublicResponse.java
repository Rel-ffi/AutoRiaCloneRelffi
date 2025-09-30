package org.autoriaclonebackend.car.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarAdPublicResponse {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private List<String> imageUrls;
    private String brand;
    private String model;
    private Double price;
    private String description;
    private String currency;
    private String status;
    private int editAttempts;
    private String region;
    private LocalDateTime createdAt;
}
