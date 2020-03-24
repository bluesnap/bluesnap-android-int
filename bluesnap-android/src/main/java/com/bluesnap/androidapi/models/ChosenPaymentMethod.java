package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalObject;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

public class ChosenPaymentMethod extends BSModel {
    public static final String CC = "CC";
    public static final String PAYPAL = "PAYPAL";
    public static final String GOOGLE_PAY = "GOOGLE_PAY";
    public static final String CHOSEN_PAYMENT_METHOD_TYPE = "chosenPaymentMethodType";
    public static final String CREDIT_CARD = "creditCard";

    @NonNull
    private String chosenPaymentMethodType = "UNKNOWN";

    @Nullable
    private CreditCard creditCard;

    public ChosenPaymentMethod() {
    }

    public ChosenPaymentMethod(@NonNull String chosenPaymentMethodType) {
        this.chosenPaymentMethodType = chosenPaymentMethodType;
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
        chosenPaymentMethod.setChosenPaymentMethodType(getOptionalString(jsonObject, CHOSEN_PAYMENT_METHOD_TYPE));
        if (CC.equals(chosenPaymentMethod.getChosenPaymentMethodType()))
            chosenPaymentMethod.setCreditCard((CreditCard.fromJson(getOptionalObject(jsonObject, CREDIT_CARD))));

        return chosenPaymentMethod;
    }

    @Override
    @NonNull
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, CHOSEN_PAYMENT_METHOD_TYPE, getChosenPaymentMethodType());
        if (CC.equals(getChosenPaymentMethodType()))
            putJSONifNotNull(jsonObject, CREDIT_CARD, getCreditCard().toJson());
        return jsonObject;
    }
}
