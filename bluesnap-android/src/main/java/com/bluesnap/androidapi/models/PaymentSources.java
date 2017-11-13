package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * A representation of server exchange rate.
 */
public class PaymentSources {

    @Nullable
    @SerializedName("creditCardInfo")
    private ArrayList<CreditCardInfo> creditCardInfos;

    @Nullable
    public ArrayList<CreditCardInfo> getCreditCardInfos() {
        return creditCardInfos;
    }

    public void setCreditCardInfos(@Nullable ArrayList<CreditCardInfo> creditCardInfos) {
        this.creditCardInfos = creditCardInfos;
    }
}
