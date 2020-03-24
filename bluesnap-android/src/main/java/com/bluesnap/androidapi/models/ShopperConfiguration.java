package com.bluesnap.androidapi.models;

import androidx.annotation.Nullable;

public class ShopperConfiguration {

    private BillingContactInfo billingContactInfo;
    @Nullable
    private ShippingContactInfo shippingContactInfo;
    @Nullable
    private ChosenPaymentMethod chosenPaymentMethod;

    public ShopperConfiguration(BillingContactInfo billingContactInfo, @Nullable ShippingContactInfo shippingContactInfo, @Nullable ChosenPaymentMethod chosenPaymentMethod) {
        this.billingContactInfo = billingContactInfo;
        this.shippingContactInfo = shippingContactInfo;
        this.chosenPaymentMethod = chosenPaymentMethod;
    }

    public BillingContactInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    public void setBillingContactInfo(BillingContactInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    @Nullable
    public ShippingContactInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ShippingContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    @Nullable
    public ChosenPaymentMethod getChosenPaymentMethod() {
        return chosenPaymentMethod;
    }

    public void setChosenPaymentMethod(@Nullable ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethod = chosenPaymentMethod;
    }
}
