package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class LastPaymentInfo {
    private static final String TAG = LastPaymentInfo.class.getSimpleName();
    private static final String PAYMENTMETHOD = "paymentMethod";
    private static final String CREDITCARD = "creditCard";

    private String paymentMethod;
    @Nullable
    private CreditCard creditCard;

    public LastPaymentInfo(JSONObject lastPaymentInfoRepresentation) {
        paymentMethod = (String) AndroidUtil.getObjectFromJsonObject(lastPaymentInfoRepresentation, PAYMENTMETHOD, TAG);
        creditCard = new CreditCard((JSONObject) AndroidUtil.getObjectFromJsonObject(lastPaymentInfoRepresentation, CREDITCARD, TAG));

    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
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
