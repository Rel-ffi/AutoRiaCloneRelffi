package org.autoriaclonebackend.car.repository;


import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.model.CarAdView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface CarAdViewRepository extends JpaRepository<CarAdView, Long> {
    List<CarAdView> findByCarAd(CarAd carAd);
    List<CarAdView> findByCarAdAndViewedAtAfter(CarAd carAd, LocalDateTime date);
}