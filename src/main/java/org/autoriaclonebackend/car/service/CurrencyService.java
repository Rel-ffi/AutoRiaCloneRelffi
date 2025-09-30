package org.autoriaclonebackend.car.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Double> ratesUAH = new HashMap<>();

    public Map<String, Double> fetchExchangeRates() {
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        String response = restTemplate.getForObject(url, String.class);

        Map<String, Double> rates = new HashMap<>();
        if (response != null) {
            try {
                JsonNode arr = objectMapper.readTree(response);
                for (JsonNode obj : arr) {
                    String ccy = obj.get("ccy").asText();
                    double sale = obj.get("sale").asDouble();
                    rates.put(ccy, sale);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error when receiving currency rates", e);
            }
        }
        rates.put("UAH", 1.0);
        return rates;
    }

    public Map<String, Object> getPricesInAllCurrencies(double price, String baseCurrency) {
        if (ratesUAH.isEmpty()) {
            ratesUAH = fetchExchangeRates();
        }

        Map<String, Double> prices = new HashMap<>();
        Map<String, Double> usedRates = new HashMap<>();

        switch (baseCurrency) {
            case "USD":
                prices.put("USD", price);
                prices.put("UAH", price * ratesUAH.get("USD"));
                prices.put("EUR", price * ratesUAH.get("USD") / ratesUAH.get("EUR"));
                usedRates.put("USD->UAH", ratesUAH.get("USD"));
                usedRates.put("USD->EUR", ratesUAH.get("USD") / ratesUAH.get("EUR"));
                break;
            case "EUR":
                prices.put("EUR", price);
                prices.put("UAH", price * ratesUAH.get("EUR"));
                prices.put("USD", price * ratesUAH.get("EUR") / ratesUAH.get("USD"));
                usedRates.put("EUR->UAH", ratesUAH.get("EUR"));
                usedRates.put("EUR->USD", ratesUAH.get("EUR") / ratesUAH.get("USD"));
                break;
            case "UAH":
                prices.put("UAH", price);
                prices.put("USD", price / ratesUAH.get("USD"));
                prices.put("EUR", price / ratesUAH.get("EUR"));
                usedRates.put("UAH->USD", 1 / ratesUAH.get("USD"));
                usedRates.put("UAH->EUR", 1 / ratesUAH.get("EUR"));
                break;
            default:
                throw new RuntimeException("Unknown currency: " + baseCurrency);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("basePrice", price);
        result.put("baseCurrency", baseCurrency);
        result.put("prices", prices);
        result.put("ratesUsed", usedRates);

        return result;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRatesDaily() {
        ratesUAH = fetchExchangeRates();
    }

    public void initRates() {
        ratesUAH = fetchExchangeRates();
    }
}
