package org.autoriaclonebackend.car.repository;


import org.autoriaclonebackend.car.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);
}