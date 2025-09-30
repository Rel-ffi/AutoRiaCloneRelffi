package org.autoriaclonebackend;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.service.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class AutoRiaCloneBackendApplication implements CommandLineRunner {

    private final CurrencyService currencyService;

    public static void main(String[] args) {
        SpringApplication.run(AutoRiaCloneBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        currencyService.initRates();
    }
}
