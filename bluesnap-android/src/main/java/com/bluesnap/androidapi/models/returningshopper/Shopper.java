package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A representation of server exchange rate.
 */
public class Shopper {

    @SerializedName("vaultedShopperId")
    private int vaultedShopperId;
    @Nullable
    @SerializedName("firstName")
    private ContactInfo firstName;
    @Nullable
    @SerializedName("lastName")
    private ContactInfo lastName;
    @Nullable
    @SerializedName("email")
    private ContactInfo email;
    @Nullable
    @SerializedName("country")
    private ContactInfo country;
    @Nullable
    @SerializedName("address")
    private ContactInfo address;
    @Nullable
    @SerializedName("city")
    private ContactInfo city;
    @Nullable
    @SerializedName("zip")
    private ContactInfo zip;
    @Nullable
    @SerializedName("phone")
    private ContactInfo phone;
    @SerializedName("shopperCurrency")
    private String shopperCurrency;
    @Nullable
    @SerializedName("paymentSources")
    private PaymentSources paymentSources;
    @Nullable
    @SerializedName("shippingContactInfo")
    private ShippingInfo shippingContactInfo;
    @Nullable
    @SerializedName("lastPaymentInfo")
    private LastPaymentInfo lastPaymentInfo;

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    @Nullable
    public ContactInfo getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nullable ContactInfo firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public ContactInfo getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable ContactInfo lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public ContactInfo getEmail() {
        return email;
    }

    public void setEmail(@Nullable ContactInfo email) {
        this.email = email;
    }

    @Nullable
    public ContactInfo getCountry() {
        return country;
    }

    public void setCountry(@Nullable ContactInfo country) {
        this.country = country;
    }

    @Nullable
    public ContactInfo getAddress() {
        return address;
    }

    public void setAddress(@Nullable ContactInfo address) {
        this.address = address;
    }

    @Nullable
    public ContactInfo getCity() {
        return city;
    }

    public void setCity(@Nullable ContactInfo city) {
        this.city = city;
    }

    @Nullable
    public ContactInfo getZip() {
        return zip;
    }

    public void setZip(@Nullable ContactInfo zip) {
        this.zip = zip;
    }

    @Nullable
    public ContactInfo getPhone() {
        return phone;
    }

    public void setPhone(@Nullable ContactInfo phone) {
        this.phone = phone;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    @Nullable
    public PaymentSources getPaymentSources() {
        return paymentSources;
    }

    public void setPaymentSources(@Nullable PaymentSources paymentSources) {
        this.paymentSources = paymentSources;
    }

    @Nullable
    public ShippingInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ShippingInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    @Nullable
    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(@Nullable LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }
}
