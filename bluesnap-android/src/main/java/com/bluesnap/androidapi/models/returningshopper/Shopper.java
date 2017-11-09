package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A representation of server exchange rate.
 */
public class Shopper {
    private static final String TAG = Shopper.class.getSimpleName();
    private static final String VAULTEDSHOPPERID = "vaultedShopperId";
    private static final String SHOPPERCURRENCY = "shopperCurrency";
    private static final String PAYMENTSOURCES = "paymentSources";
    private static final String SHIPPINGCONTACTINFO = "shippingContactInfo";
    private static final String LASTPAYMENTINFO = "lastPaymentInfo";

    private int vaultedShopperId;
    @Nullable
    private ContactInfo contactInfo;
    @Nullable
    private PaymentSources paymentSources;
    @Nullable
    private ContactInfo shippingContactInfo;
    @Nullable
    private LastPaymentInfo lastPaymentInfo;
    private String shopperCurrency;

    public Shopper(@Nullable JSONObject shopper) {
        if (null != shopper) {
            try {
                vaultedShopperId = (int) shopper.get(VAULTEDSHOPPERID);
                shopperCurrency = (String) shopper.get(SHOPPERCURRENCY);
            } catch (JSONException e) {
                Log.e(TAG, "json parsing exception", e);
            }
        }

        contactInfo = new ContactInfo(shopper);
        shippingContactInfo = new ContactInfo((JSONObject) AndroidUtil.getObjectFromJsonObject(shopper, SHIPPINGCONTACTINFO, TAG));
        lastPaymentInfo = new LastPaymentInfo((JSONObject) AndroidUtil.getObjectFromJsonObject(shopper, LASTPAYMENTINFO, TAG));
        paymentSources = new PaymentSources((JSONObject) AndroidUtil.getObjectFromJsonObject(shopper, PAYMENTSOURCES, TAG));
    }

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    @Nullable
    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(@Nullable ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Nullable
    public ContactInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    @Nullable
    public PaymentSources getPaymentSources() {
        return paymentSources;
    }

    public void setPaymentSources(@Nullable PaymentSources paymentSources) {
        this.paymentSources = paymentSources;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    @Nullable
    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(@Nullable LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }
}
