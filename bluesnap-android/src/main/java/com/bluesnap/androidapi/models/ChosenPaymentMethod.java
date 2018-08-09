package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalObject;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;

public class ChosenPaymentMethod {
    public static final String CC = "CC";
    public static final String PAYPAL = "PAYPAL";

    @NonNull
    //@SerializedName("chosenPaymentMethodType")
    private String chosenPaymentMethodType;

    @Nullable
    //@SerializedName("creditCard")
    private CreditCard creditCard;

    public ChosenPaymentMethod() {
    }

    public ChosenPaymentMethod(@NonNull String chosenPaymentMethodType, @Nullable CreditCard creditCard) {

        this.chosenPaymentMethodType = chosenPaymentMethodType;
        this.creditCard = creditCard;
    }

    public ChosenPaymentMethod(@NonNull ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethodType = chosenPaymentMethod.chosenPaymentMethodType;
        this.creditCard = chosenPaymentMethod.creditCard == null ? null : new CreditCard(chosenPaymentMethod.creditCard);
    }

    @NonNull
    public String getChosenPaymentMethodType() {
        return chosenPaymentMethodType;
    }

    public void setChosenPaymentMethodType(@NonNull String chosenPaymentMethodType) {
        this.chosenPaymentMethodType = chosenPaymentMethodType;
    }

    @Nullable
    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(@Nullable CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    @Nullable
    public static ChosenPaymentMethod fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        ChosenPaymentMethod chosenPaymentMethod = new ChosenPaymentMethod();
        chosenPaymentMethod.setChosenPaymentMethodType(getOptionalString(jsonObject, "chosenPaymentMethodType"));
        if (CC.equals(chosenPaymentMethod.getChosenPaymentMethodType()))
            chosenPaymentMethod.setCreditCard((CreditCard.fromJson(getOptionalObject(jsonObject, "creditCard"))));

        return chosenPaymentMethod;
    }
}
