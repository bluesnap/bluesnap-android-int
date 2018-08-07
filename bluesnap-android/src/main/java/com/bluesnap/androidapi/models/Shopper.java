package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bluesnap.androidapi.utils.JsonParser;
import org.json.JSONObject;

/**
 * A representation of server exchange rate.
 */
public class Shopper extends ContactInfo {

    private static final String TAG = Shopper.class.getSimpleName();

    //@SerializedName("vaultedShopperId")
    private int vaultedShopperId;
    @Nullable
    //@SerializedName("email")
    private String email;
    @Nullable
    //@SerializedName("address")
    private String address;
    @Nullable
    //@SerializedName("phone")
    private String phone;
    //@SerializedName("shopperCurrency")
    private String shopperCurrency;
    @Nullable
    //@SerializedName("paymentSources")
    private PaymentSources previousPaymentSources;
    @Nullable
    //@SerializedName("shippingContactInfo")
    private ShippingContactInfo shippingContactInfo;
    @Nullable
    //@SerializedName("lastPaymentInfo")
    private LastPaymentInfo lastPaymentInfo;

    private CreditCardInfo newCreditCardInfo;

    public CreditCardInfo getNewCreditCardInfo() {
        return newCreditCardInfo;
    }

    public void setNewCreditCardInfo(CreditCardInfo newCreditCardInfo) {
        this.newCreditCardInfo = newCreditCardInfo;
    }

    public Shopper() {
        shippingContactInfo = new ShippingContactInfo();
        newCreditCardInfo = new CreditCardInfo();
    }

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    @Nullable
    public PaymentSources getPreviousPaymentSources() {
        return previousPaymentSources;
    }

    public void setPreviousPaymentSources(@Nullable PaymentSources previousPaymentSources) {
        this.previousPaymentSources = previousPaymentSources;
    }

    @NonNull
    public ShippingContactInfo getShippingContactInfo() {
        if (null == shippingContactInfo)
            shippingContactInfo = new ShippingContactInfo();
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ShippingContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable BillingContactInfo billingContactInfo) {
        if (shippingContactInfo == null || billingContactInfo == null) {
            Log.w(TAG, "Cannot setShippingContactInfo, either shipping or billing is null");
        } else {
            this.shippingContactInfo.setFullName(billingContactInfo.getFullName());
            this.shippingContactInfo.setAddress(billingContactInfo.getAddress());
            this.shippingContactInfo.setAddress2(billingContactInfo.getAddress2());
            this.shippingContactInfo.setZip(billingContactInfo.getZip());
            this.shippingContactInfo.setCity(billingContactInfo.getCity());
            this.shippingContactInfo.setState(billingContactInfo.getState());
            this.shippingContactInfo.setCountry(billingContactInfo.getCountry());
        }
    }

    @Nullable
    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(@Nullable LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }

    @Nullable
    public static Shopper fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Shopper shopper = new Shopper();
        shopper.setFirstName(JsonParser.getOptionalString(jsonObject, "firstName"));
        shopper.setLastName(JsonParser.getOptionalString(jsonObject, "lastName"));
        shopper.setEmail(JsonParser.getOptionalString(jsonObject, "email"));
        shopper.setCountry(JsonParser.getOptionalString(jsonObject, "country"));
        shopper.setState(JsonParser.getOptionalString(jsonObject, "state"));
        shopper.setAddress(JsonParser.getOptionalString(jsonObject, "address"));
        shopper.setAddress2(JsonParser.getOptionalString(jsonObject, "address2"));
        shopper.setCity(JsonParser.getOptionalString(jsonObject, "city"));
        shopper.setZip(JsonParser.getOptionalString(jsonObject, "zip"));
        shopper.setShopperCurrency(JsonParser.getOptionalString(jsonObject, "shopperCurrency"));

        shopper.setLastPaymentInfo(LastPaymentInfo.fromJson(JsonParser.getOptionalObject(jsonObject, "lastPaymentInfo")));
        shopper.setPreviousPaymentSources(PaymentSources.fromJson(JsonParser.getOptionalObject(jsonObject, "paymentSources")));

        if (shopper.previousPaymentSources != null && shopper.previousPaymentSources.getPreviousCreditCardInfos() != null && shopper.previousPaymentSources.getPreviousCreditCardInfos().size() > 0)
            shopper.setNewCreditCardInfo(shopper.previousPaymentSources.getPreviousCreditCardInfos().get(0));


        return shopper;
    }
}
