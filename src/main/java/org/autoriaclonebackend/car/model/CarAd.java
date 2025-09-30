package org.autoriaclonebackend.car.model;


import jakarta.persistence.*;
import lombok.*;
import org.autoriaclonebackend.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "car_ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarAd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;


    @ManyToOne
    @JoinColumn(name = "brand_id")
    private CarBrand brand;


    @ManyToOne
    @JoinColumn(name = "model_id")
    private CarModel model;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false)
    private Double price;


    @Column(nullable = false)
    private String currency;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false)
    private String status;


    @Column(nullable = false)
    private int editAttempts;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @ElementCollection
    @CollectionTable(name = "car_ad_images", joinColumns = @JoinColumn(name = "car_ad_id"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();
}