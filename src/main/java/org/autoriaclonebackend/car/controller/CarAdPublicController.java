package org.autoriaclonebackend.car.controller;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.dto.BrandAndModelResponse;
import org.autoriaclonebackend.car.dto.CarAdPublicResponse;
import org.autoriaclonebackend.car.service.CarAdPublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CarAdPublicController {

    private final CarAdPublicService carAdPublicService;

    @GetMapping("/api/cars/public")
    @PreAuthorize(value = "null")
    public List<CarAdPublicResponse> getAllActiveCars() {
        return carAdPublicService.getActiveCars();
    }

    @GetMapping("/api/cars/public/{adId}")
    public CarAdPublicResponse getActiveCar(@PathVariable Long adId) {
        return carAdPublicService.getOneCar(adId);
    }

    @PostMapping("/api/cars/public/stats/{adId}")
    public void addNewView(@PathVariable Long adId) {
        carAdPublicService.addNewView(adId);
    }

    @GetMapping("/api/cars/public/{adId}/prices")
    public ResponseEntity<Map<String, Object>> getAdPricesPublic(@PathVariable Long adId) {
        return ResponseEntity.ok(carAdPublicService.getAdPublicPrices(adId));
    }

    @GetMapping("/api/cars/public/brandsflow")
    public ResponseEntity<List<BrandAndModelResponse>> getBrandsFlow() {
        return ResponseEntity.ok(carAdPublicService.getBrandsFlow());
    }
}
