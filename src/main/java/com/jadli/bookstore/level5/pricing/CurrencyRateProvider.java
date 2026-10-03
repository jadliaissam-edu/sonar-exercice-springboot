package com.jadli.bookstore.level5.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyRateProvider {

    private static CurrencyRateProvider instance;

    private static final Map<String, Double> RATES = Map.of("EUR", 1.0, "USD", 1.08, "GBP", 0.85);

    private volatile List<String> supported = new ArrayList<>(RATES.keySet());

    private CurrencyRateProvider() {
    }

    public static CurrencyRateProvider getInstance() {
        if (instance == null) {
            synchronized (CurrencyRateProvider.class) {
                if (instance == null) {
                    instance = new CurrencyRateProvider();
                }
            }
        }
        return instance;
    }

    public double convert(double euros, String currency) {
        Double rate = RATES.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency " + currency);
        }
        return euros * rate;
    }

    public List<String> supportedCurrencies() {
        return supported;
    }
}
