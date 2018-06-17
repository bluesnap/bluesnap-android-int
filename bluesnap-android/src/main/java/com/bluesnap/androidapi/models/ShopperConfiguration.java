package com.bluesnap.androidapi.models;

public class ShopperConfiguration {

    private BillingInfo billingInfo;
    private ShippingInfo shippingInfo;
    private ChosenPaymentMethod chosenPaymentMethod;

    public ShopperConfiguration(BillingInfo billingInfo, ShippingInfo shippingInfo, ChosenPaymentMethod chosenPaymentMethod) {
        this.billingInfo = billingInfo;
        this.shippingInfo = shippingInfo;
        this.chosenPaymentMethod = chosenPaymentMethod;
    }

    public BillingInfo getBillingInfo() { return billingInfo; }

    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public ChosenPaymentMethod getChosenPaymentMethod() {
        return chosenPaymentMethod;
    }

    public void setChosenPaymentMethod(ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethod = chosenPaymentMethod;
    }
}
