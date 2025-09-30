package org.autoriaclonebackend.car.service;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.model.CarAd;
import org.autoriaclonebackend.car.model.Region;
import org.autoriaclonebackend.car.repository.CarAdRepository;
import org.autoriaclonebackend.car.repository.CarAdViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;


@Service
@RequiredArgsConstructor
public class CarAdStatsService {


    private final CarAdViewRepository viewRepository;
    private final CarAdRepository carAdRepository;
    private final CurrencyService currencyService;


    // Просмотры
    public long getTotalViews(CarAd ad) {
        return viewRepository.findByCarAd(ad).size();
    }


    public long getViewsLastDays(CarAd ad, int days) {
        return viewRepository.findByCarAdAndViewedAtAfter(ad, java.time.LocalDateTime.now().minusDays(days)).size();
    }


    // Средние цены
    public double getAveragePriceByRegion(String modelName, Region region) {
        List<CarAd> ads = carAdRepository.findAll();
        OptionalDouble avg = ads.stream()
                .filter(ad -> ad.getModel().getName().equals(modelName))
                .filter(ad -> ad.getRegion().equals(region))
                .mapToDouble(CarAd::getPrice)
                .average();
        return avg.orElse(0);
    }


    public double getAveragePriceByCountry(String modelName) {
        List<CarAd> ads = carAdRepository.findAll();
        OptionalDouble avg = ads.stream()
                .filter(ad -> ad.getModel().getName().equals(modelName))
                .mapToDouble(CarAd::getPrice)
                .average();
        return avg.orElse(0);
    }

}