package com.bluesnap.androidapi.models;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class Rates {
    //@SerializedName("baseCurrency")
    private String merchantStoreCurrency;
    //@SerializedName("baseCurrencyName")
    private String merchantStoreCurrencyName;
    //@SerializedName("exchangeRate")
    private ArrayList<Currency> currencies;

    // internal c'tor for tests
    public Rates(ArrayList<Currency> currencies, String merchantStoreCurrency, String merchantStoreCurrencyName) {
        this.currencies = currencies;
        this.merchantStoreCurrency = merchantStoreCurrency;
        this.merchantStoreCurrencyName = merchantStoreCurrencyName;
    }

    public Currency getCurrencyByCode(String currencyCode) {

        if (this.merchantStoreCurrency.equals(currencyCode)) {
            Currency baseCurrency = new Currency();
            baseCurrency.setConversionRate(1.0);
            baseCurrency.setQuoteCurrency(this.merchantStoreCurrency);
            baseCurrency.setQuoteCurrencyName(this.merchantStoreCurrencyName);
            return baseCurrency;
        }
        for (Currency r : currencies) {
            if (r.getQuoteCurrency().equalsIgnoreCase(currencyCode)) {
                return r;
            }
        }
        return null;
    }

    public String getMerchantStoreCurrency() {
        return merchantStoreCurrency;
    }

    public String getMerchantStoreCurrencyName() {
        return merchantStoreCurrencyName;
    }

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    public Set<String> getCurrencyCodes() {
        Set<String> result = new HashSet<>();
        result.add(this.merchantStoreCurrency);
        for (Currency r : currencies) {
            result.add(r.getQuoteCurrency());
        }
        return result;
    }
}
