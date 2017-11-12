package com.bluesnap.androidapi.models.returningshopper;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCardInfo {

    @SerializedName("billingContactInfo")
    private BillingInfo billingContactInfo;
    @SerializedName("creditCard")
    private CreditCard creditCard;
    @SerializedName("processingInfo")
    private ProcessingInfo processingInfo;

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

    public ProcessingInfo getProcessingInfo() {
        return processingInfo;
    }

    public void setProcessingInfo(ProcessingInfo processingInfo) {
        this.processingInfo = processingInfo;
    }
}
