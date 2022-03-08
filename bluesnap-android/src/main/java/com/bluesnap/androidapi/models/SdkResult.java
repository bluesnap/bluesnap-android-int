package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import static com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity.BS_CHOOSE_PAYMENT_METHOD_RESULT_OK;

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

    @Nullable
    private Double amount;
    @Nullable
    private String currencyNameCode;

    private String last4Digits;
    private String cardType;
    private String expDate;

    @Nullable
    private String paypalInvoiceId;

    @Nullable
    private String googlePayToken;

    @Nullable
    private String chosenPaymentMethodType;
    private int result;

    private BillingContactInfo billingContactInfo;
    private ShippingContactInfo shippingContactInfo;

    private String token;
    private String kountSessionId;

    private String threeDSAuthenticationResult;

    public SdkResult() {
    }

    protected SdkResult(Parcel in) {
        setLast4Digits(in.readString());
        setCardType(in.readString());
        setExpDate(in.readString());
        setBillingContactInfo((BillingContactInfo) in.readParcelable(BillingContactInfo.class.getClassLoader()));
        setShippingContactInfo((ShippingContactInfo) in.readParcelable(ShippingContactInfo.class.getClassLoader()));
        setChosenPaymentMethodType(in.readString());
        setResult(in.readInt());
        setKountSessionId(in.readString());
        setGooglePayToken(in.readString());
        setToken(in.readString());
        setThreeDSAuthenticationResult(in.readString());

        if (BS_CHOOSE_PAYMENT_METHOD_RESULT_OK != getResult()) {
            setAmount(in.readDouble());
            setCurrencyNameCode(in.readString());
            setPaypalInvoiceId(in.readString());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getLast4Digits());
        dest.writeString(getCardType());
        dest.writeString(getExpDate());
        dest.writeParcelable(billingContactInfo, flags);
        dest.writeParcelable(shippingContactInfo, flags);
        dest.writeString(getChosenPaymentMethodType());
        dest.writeInt(getResult());
        dest.writeString(getKountSessionId());
        dest.writeString(getGooglePayToken());
        dest.writeString(getToken());
        dest.writeString(getThreeDSAuthenticationResult());

        if (BS_CHOOSE_PAYMENT_METHOD_RESULT_OK != getResult()) {
            if (null != getAmount())
                dest.writeDouble(getAmount());
            dest.writeString(getCurrencyNameCode());
            dest.writeString(getPaypalInvoiceId());
        }
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
        if (getAmount() != null && !getAmount().equals(that.getAmount())) return false;
        if (getCurrencyNameCode() != null && !getCurrencyNameCode().equals(that.getCurrencyNameCode()))
            return false;
        if (!getThreeDSAuthenticationResult().equals(that.getThreeDSAuthenticationResult()))
            return false;
        return getCardType().equals(that.getCardType());
    }

    @Override
    public int hashCode() {
        int result = getLast4Digits().hashCode();
        if (null != getAmount())
            result = 31 * result + getAmount().hashCode();
        if (null != getCurrencyNameCode())
            result = 31 * result + getCurrencyNameCode().hashCode();
        result = 31 * result + getCardType().hashCode();
        result = 31 * result + getThreeDSAuthenticationResult().hashCode();
        return result;
    }

    public boolean validate() {
        if (getAmount() != null && getAmount().equals(0.0)) return false;
        if (getCurrencyNameCode() != null && getCurrencyNameCode().isEmpty()) return false;
        if (getExpDate() == null || getExpDate().isEmpty()) return false;
        if (getThreeDSAuthenticationResult().isEmpty()) return false;
        return !(getLast4Digits() == null || Integer.valueOf(getLast4Digits()) == 0);
    }

    @Override
    public String toString() {
        String s = "SdkResult{" +
                ", chosenPaymentMethodType=" + getChosenPaymentMethodType() + '\'' +
                ", result=" + getResult() + '\'' +
                ", kountSessionId=" + kountSessionId + '\'' +
                ", threeDSAuthenticationResult=" + threeDSAuthenticationResult + '\'';

        if (null != amount)
            s += ", amount=" + getAmount() + '\'' +
                    ", currencyNameCode='" + getCurrencyNameCode() + '\'' +
                    ", paypalInvoiceId=" + getPaypalInvoiceId() + '\'';

        s += '}';
        return s;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    @Nullable
    public Double getAmount() {
        return amount;
    }

    public void setAmount(@Nullable Double amount) {
        this.amount = amount;
    }

    @Nullable
    public String getCurrencyNameCode() {
        return currencyNameCode;
    }

    public void setCurrencyNameCode(@Nullable String currencyNameCode) {
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
    @Nullable
    public String getPaypalInvoiceId() {
        return paypalInvoiceId;
    }

    public void setPaypalInvoiceId(@Nullable String paypalInvoiceId) {
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

    /**
     * Returns the chosen Payment Method Type For Shopper Configuration
     *
     * @return String - chosenPaymentMethodType ("CC", "PAYPAL")
     */
    @Nullable
    public String getChosenPaymentMethodType() {
        return chosenPaymentMethodType;
    }

    /**
     * Set the chosen Payment Method Type For Shopper Configuration
     *
     * @param chosenPaymentMethodType - String chosenPaymentMethodType ("CC", "PAYPAL")
     */
    public void setChosenPaymentMethodType(@Nullable String chosenPaymentMethodType) {
        this.chosenPaymentMethodType = chosenPaymentMethodType;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Nullable
    public String getGooglePayToken() {
        return googlePayToken;
    }

    public void setGooglePayToken(@Nullable String googlePayToken) {
        this.googlePayToken = googlePayToken;
    }

    public String getThreeDSAuthenticationResult() {
        return threeDSAuthenticationResult;
    }

    public void setThreeDSAuthenticationResult(String threeDSAuthenticationResult) {
        this.threeDSAuthenticationResult = threeDSAuthenticationResult;
    }
}
