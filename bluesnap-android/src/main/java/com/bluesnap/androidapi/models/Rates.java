package com.bluesnap.androidapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class Rates {
    @SerializedName("baseCurrency")
    private String merchantStoreCurrency;
    @SerializedName("baseCurrencyName")
    private String merchantStoreCurrencyName;
    @SerializedName("exchangeRate")
    private ArrayList<Currency> currencies;
    private HashMap<String, Currency> ratesMap;
    private Double merchantStoreAmount;

    /**
     * Update the Conversion rates map from the server response data.
     * The rates are merchant specific, the merchantToken is used to identify the merchant.
     */
    public void setInitialRates() {
        ratesMap = new HashMap<>(currencies.size() + 1);
        Currency baseCurrency = new Currency();
        baseCurrency.setConversionRate(1.0);
        baseCurrency.setQuoteCurrency(this.merchantStoreCurrency);
        ratesMap.put(this.merchantStoreCurrency, baseCurrency);
        for (Currency r : currencies) {
            ratesMap.put(r.getQuoteCurrency(), r);
        }
    }

    public HashMap<String, Currency> getRatesMap() {
        return ratesMap;
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

    public Double getMerchantStoreAmount() {
        return merchantStoreAmount;
    }

    public void setMerchantStoreAmount(Double merchantStoreAmount) {
        this.merchantStoreAmount = merchantStoreAmount;
    }
}
