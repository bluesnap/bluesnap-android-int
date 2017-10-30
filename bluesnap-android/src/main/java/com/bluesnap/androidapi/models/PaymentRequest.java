package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

/**
 * A Request for payment process in the SDK.
 * A new PaymentRequest should be used for each purchase.
 */
public class PaymentRequest implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel parcel) {
            PaymentRequest pr = new PaymentRequest(parcel);
            return pr;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };
    private String currencyNameCode;
    private Double amount;
    private String customTitle;
    private String userEmail;
    private boolean shippingRequired;
    private boolean billingRequired;
    private boolean emailRequired;
    private String shopperID;
    private Double subtotalAmount;
    private Double taxAmount;
    private String baseCurrency;
    private Double baseAmount;
    private Double baseTaxAmount;
    private Double baseSubtotalAmount;

    public PaymentRequest(Parcel parcel) {
        currencyNameCode = parcel.readString();
        amount = parcel.readDouble();
        customTitle = parcel.readString();
        userEmail = parcel.readString();
        shippingRequired = parcel.readInt() != 0;
        billingRequired = parcel.readInt() != 0;
        emailRequired = parcel.readInt() != 0;
        shopperID = parcel.readString();
        subtotalAmount = parcel.readDouble();
        taxAmount = parcel.readDouble();
        setBase();
    }

    public PaymentRequest() {

    }

    public PaymentRequest(String currencyNameCode) {
        setCurrencyNameCode(currencyNameCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(currencyNameCode);
        parcel.writeDouble(amount);
        parcel.writeString(customTitle);
        parcel.writeString(userEmail);
        parcel.writeInt(shippingRequired ? 1 : 0);
        parcel.writeInt(billingRequired ? 1 : 0);
        parcel.writeInt(emailRequired ? 1 : 0);
        parcel.writeString(shopperID);
        parcel.writeDouble(subtotalAmount != null ? subtotalAmount : 0D);
        parcel.writeDouble(taxAmount != null ? taxAmount : 0D);
    }


    public String getCurrencyNameCode() {
        return currencyNameCode;
    }

    public void setCurrencyNameCode(String currencyNameCode) {
        if (baseCurrency == null)
            baseCurrency = currencyNameCode;
        this.currencyNameCode = currencyNameCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (baseAmount == null)
            baseAmount = amount;
        this.amount = amount;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public boolean isBillingRequired() {
        return billingRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public void setBillingRequired(boolean billingRequired) {
        this.billingRequired = billingRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public String getShopperID() {
        return shopperID;
    }


    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        if (baseSubtotalAmount == null)
            baseSubtotalAmount = subtotalAmount;

        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        if (baseTaxAmount == null)
            baseTaxAmount = taxAmount;

        this.taxAmount = taxAmount;
    }

    public void setAmountWithTax(Double subtotalAmount, Double taxAmount) {
        this.taxAmount = taxAmount;
        setSubtotalAmount(subtotalAmount);
        setAmount(subtotalAmount + taxAmount);
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public Double getBaseTaxAmount() {
        return baseTaxAmount;
    }

    public Double getBaseSubtotalAmount() {
        return baseSubtotalAmount;
    }


    public boolean verify() throws BSPaymentRequestException {
        if (amount == null)
            throw new BSPaymentRequestException("Invalid amount");
        if (amount <= 0)
            throw new BSPaymentRequestException(String.format("Invalid amount %f", amount));
        if (currencyNameCode == null)
            throw new BSPaymentRequestException("Invalid currency");

        return true;
    }

    public boolean isSubtotalTaxSet() {
        return (baseSubtotalAmount != 0D && baseTaxAmount != 0D);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentRequest that = (PaymentRequest) o;

        if (shippingRequired != that.shippingRequired) return false;
        if (billingRequired != that.billingRequired) return false;
        if (emailRequired != that.emailRequired) return false;
        if (!currencyNameCode.equals(that.currencyNameCode)) return false;
        if (!amount.equals(that.amount)) return false;
        if (customTitle != null ? !customTitle.equals(that.customTitle) : that.customTitle != null)
            return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null)
            return false;
        if (shopperID != null ? !shopperID.equals(that.shopperID) : that.shopperID != null)
            return false;
        if (subtotalAmount != null ? !subtotalAmount.equals(that.subtotalAmount) : that.subtotalAmount != null)
            return false;
        if (taxAmount != null ? !taxAmount.equals(that.taxAmount) : that.taxAmount != null)
            return false;
        if (baseCurrency != null ? !baseCurrency.equals(that.baseCurrency) : that.baseCurrency != null)
            return false;
        if (baseAmount != null ? !baseAmount.equals(that.baseAmount) : that.baseAmount != null)
            return false;
        return baseTaxAmount != null ? baseTaxAmount.equals(that.baseTaxAmount) : that.baseTaxAmount == null;

    }

    @Override
    public int hashCode() {
        int result = currencyNameCode != null ? currencyNameCode.hashCode() : 0;
        result = 31 * result + amount.hashCode();
        result = 31 * result + (customTitle != null ? customTitle.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (shippingRequired ? 1 : 0);
        result = 31 * result + (billingRequired ? 1 : 0);
        result = 31 * result + (emailRequired ? 1 : 0);
        result = 31 * result + (shopperID != null ? shopperID.hashCode() : 0);
        result = 31 * result + (subtotalAmount != null ? subtotalAmount.hashCode() : 0);
        result = 31 * result + (taxAmount != null ? taxAmount.hashCode() : 0);
        result = 31 * result + (baseCurrency != null ? baseCurrency.hashCode() : 0);
        result = 31 * result + (baseAmount != null ? baseAmount.hashCode() : 0);
        result = 31 * result + (baseTaxAmount != null ? baseTaxAmount.hashCode() : 0);
        result = 31 * result + (baseSubtotalAmount != null ? baseSubtotalAmount.hashCode() : 0);
        return result;
    }

    public void setBase() {
        //Set these values once to remember the base currency values
        baseCurrency = currencyNameCode;
        baseAmount = amount;
        baseTaxAmount = taxAmount;
        baseSubtotalAmount = subtotalAmount;
    }
}
