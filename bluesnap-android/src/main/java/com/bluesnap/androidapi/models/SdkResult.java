package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Returns the result of the payment process to the Caller.
 * This will be passed as an activityResult back to the calling activity.
 */
public class SdkResult implements Parcelable {


    public static final Creator<SdkResult> CREATOR = new Creator<SdkResult>() {
        @Override
        public SdkResult createFromParcel(Parcel in) {
            return new SdkResult(in);
        }

        @Override
        public SdkResult[] newArray(int size) {
            return new SdkResult[size];
        }
    };

    private Double amount;
    private String currencyNameCode;

    private String last4Digits;
    private String cardType;
    private String expDate;

    private String paypalInvoiceId;

    private BillingContactInfo billingContactInfo;
    private ShippingContactInfo shippingContactInfo;

    private String token;
    private String kountSessionId;

    public SdkResult() {
    }

    protected SdkResult(Parcel in) {
        setLast4Digits(in.readString());
        setAmount(in.readDouble());
        setCurrencyNameCode(in.readString());
        setCardType(in.readString());
        setExpDate(in.readString());
        setBillingContactInfo((BillingContactInfo) in.readParcelable(BillingContactInfo.class.getClassLoader()));
        setShippingContactInfo((ShippingContactInfo) in.readParcelable(ShippingContactInfo.class.getClassLoader()));
        setPaypalInvoiceId(in.readString());
        setKountSessionId(in.readString());
        setToken(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getLast4Digits());
        dest.writeDouble(getAmount());
        dest.writeString(getCurrencyNameCode());
        dest.writeString(getCardType());
        dest.writeString(getExpDate());
        dest.writeParcelable(billingContactInfo, flags);
        dest.writeParcelable(shippingContactInfo, flags);
        dest.writeString(getPaypalInvoiceId());
        dest.writeString(getKountSessionId());
        dest.writeString(getToken());
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SdkResult that = (SdkResult) o;

        if (!getLast4Digits().equals(that.getLast4Digits())) return false;
        if (!getAmount().equals(that.getAmount())) return false;
        if (!getCurrencyNameCode().equals(that.getCurrencyNameCode())) return false;
        return getCardType().equals(that.getCardType());
    }

    @Override
    public int hashCode() {
        int result = getLast4Digits().hashCode();
        result = 31 * result + getAmount().hashCode();
        result = 31 * result + getCurrencyNameCode().hashCode();
        result = 31 * result + getCardType().hashCode();
        return result;
    }

    public boolean validate() {
        if (getAmount() == null || getAmount().equals(0.0)) return false;
        if (getCurrencyNameCode() == null || getCurrencyNameCode().isEmpty()) return false;
        if (getExpDate() == null || getExpDate().isEmpty()) return false;
        return !(getLast4Digits() == null || Integer.valueOf(getLast4Digits()) == 0);
    }

    @Override
    public String toString() {
        return "SdkResult{" +
                "amount=" + getAmount() + '\'' +
                ", currencyNameCode='" + getCurrencyNameCode() + '\'' +
                ", last4Digits='" + getLast4Digits() + '\'' +
                ", cardType='" + getCardType() + '\'' +
                ", expDate='" + getExpDate() + '\'' +
                ", paypalInvoiceId=" + getPaypalInvoiceId() + '\'' +
                ", token=" + getToken() + '\'' +
                ", billingContactInfo" + billingContactInfo + '\'' +
                ", shippingContactInfo" + shippingContactInfo + '\'' +
                ", kountSessionId=" + kountSessionId + '\'' +
                '}';
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrencyNameCode() {
        return currencyNameCode;
    }

    public void setCurrencyNameCode(String currencyNameCode) {
        this.currencyNameCode = currencyNameCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public BillingContactInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    public void setBillingContactInfo(BillingContactInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    public ShippingContactInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(ShippingContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    /**
     * Returns the paypal invoice ID in case of a paypal transaction.
     *
     * @return A string representing the invoice ID on paypal. null if not a paypal transaction.
     */
    public String getPaypalInvoiceId() {
        return paypalInvoiceId;
    }

    public void setPaypalInvoiceId(String paypalInvoiceId) {
        this.paypalInvoiceId = paypalInvoiceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKountSessionId() {
        return kountSessionId;
    }

    public void setKountSessionId(String kountSessionId) {
        this.kountSessionId = kountSessionId;
    }

}
