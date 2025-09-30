package org.autoriaclonebackend.car.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "car_ad_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarAdView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "ad_id")
    private CarAd carAd;


    @Column(nullable = false)
    private LocalDateTime viewedAt;
}