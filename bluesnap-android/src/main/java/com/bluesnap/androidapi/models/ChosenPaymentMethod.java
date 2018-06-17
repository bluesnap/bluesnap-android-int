package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ChosenPaymentMethod {

    @SerializedName("chosenPaymentMethodType")
    private PaymentTypes chosenPaymentMethodType;

    @Nullable
    @SerializedName("creditCard")
    private CreditCard creditCard;

    public ChosenPaymentMethod(PaymentTypes chosenPaymentMethodType, CreditCard creditCard) {

        this.chosenPaymentMethodType = chosenPaymentMethodType;
        this.creditCard = creditCard;
    }

    public PaymentTypes getChosenPaymentMethodType() {
        return chosenPaymentMethodType;
    }

    public void setChosenPaymentMethodType(PaymentTypes chosenPaymentMethodType) {
        this.chosenPaymentMethodType = chosenPaymentMethodType;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
