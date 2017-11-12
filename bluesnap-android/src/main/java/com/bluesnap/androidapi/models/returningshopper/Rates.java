package com.bluesnap.androidapi.models.returningshopper;

import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class Rates {
    @SerializedName("baseCurrency")
    private String baseCurrency;
    @SerializedName("baseCurrencyName")
    private String baseCurrencyName;
    @SerializedName("exchangeRate")
    private ArrayList<Currency> currencies;
    private HashMap<String, Currency> ratesMap;

    /**
     * Update the Conversion rates map from the server response data.
     * The rates are merchant specific, the merchantToken is used to identify the merchant.
     */
    public void setInitialRates() {
        ratesMap = new HashMap<>(currencies.size() + 1);
        Currency baseCurrency = new Currency();
        baseCurrency.setConversionRate(1.0);
        baseCurrency.setQuoteCurrency(this.baseCurrency);
        ratesMap.put(this.baseCurrency, baseCurrency);
        for (Currency r : currencies) {
            ratesMap.put(r.getQuoteCurrency(), r);
        }
    }

    public HashMap<String, Currency> getRatesMap() {
        return ratesMap;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayList<Currency> currencies) {
        this.currencies = currencies;
    }
}
