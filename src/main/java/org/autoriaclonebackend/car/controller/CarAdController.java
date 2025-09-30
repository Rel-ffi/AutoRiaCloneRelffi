package org.autoriaclonebackend.car.controller;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.dto.CarAdPublicResponse;
import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.service.CarAdService;
import org.autoriaclonebackend.user.repository.UserRepository;
import org.autoriaclonebackend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping( value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> createAd(
            @RequestPart("brand") String brand,
            @RequestPart("model") String model,
            @RequestPart("price") double price,
            @RequestPart("currency") String currency,
            @RequestPart("description") String description,
            @RequestPart("region") String region,
            @RequestPart("images") List<MultipartFile> images,
            Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarAd ad = null;
        try {
            ad = carAdService.createCarAd(user, brand, model, price, currency, description, region, images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Advertisement created with ID: " + ad.getId() + ", status: " + ad.getStatus());
    }

    @PostMapping("/edit/{adId}")
    public ResponseEntity<String> editAd(@PathVariable Long adId, @RequestBody Map<String, String> body, Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        double price = Double.parseDouble(body.get("price"));
        String currency = body.get("currency");
        String description = body.get("description");

        var ad = carAdService.editCarAd(user, adId, price, currency, description);
        return ResponseEntity.ok("Advertisement updated, new status: " + ad.getStatus());
    }

    @GetMapping("/stats/{adId}")
    public ResponseEntity<Map<String, Object>> getAdStats(@PathVariable Long adId, Authentication auth) {
        return ResponseEntity.ok(carAdService.getAdStats(adId, auth));
    }

    @GetMapping("/manager")
    public ResponseEntity<List<CarAdPublicResponse>> getAllInActiveCars(Authentication auth) {
        return ResponseEntity.ok(carAdService.getAllCarsManager(auth));
    }

    @DeleteMapping("/delete/{adId}")
    public ResponseEntity<HttpStatus> deleteCarAd(@PathVariable Long adId, Authentication auth) {
        carAdService.removeCarAd(adId,auth);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/manager/carAd/{adId}")
    public ResponseEntity<HttpStatus> changeStatusToActive(@PathVariable Long adId, Authentication auth) {
        carAdService.changeStatusToActive(adId,auth);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/manager/user/{id}")
    public ResponseEntity<HttpStatus> banUserById(@PathVariable Long id, Authentication auth) {
        userService.banUser(id,auth);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
