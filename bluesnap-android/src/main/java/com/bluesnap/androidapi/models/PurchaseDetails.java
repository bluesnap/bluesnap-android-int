package com.bluesnap.androidapi.models;


import android.support.annotation.Nullable;

/**
 * Purchase Details for Tokenization
 */
public class PurchaseDetails {

    @Nullable
    private ShippingContactInfo shippingContactInfo;
    private BillingContactInfo billingContactInfo;
    private CreditCard creditCard;

    public PurchaseDetails() {
    }

    public PurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo) {
        setPurchaseDetails(creditCard, billingContactInfo, null);
    }

    public PurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo, ShippingContactInfo shippingContactInfo) {
        setPurchaseDetails(creditCard, billingContactInfo, shippingContactInfo);
    }

    /**
     * set Purchase Details
     *
     * @param creditCard          - {@link CreditCard}
     * @param billingContactInfo  - {@link BillingContactInfo}
     * @param shippingContactInfo - {@link ShippingContactInfo}
     */
    public void setPurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo, @Nullable ShippingContactInfo shippingContactInfo) {
        setCreditCard(creditCard);
        setBillingContactInfo(billingContactInfo);
        setShippingContactInfo(shippingContactInfo);
    }

    @Nullable
    public ShippingContactInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    /**
     * set Shipping Contact Info
     *
     * @param shippingContactInfo - @Nullable {@link ShippingContactInfo}
     */
    public void setShippingContactInfo(@Nullable ShippingContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    public BillingContactInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    /**
     * set Billing Contact Info
     *
     * @param billingContactInfo - {@link BillingContactInfo}
     */
    public void setBillingContactInfo(BillingContactInfo billingContactInfo) {
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
