package com.bluesnap.androidapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCardInfo {

    @SerializedName("billingContactInfo")
    private BillingInfo billingContactInfo;
    @SerializedName("creditCard")
    private CreditCard creditCard;

    public CreditCardInfo() {
        creditCard = new CreditCard();
        billingContactInfo = new BillingInfo();
    }

    public BillingInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    public void setBillingContactInfo(BillingInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

}
