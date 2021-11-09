package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.services.KountService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

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

    public static final String PAYMENT_TOKEN = "paymentToken";
    public static final String PAYMENT_METHOD = "paymentMethod";

    public static final String SHIPPINGFIRSTNAME = "shippingFirstName";
    public static final String SHIPPINGLASTNAME = "shippingLastName";
    public static final String SHIPPINGCOUNTRY = "shippingCountry";
    public static final String SHIPPINGSTATE = "shippingState";
    public static final String SHIPPINGCITY = "shippingCity";
    public static final String SHIPPINGADDRESS = "shippingAddress";
    public static final String SHIPPINGZIP = "shippingZip";
    public static final String PHONE = "phone";

    public static final String FRAUDSESSIONID = "fraudSessionId";
    public static final String STORECARD = "storeCard";


    /**
     * @param creditCard          {@link CreditCard}
     * @param billingContactInfo  {@link BillingContactInfo}
     * @param shippingContactInfo {@link ShippingContactInfo}
     * @return {@link JSONObject} representation for api put call for the server
     * @throws JSONException in case of invalid JSON object (should not happen)
     */
    public static JSONObject createDataObject(@NonNull CreditCard creditCard, @Nullable BillingContactInfo billingContactInfo, @Nullable ShippingContactInfo shippingContactInfo, boolean storeCard) throws JSONException {
        JSONObject postData = new JSONObject();

        if (creditCard.getIsNewCreditCard()) {
            putJSONifNotNull(postData, CCNUMBER, creditCard.getNumber());
            putJSONifNotNull(postData, CVV, creditCard.getCvc());
            putJSONifNotNull(postData, EXPDATE, creditCard.getExpirationDate());
        } else {
            putJSONifNotNull(postData, CARDTYPE, creditCard.getCardType().toUpperCase());
            putJSONifNotNull(postData, LAST4DIGITS, creditCard.getCardLastFourDigits());

        }

        if (null != billingContactInfo) {
            putJSONifNotNull(postData, BILLINGFIRSTNAME, billingContactInfo.getFirstName());
            putJSONifNotNull(postData, BILLINGLASTNAME, billingContactInfo.getLastName());
            putJSONifNotNull(postData, BILLINGCOUNTRY, billingContactInfo.getCountry());

            if (null != billingContactInfo.getZip() && !"".equals(billingContactInfo.getZip()))
                putJSONifNotNull(postData, BILLINGZIP, billingContactInfo.getZip());


            if (BlueSnapValidator.checkCountryHasState(billingContactInfo.getCountry()))
                putJSONifNotNull(postData, BILLINGSTATE, billingContactInfo.getState());
            putJSONifNotNull(postData, BILLINGCITY, billingContactInfo.getCity());
            putJSONifNotNull(postData, BILLINGADDRESS, billingContactInfo.getAddress());

            putJSONifNotNull(postData, EMAIL, billingContactInfo.getEmail());
        }
        //postData.put(PHONE, creditCardInfo.getBillingContactInfo().getPhone());

        if (null != shippingContactInfo) {
            putJSONifNotNull(postData, SHIPPINGFIRSTNAME, shippingContactInfo.getFirstName());
            putJSONifNotNull(postData, SHIPPINGLASTNAME, shippingContactInfo.getLastName());
            putJSONifNotNull(postData, SHIPPINGCOUNTRY, shippingContactInfo.getCountry());
            if (BlueSnapValidator.checkCountryHasState(shippingContactInfo.getCountry()))
                putJSONifNotNull(postData, SHIPPINGSTATE, shippingContactInfo.getState());
            putJSONifNotNull(postData, SHIPPINGCITY, shippingContactInfo.getCity());
            putJSONifNotNull(postData, SHIPPINGADDRESS, shippingContactInfo.getAddress());
            putJSONifNotNull(postData, SHIPPINGZIP, shippingContactInfo.getZip());
        }

        putJSONifNotNull(postData, FRAUDSESSIONID, KountService.getInstance().getKountSessionId());
        putJSONifNotNull(postData, STORECARD, Boolean.toString(storeCard));

        return postData;
    }


    /**
     * @param purchaseDetails {@link PurchaseDetails}
     * @return {@link JSONObject} representation for api put call for the server
     * @throws JSONException in case of invalid JSON object (should not happen)
     */
    public static JSONObject createDataObject(@NonNull PurchaseDetails purchaseDetails) throws JSONException {
        CreditCard creditCard = purchaseDetails.getCreditCard();
        BillingContactInfo billingContactInfo = purchaseDetails.getBillingContactInfo();
        ShippingContactInfo shippingContactInfo = null;
        shippingContactInfo = purchaseDetails.getShippingContactInfo();

        boolean storeCard = purchaseDetails.getStoreCard();

        return createDataObject(creditCard, billingContactInfo, shippingContactInfo, storeCard);
    }
}
