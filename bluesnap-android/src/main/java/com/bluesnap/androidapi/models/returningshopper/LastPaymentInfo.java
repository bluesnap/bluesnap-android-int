package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by roy.biber on 07/11/2017.
 */

public class LastPaymentInfo {

    @Nullable
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @Nullable
    @SerializedName("creditCard")
    private CreditCard creditCard;

    @Nullable
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Nullable
    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(@Nullable CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
