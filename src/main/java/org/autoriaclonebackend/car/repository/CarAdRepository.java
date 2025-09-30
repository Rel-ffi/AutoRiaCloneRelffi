package org.autoriaclonebackend.car.repository;


import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CarAdRepository extends JpaRepository<CarAd, Long> {
    List<CarAd> findBySeller(User seller);

    List<CarAd> findByStatus(String active);

    CarAd findByStatusAndId(String status, Long id);


    List<CarAd> findAllByStatus(String status);

    List<CarAd> findAllByStatusOrStatus(String inactive, String pending);
}
