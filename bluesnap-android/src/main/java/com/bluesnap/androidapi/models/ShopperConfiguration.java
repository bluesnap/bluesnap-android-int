package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

public class ShopperConfiguration {

    private BillingInfo billingInfo;
    @Nullable
    private ShippingInfo shippingInfo;
    @Nullable
    private ChosenPaymentMethod chosenPaymentMethod;

    public ShopperConfiguration(BillingInfo billingInfo, @Nullable ShippingInfo shippingInfo, @Nullable ChosenPaymentMethod chosenPaymentMethod) {
        this.billingInfo = billingInfo;
        this.shippingInfo = shippingInfo;
        this.chosenPaymentMethod = chosenPaymentMethod;
    }

    public BillingInfo getBillingInfo() { return billingInfo; }

    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }

    @Nullable
    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    @Nullable
    public ChosenPaymentMethod getChosenPaymentMethod() {
        return chosenPaymentMethod;
    }

    public void setChosenPaymentMethod(ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethod = chosenPaymentMethod;
    }
}
