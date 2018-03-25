package com.bluesnap.androidapi.models;


import android.support.annotation.Nullable;

/**
 * Purchase Details for Tokenization
 */
public class PurchaseDetails {

    @Nullable
    private ShippingInfo shippingContactInfo;
    private BillingInfo billingContactInfo;
    private CreditCard creditCard;

    public PurchaseDetails() {
    }

    public PurchaseDetails(CreditCard creditCard, BillingInfo billingContactInfo) {
        setPurchaseDetails(creditCard, billingContactInfo, null);
    }

    public PurchaseDetails(CreditCard creditCard, BillingInfo billingContactInfo, ShippingInfo shippingContactInfo) {
        setPurchaseDetails(creditCard, billingContactInfo, shippingContactInfo);
    }

    /**
     * set Purchase Details
     *
     * @param creditCard          - {@link CreditCard}
     * @param billingContactInfo  - {@link BillingInfo}
     * @param shippingContactInfo - {@link ShippingInfo}
     */
    public void setPurchaseDetails(CreditCard creditCard, BillingInfo billingContactInfo, @Nullable ShippingInfo shippingContactInfo) {
        setCreditCard(creditCard);
        setBillingContactInfo(billingContactInfo);
        setShippingContactInfo(shippingContactInfo);
    }

    @Nullable
    public ShippingInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    /**
     * set Shipping Contact Info
     *
     * @param shippingContactInfo - @Nullable {@link ShippingInfo}
     */
    public void setShippingContactInfo(@Nullable ShippingInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    public BillingInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    /**
     * set Billing Contact Info
     *
     * @param billingContactInfo - {@link BillingInfo}
     */
    public void setBillingContactInfo(BillingInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * set Credit Card
     *
     * @param creditCard - {@link CreditCard}
     */
    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

}
