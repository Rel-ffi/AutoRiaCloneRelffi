package org.autoriaclonebackend.car.repository;


import org.autoriaclonebackend.car.model.CarBrand;
import org.autoriaclonebackend.car.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    Optional<CarModel> findByNameAndBrand(String name, CarBrand brand);
}