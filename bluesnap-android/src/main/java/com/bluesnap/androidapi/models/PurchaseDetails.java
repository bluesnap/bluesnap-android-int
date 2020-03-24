package com.bluesnap.androidapi.models;


import androidx.annotation.Nullable;

/**
 * Purchase Details for Tokenization
 */
public class PurchaseDetails {

    @Nullable
    private ShippingContactInfo shippingContactInfo;
    private BillingContactInfo billingContactInfo;
    private CreditCard creditCard;
    private boolean storeCard = false;

    public PurchaseDetails() {
    }

    public PurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo, boolean storeCard) {
        setPurchaseDetails(creditCard, billingContactInfo, null, storeCard);
    }

    public PurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo, ShippingContactInfo shippingContactInfo, boolean storeCard) {
        setPurchaseDetails(creditCard, billingContactInfo, shippingContactInfo, storeCard);
    }

    /**
     * set Purchase Details
     *
     * @param creditCard          - {@link CreditCard}
     * @param billingContactInfo  - {@link BillingContactInfo}
     * @param shippingContactInfo - {@link ShippingContactInfo}
     */
    public void setPurchaseDetails(CreditCard creditCard, BillingContactInfo billingContactInfo, @Nullable ShippingContactInfo shippingContactInfo, boolean storeCard) {
        setCreditCard(creditCard);
        setBillingContactInfo(billingContactInfo);
        setShippingContactInfo(shippingContactInfo);
        setStoreCard(storeCard);
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

    public boolean getStoreCard() {
        return storeCard;
    }

    /**
     * set Credit Card
     *
     * @param storeCard - store card value
     */
    public void setStoreCard(boolean storeCard) {
        this.storeCard = storeCard;
    }
}
