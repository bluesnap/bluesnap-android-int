package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * A representation of server exchange rate.
 */
public class PaymentSources {

    @Nullable
    //@SerializedName("creditCardInfo")
    private ArrayList<CreditCardInfo> previousCreditCardInfos;

    @Nullable
    public ArrayList<CreditCardInfo> getPreviousCreditCardInfos() {
        return previousCreditCardInfos;
    }

    public void setPreviousCreditCardInfos(@Nullable ArrayList<CreditCardInfo> previousCreditCardInfos) {
        this.previousCreditCardInfos = previousCreditCardInfos;
    }
}
