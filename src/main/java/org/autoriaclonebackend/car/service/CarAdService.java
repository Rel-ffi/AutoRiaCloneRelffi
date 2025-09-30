package org.autoriaclonebackend.car.service;


import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.dto.CarAdPublicResponse;
import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.model.CarBrand;
import org.autoriaclonebackend.car.model.CarModel;
import org.autoriaclonebackend.car.model.Region;
import org.autoriaclonebackend.car.repository.CarAdRepository;
import org.autoriaclonebackend.car.repository.CarBrandRepository;
import org.autoriaclonebackend.car.repository.CarModelRepository;
import org.autoriaclonebackend.car.repository.RegionRepository;
import org.autoriaclonebackend.car.util.ProfanityService;
import org.autoriaclonebackend.files.FileStorageService;
import org.autoriaclonebackend.user.model.User;
import org.autoriaclonebackend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CarAdService {


    private final CarAdRepository carAdRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final ProfanityService profanityService;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final CarAdStatsService carAdStatsService;
    private final FileStorageService fileStorageService;
    private final CarAdPublicService carAdPublicService;

    @Transactional
    public CarAd createCarAd(User seller,
                             String brandName,
                             String modelName,
                             Double price,
                             String currency,
                             String description,
                             String regionName,
                             List<MultipartFile> images) {

        List<CarAd> sellerAds = carAdRepository.findBySeller(seller);
        boolean isPremium = seller.getRoles().stream().anyMatch(r -> r.getName().equals("PREMIUM"));
        if (!isPremium && !sellerAds.isEmpty()) {
            throw new RuntimeException("A basic account can only post one advertisement.");
        }

        if (brandName == null || brandName.isBlank()) {
            throw new RuntimeException("Brand can't be blank");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new RuntimeException("Model can't be blank");
        }
        if (price == null || price <= 0) {
            throw new RuntimeException("Price must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new RuntimeException("Currency can't be blank");
        }

        List<String> imagePaths = images.stream().map(fileStorageService::save)
                .toList();

        if (imagePaths == null || imagePaths.isEmpty()) {
            throw new RuntimeException("Images can't be blank");
        }

        CarBrand brand = carBrandRepository.findByName(brandName)
                .orElseGet(() -> carBrandRepository.save(CarBrand.builder().name(brandName).build()));

        CarModel model = carModelRepository.findByNameAndBrand(modelName, brand)
                .orElseGet(() -> carModelRepository.save(CarModel.builder().name(modelName).brand(brand).build()));

        Region region = null;
        if (regionName != null && !regionName.isBlank()) {
            region = regionRepository.findByName(regionName)
                    .orElseGet(() -> regionRepository.save(Region.builder().name(regionName).build()));
        }

        String status = profanityService.containsProfanity(description) ? "PENDING" : "ACTIVE";

        CarAd ad = CarAd.builder()
                .seller(seller)
                .brand(brand)
                .model(model)
                .region(region)
                .price(price)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .status(status)
                .editAttempts(0)
                .description(description)
                .images(imagePaths)
                .build();

        return carAdRepository.save(ad);
    }


    @Transactional
    public CarAd editCarAd(User seller, Long adId, double newPrice, String newCurrency, String newDescription) {
        CarAd ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));


        if (!ad.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You cannot edit someone else's ad");
        }


        if (ad.getEditAttempts() >= 3) {
            ad.setStatus("INACTIVE");

            carAdRepository.save(ad);

            return ad;
        }


        ad.setPrice(newPrice);
        ad.setCurrency(newCurrency);
        ad.setDescription(newDescription);
        ad.setEditAttempts(ad.getEditAttempts() + 1);
        ad.setStatus(profanityService.containsProfanity(newDescription) ? "PENDING" : "ACTIVE");


        return carAdRepository.save(ad);
    }

    public Map<String,Object> getAdStats(Long adId, Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream().anyMatch(r -> r.getName().equals("PREMIUM"))) {
            return Map.of("error", "Only available for premium accounts");
        }

        var ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        long totalViews = carAdStatsService.getTotalViews(ad);
        long viewsDay = carAdStatsService.getViewsLastDays(ad, 1);
        long viewsWeek = carAdStatsService.getViewsLastDays(ad, 7);
        long viewsMonth = carAdStatsService.getViewsLastDays(ad, 30);

        String modelName = ad.getModel().getName();

        double avgRegion = carAdStatsService.getAveragePriceByRegion(modelName, ad.getRegion());
        double avgCountry = carAdStatsService.getAveragePriceByCountry(modelName);

        return Map.of(
                "totalViews", totalViews,
                "viewsDay", viewsDay,
                "viewsWeek", viewsWeek,
                "viewsMonth", viewsMonth,
                "averagePriceRegion", avgRegion,
                "averagePriceCountry", avgCountry
        );
    }

    public List<CarAdPublicResponse> getAllCarsManager(Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("MANAGER") || r.getName().equals("ADMIN"))) {
            throw new RuntimeException("Roles MANAGER or ADMIN not found");
        }

        List<CarAd> allAds = new ArrayList<>();
        allAds.addAll(carAdRepository.findAllByStatus("INACTIVE"));
        allAds.addAll(carAdRepository.findAllByStatus("PENDING"));

        allAds.sort(Comparator.comparing(CarAd::getCreatedAt).reversed());

        return allAds.stream().map(carAdPublicService::carAdPublicResponseBuilder)
                .toList();
    }


    public void removeCarAd(Long adId, Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || !r.getName().equals("MANAGER"))) {

        }

        carAdRepository.deleteById(adId);
    }

    public void changeStatusToActive(Long adId, Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("MANAGER"))) {
            throw new RuntimeException("You don't have permissions");
        }

        var carAd = carAdRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Car advertisement not found "));

        carAd.setStatus("ACTIVE");
        carAd.setEditAttempts(0);
        carAdRepository.save(carAd);
    }

}