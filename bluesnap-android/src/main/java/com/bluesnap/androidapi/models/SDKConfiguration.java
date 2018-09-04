package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
}
