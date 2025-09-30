package org.autoriaclonebackend.car.service;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.dto.BrandAndModelResponse;
import org.autoriaclonebackend.car.dto.CarAdPublicResponse;
import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.model.CarAdView;
import org.autoriaclonebackend.car.repository.CarAdRepository;
import org.autoriaclonebackend.car.repository.CarAdViewRepository;
import org.autoriaclonebackend.car.repository.CarModelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarAdPublicService {

    private final CarAdRepository carAdRepository;
    private final CarAdViewRepository carAdViewRepository;
    private final CurrencyService currencyService;
    private final CarModelRepository carModelRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public List<CarAdPublicResponse> getActiveCars() {
        return carAdRepository.findByStatus("ACTIVE").stream()
                .map(this::carAdPublicResponseBuilder)
                .toList();
    }

    public CarAdPublicResponse getOneCar(Long adId) {
        CarAd ad = carAdRepository.findByStatusAndId("ACTIVE", adId);
        return carAdPublicResponseBuilder(ad);
    }

    public void addNewView(Long adId) {
        CarAd carAd = carAdRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Car with id: " + adId + " not found"));

        CarAdView newView = CarAdView.builder()
                .carAd(carAd)
                .viewedAt(LocalDateTime.now())
                .build();

        carAdViewRepository.save(newView);
    }

    public Map<String,Object> getAdPublicPrices(Long adId) {
        var ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        return currencyService.getPricesInAllCurrencies(ad.getPrice(), ad.getCurrency());
    }

    public List<BrandAndModelResponse> getBrandsFlow() {
        var flow = carModelRepository.findAll();

        return flow.stream()
                .map(carModel -> BrandAndModelResponse.builder()
                        .brand(carModel.getBrand().getName())
                        .model(carModel.getName())
                        .build()
                )
                .toList();
    }

    public CarAdPublicResponse carAdPublicResponseBuilder(CarAd ad) {

        List<String> imageUrls = ad.getImages().stream()
                .map(filename -> baseUrl + "/images/" + filename)
                .toList();

        return CarAdPublicResponse.builder()
                .id(ad.getId())
                .sellerId(ad.getSeller().getId())
                .sellerName(ad.getSeller().getFullName())
                .imageUrls(imageUrls)
                .brand(ad.getBrand().getName())
                .model(ad.getModel().getName())
                .price(ad.getPrice())
                .currency(ad.getCurrency())
                .status(ad.getStatus())
                .editAttempts(ad.getEditAttempts())
                .region(ad.getRegion() != null ? ad.getRegion().getName() : null)
                .createdAt(ad.getCreatedAt())
                .description(ad.getDescription())
                .build();
    }
}
