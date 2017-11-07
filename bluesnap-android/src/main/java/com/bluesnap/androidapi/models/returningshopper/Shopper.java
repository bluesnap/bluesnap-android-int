package com.bluesnap.androidapi.models.returningshopper;

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
    private static final String CREDITCARDINFO = "creditCardInfo";
    private static final String SHIPPINGCONTACTINFO = "shippingContactInfo";
    private static final String LASTPAYMENTINFO = "lastPaymentInfo";

    private int vaultedShopperId;
    private ContactInfo contactInfo;
    @Nullable
    private ArrayList<CreditCardInfo> creditCardInfos;
    @Nullable
    private ContactInfo shippingContactInfo;
    @Nullable
    private LastPaymentInfo lastPaymentInfo;
    private String shopperCurrency;

    public Shopper(JSONObject shopperRepresentation) {
        vaultedShopperId = (int) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, VAULTEDSHOPPERID, TAG);
        contactInfo = new ContactInfo(shopperRepresentation);
        shippingContactInfo = new ContactInfo((JSONObject) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, SHIPPINGCONTACTINFO, TAG));
        shopperCurrency = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, SHOPPERCURRENCY, TAG);
        lastPaymentInfo = new LastPaymentInfo((JSONObject) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, LASTPAYMENTINFO, TAG));
        try {
            if (creditCardInfos != null)
                creditCardInfos.clear();
            setCreditCardInfos(shopperRepresentation);
        } catch (JSONException e) {
            Log.e(TAG, "json parsing exception", e);
        }

    }

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Nullable
    public ArrayList<CreditCardInfo> getCreditCardInfos() {
        return creditCardInfos;
    }

    public void setCreditCardInfos(@Nullable JSONObject shopperRepresentation) throws JSONException {
        if (null != shopperRepresentation && !shopperRepresentation.isNull(PAYMENTSOURCES) && !shopperRepresentation.getJSONObject(PAYMENTSOURCES).isNull(CREDITCARDINFO)) {
            JSONArray creditCardInfo = (JSONArray) AndroidUtil.getObjectFromJsonObject((JSONObject) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, PAYMENTSOURCES, TAG), CREDITCARDINFO, TAG);
            assert creditCardInfo != null;
            for (int i = 0; i < creditCardInfo.length(); i++) {
                assert creditCardInfos != null;
                creditCardInfos.add(new CreditCardInfo(creditCardInfo.getJSONObject(i)));
            }
        }

    }

    @Nullable
    public ContactInfo getShippingContactInfo() {
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
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
