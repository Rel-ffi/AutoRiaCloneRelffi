package org.autoriaclonebackend.car.repository;


import org.autoriaclonebackend.car.model.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
    Optional<CarBrand> findByName(String name);
}