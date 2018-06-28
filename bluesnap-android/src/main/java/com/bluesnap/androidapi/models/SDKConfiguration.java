package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.models.Rates;
import com.bluesnap.androidapi.models.Shopper;
import com.google.gson.annotations.SerializedName;

public class SDKConfiguration {

    @Nullable
    @SerializedName("shopper")
    private Shopper shopper;

    @SerializedName("kountMerchantId")
    private int kountMerchantId;
    @SerializedName("rates")
    private Rates rates;
    @SerializedName("supportedPaymentMethods")
    private SupportedPaymentMethods supportedPaymentMethods;

    public int getKountMerchantId() {
        return kountMerchantId;
    }

    public void setKountMerchantId(int kountMerchantId) {
        this.kountMerchantId = kountMerchantId;
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
