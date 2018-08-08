package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ChosenPaymentMethod {

    @NonNull
    @SerializedName("chosenPaymentMethodType")
    private PaymentTypes chosenPaymentMethodType;

    @Nullable
    @SerializedName("creditCard")
    private CreditCard creditCard;

    public ChosenPaymentMethod(@NonNull PaymentTypes chosenPaymentMethodType, @Nullable CreditCard creditCard) {

        this.chosenPaymentMethodType = chosenPaymentMethodType;
        this.creditCard = creditCard;
    }

    public ChosenPaymentMethod(@NonNull ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethodType = chosenPaymentMethod.chosenPaymentMethodType;
        this.creditCard = chosenPaymentMethod.creditCard == null ? null : new CreditCard(chosenPaymentMethod.creditCard);
    }

    @NonNull
    public PaymentTypes getChosenPaymentMethodType() {
        return chosenPaymentMethodType;
    }

    public void setChosenPaymentMethodType(@NonNull PaymentTypes chosenPaymentMethodType) {
        this.chosenPaymentMethodType = chosenPaymentMethodType;
    }

    @Nullable
    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(@Nullable CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
