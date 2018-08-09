package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluesnap.androidapi.services.BlueSnapValidator;

import org.json.JSONException;
import org.json.JSONObject;

public class BSTokenizeDetailsJsonFactory {
    public static final String BILLINGFIRSTNAME = "billingFirstName";
    public static final String BILLINGLASTNAME = "billingLastName";
    public static final String BILLINGCOUNTRY = "billingCountry";
    public static final String BILLINGSTATE = "billingState";
    public static final String BILLINGCITY = "billingCity";
    public static final String BILLINGADDRESS = "billingAddress";
    public static final String BILLINGZIP = "billingZip";
    public static final String EMAIL = "email";

    public static final String CCNUMBER = "ccNumber";
    public static final String CVV = "cvv";
    public static final String EXPDATE = "expDate";
    public static final String LAST4DIGITS = "lastFourDigits";
    public static final String CARDTYPE = "ccType";

    public static final String SHIPPINGFIRSTNAME = "shippingFirstName";
    public static final String SHIPPINGLASTNAME = "shippingLastName";
    public static final String SHIPPINGCOUNTRY = "shippingCountry";
    public static final String SHIPPINGSTATE = "shippingState";
    public static final String SHIPPINGCITY = "shippingCity";
    public static final String SHIPPINGADDRESS = "shippingAddress";
    public static final String SHIPPINGZIP = "shippingZip";
    public static final String PHONE = "phone";

    private static final String FRAUDSESSIONID = "fraudSessionId";

    /**
     * @param creditCard          {@link CreditCard}
     * @param billingContactInfo  {@link BillingContactInfo}
     * @param shippingContactInfo {@link ShippingContactInfo}
     * @return {@link JSONObject} representation for api put call for the server
     * @throws JSONException in case of invalid JSON object (should not happen)
     */
    public static JSONObject createDataObject(@NonNull ShopperInfoConfig shopperInfoConfig, @NonNull CreditCard creditCard, @NonNull BillingContactInfo billingContactInfo, @Nullable ShippingContactInfo shippingContactInfo, @Nullable String kountSessionId) throws JSONException {
        JSONObject postData = new JSONObject();

        if (creditCard.getIsNewCreditCard()) {
            postData.put(CCNUMBER, creditCard.getNumber());
            postData.put(CVV, creditCard.getCvc());
            postData.put(EXPDATE, creditCard.getExpirationDate());
        } else {
            postData.put(CARDTYPE, creditCard.getCardType());
            postData.put(LAST4DIGITS, creditCard.getCardLastFourDigits());

        }

        postData.put(BILLINGFIRSTNAME, billingContactInfo.getFirstName());
        postData.put(BILLINGLASTNAME, billingContactInfo.getLastName());
        postData.put(BILLINGCOUNTRY, billingContactInfo.getCountry());

        if (null != billingContactInfo.getZip() && !"".equals(billingContactInfo.getZip()))
            postData.put(BILLINGZIP, billingContactInfo.getZip());

        if (shopperInfoConfig.isBillingRequired()) {
            if (BlueSnapValidator.checkCountryHasState(billingContactInfo.getCountry()))
                postData.put(BILLINGSTATE, billingContactInfo.getState());
            postData.put(BILLINGCITY, billingContactInfo.getCity());
            postData.put(BILLINGADDRESS, billingContactInfo.getAddress());
        }

        if (shopperInfoConfig.isEmailRequired())
            postData.put(EMAIL, billingContactInfo.getEmail());

        //postData.put(PHONE, creditCardInfo.getBillingContactInfo().getPhone());

        if (shopperInfoConfig.isShippingRequired() || null != shippingContactInfo) {
            postData.put(SHIPPINGFIRSTNAME, shippingContactInfo.getFirstName());
            postData.put(SHIPPINGLASTNAME, shippingContactInfo.getLastName());
            postData.put(SHIPPINGCOUNTRY, shippingContactInfo.getCountry());
            if (BlueSnapValidator.checkCountryHasState(shippingContactInfo.getCountry()))
                postData.put(SHIPPINGSTATE, shippingContactInfo.getState());
            postData.put(SHIPPINGCITY, shippingContactInfo.getCity());
            postData.put(SHIPPINGADDRESS, shippingContactInfo.getAddress());
            postData.put(SHIPPINGZIP, shippingContactInfo.getZip());
        }

        if (null != kountSessionId && !"".equals(kountSessionId)) {
            postData.put(FRAUDSESSIONID, kountSessionId);
        }

        return postData;
    }


    /**
     * @param purchaseDetails {@link PurchaseDetails}
     * @return {@link JSONObject} representation for api put call for the server
     * @throws JSONException in case of invalid JSON object (should not happen)
     */
    public static JSONObject createDataObject(@NonNull ShopperInfoConfig shopperInfoConfig, @NonNull PurchaseDetails purchaseDetails, @Nullable String kountSessionId) throws JSONException {
        CreditCard creditCard = purchaseDetails.getCreditCard();
        BillingContactInfo billingContactInfo = purchaseDetails.getBillingContactInfo();
        ShippingContactInfo shippingContactInfo = null;
        if (shopperInfoConfig.isShippingRequired())
            shippingContactInfo = purchaseDetails.getShippingContactInfo();

        return createDataObject(shopperInfoConfig, creditCard, billingContactInfo, shippingContactInfo, kountSessionId);
    }
}
