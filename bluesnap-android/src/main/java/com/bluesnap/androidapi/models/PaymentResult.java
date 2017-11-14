package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Returns the result of the payment process to the Caller.
 * This will be passed as an activityResult back to the calling activity.
 */
public class PaymentResult implements Parcelable {


    public static final Creator<PaymentResult> CREATOR = new Creator<PaymentResult>() {
        @Override
        public PaymentResult createFromParcel(Parcel in) {
            return new PaymentResult(in);
        }

        @Override
        public PaymentResult[] newArray(int size) {
            return new PaymentResult[size];
        }
    };

    private Double amount;
    private String currencyNameCode;

    private String last4Digits;
    private String cardType;
    private String expDate;

    private String paypalInvoiceId;

    private String shopperID;

    private BillingInfo billingInfo;
    private ShippingInfo shippingInfo;

    private String token;
    private String kountSessionId;

    public PaymentResult() {
    }

    protected PaymentResult(Parcel in) {
        setShopperID(in.readString());
        setLast4Digits(in.readString());
        setAmount(in.readDouble());
        setCurrencyNameCode(in.readString());
        setCardType(in.readString());
        setExpDate(in.readString());
        setBillingInfo((BillingInfo) in.readParcelable(BillingInfo.class.getClassLoader()));
        setShippingInfo((ShippingInfo) in.readParcelable(ShippingInfo.class.getClassLoader()));
        setPaypalInvoiceId(in.readString());
        setKountSessionId(in.readString());
        setToken(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getShopperID());
        dest.writeString(getLast4Digits());
        dest.writeDouble(getAmount());
        dest.writeString(getCurrencyNameCode());
        dest.writeString(getCardType());
        dest.writeString(getExpDate());
        dest.writeParcelable(billingInfo, flags);
        dest.writeParcelable(shippingInfo, flags);
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

        PaymentResult that = (PaymentResult) o;

        if (!getLast4Digits().equals(that.getLast4Digits())) return false;
        if (!getAmount().equals(that.getAmount())) return false;
        if (!getCurrencyNameCode().equals(that.getCurrencyNameCode())) return false;
        if (!getShopperID().equals(that.getShopperID())) return false;
        return getCardType().equals(that.getCardType());

    }

    @Override
    public int hashCode() {
        int result = getLast4Digits().hashCode();
        result = 31 * result + getAmount().hashCode();
        result = 31 * result + getCurrencyNameCode().hashCode();
        result = 31 * result + getShopperID().hashCode();
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
        return "PaymentResult{" +
                "amount=" + getAmount() + '\'' +
                ", currencyNameCode='" + getCurrencyNameCode() + '\'' +
                ", last4Digits='" + getLast4Digits() + '\'' +
                ", cardType='" + getCardType() + '\'' +
                ", expDate='" + getExpDate() + '\'' +
                ", paypalInvoiceId=" + getPaypalInvoiceId() + '\'' +
                ", shopperID='" + getShopperID() + '\'' +
                ", token=" + getToken() + '\'' +
                ", billingInfo" + billingInfo + '\'' +
                ", shippingInfo" + shippingInfo + '\'' +
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

    public String getShopperID() {
        return shopperID;
    }

    public void setShopperID(String shopperID) {
        this.shopperID = shopperID;
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

    public BillingInfo getBillingInfo() {
        return billingInfo;
    }

    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
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
