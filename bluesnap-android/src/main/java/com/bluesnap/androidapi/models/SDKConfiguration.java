package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SDKConfiguration {

    @Nullable
    //@SerializedName("shopper")
    private Shopper shopper;

    //@SerializedName("kountMerchantId")
    private int kountMerchantId;

    //@SerializedName("merchantId")
    private Long merchantId;

    //@SerializedName("rates")
    private Rates rates;

    //@SerializedName("supportedPaymentMethods")
    private SupportedPaymentMethods supportedPaymentMethods;

    //@SerializedName("threeDSecureJwt")
    private String cardinalToken;



    public int getKountMerchantId() {
        return kountMerchantId;
    }

    public void setKountMerchantId(int kountMerchantId) {
        this.kountMerchantId = kountMerchantId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    @NonNull
    public synchronized Shopper getShopper() {
        if (shopper == null)
            shopper = new Shopper();
        return shopper;
    }

    public void setShopper(@Nullable Shopper shopper) {
        this.shopper = shopper;
    }

    public Rates getRates() {
        return rates;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }

    public SupportedPaymentMethods getSupportedPaymentMethods() {
        return supportedPaymentMethods;
    }

    public void setSupportedPaymentMethods(SupportedPaymentMethods supportedPaymentMethods) {
        this.supportedPaymentMethods = supportedPaymentMethods;
    }

    public String getCardinalToken() {
        return cardinalToken;
    }

    public void setCardinalToken(String cardinalToken) {
        this.cardinalToken = cardinalToken;
    }
}
