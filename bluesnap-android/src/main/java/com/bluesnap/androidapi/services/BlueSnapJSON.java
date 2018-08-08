package com.bluesnap.androidapi.services;

import android.support.annotation.NonNull;

import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PaymentTypes;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.Shopper;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueSnapJSON {
    private static final String VAULTEDSHOPPERID = "vaultedShopperId";

    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String COUNTRY = "country";
    private static final String STATE = "state";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static final String CITY = "city";
    private static final String ZIP = "zip";

    private static final String EMAIL = "email";
    private static final String PHONE = "phone";

    private static final String CHOSENPAYMENTMETHOD = "chosenPaymentMethod";
    private static final String CHOSENPAYMENTMETHODTYPE = "chosenPaymentMethodType";

    private static final String CARDLASTFOURDIGITS = "cardLastFourDigits";
    private static final String CARDTYPE = "cardType";

    private static final String SHIPPINGCONTACTINFO = "shippingContactInfo";

    private static final String PAYMENTSOURCES = "paymentSources";
    private static final String CREDITCARDINFO = "creditCardInfo";
    private static final String BILLINGCONTACTINFO = "billingContactInfo";
    private static final String CREDITCARD = "creditCard";

    private static final String EXPIRATIONYEAR = "expirationYear";
    private static final String SECURITYCODE = "securityCode";
    private static final String EXPIRATIONMONTH = "expirationMonth";
    private static final String CARDNUMBER = "cardNumber";

    private static final String TRANSACTIONFRAUDINFO = "transactionFraudInfo";
    private static final String FRAUDSESSIONID = "fraudSessionId";


    public static JSONObject convertShopperObject2JSON() throws JSONException {
        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        SdkRequest sdkRequest = blueSnapService.getSdkRequest();
        Shopper shopper = blueSnapService.getsDKConfiguration().getShopper();
        ChosenPaymentMethod chosenPaymentMethod = shopper.getChosenPaymentMethod();

        JSONObject postData = new JSONObject();

        postData.put(VAULTEDSHOPPERID, shopper.getVaultedShopperId());
        setShopperInfoJsonObject(postData, shopper, true);
        checkIfNotNullAndPutInJson(postData, EMAIL, shopper.getEmail());
        checkIfNotNullAndPutInJson(postData, PHONE, shopper.getPhone());

        if (sdkRequest.isShippingRequired()) {
            JSONObject shippingInfoJSON = new JSONObject();
            setShopperInfoJsonObject(shippingInfoJSON, shopper.getShippingContactInfo(), true);
            postData.put(SHIPPINGCONTACTINFO, shippingInfoJSON);
        }

        JSONObject chosenPaymentMethodJSON = new JSONObject();
        PaymentTypes paymentTypes = null;
        if (chosenPaymentMethod != null)
            paymentTypes = chosenPaymentMethod.getChosenPaymentMethodType();

        chosenPaymentMethodJSON.put(CHOSENPAYMENTMETHODTYPE, paymentTypes);


        if (PaymentTypes.CC.equals(paymentTypes)) {
            CreditCard creditCard = shopper.getNewCreditCardInfo().getCreditCard();
            JSONObject creditCardJSON = new JSONObject();
            creditCardJSON.put(CARDTYPE, creditCard.getCardType());
            creditCardJSON.put(CARDLASTFOURDIGITS, creditCard.getCardLastFourDigits());
            chosenPaymentMethodJSON.put(CREDITCARD, creditCardJSON);

            JSONObject billingContactInfo = new JSONObject();
            setShopperInfoJsonObject(billingContactInfo, shopper.getNewCreditCardInfo().getBillingContactInfo(), sdkRequest.isBillingRequired());
            if (sdkRequest.isEmailRequired())
                billingContactInfo.put(EMAIL, shopper.getEmail());

            if (creditCard.getIsNewCreditCard()) {
                creditCardJSON = new JSONObject();
                creditCardJSON.put(CARDNUMBER, creditCard.getNumber());
                creditCardJSON.put(SECURITYCODE, creditCard.getCvc());
                creditCardJSON.put(EXPIRATIONMONTH, creditCard.getExpirationMonth());
                creditCardJSON.put(EXPIRATIONYEAR, creditCard.getExpirationYear());
            }

            JSONObject creditCardInfo = new JSONObject();
            creditCardInfo.put(CREDITCARD, creditCardJSON);
            creditCardInfo.put(BILLINGCONTACTINFO, billingContactInfo);

            JSONObject paymentSourcesJSON = new JSONObject();
            paymentSourcesJSON.put(CREDITCARDINFO, creditCardInfo);
            postData.put(PAYMENTSOURCES, paymentSourcesJSON);
        }

        postData.put(CHOSENPAYMENTMETHOD, chosenPaymentMethodJSON);


        if (null != blueSnapService.getKountSessionId()) {
            JSONObject transactionFraudInfoJSON = new JSONObject();
            postData.put(FRAUDSESSIONID, blueSnapService.getKountSessionId());
            postData.put(TRANSACTIONFRAUDINFO, transactionFraudInfoJSON);
        }

        return postData;
    }

    /**
     * set Shopper Json Object
     *
     * @param jsonObject            - {@link JSONObject}
     * @param contactInfo           - {@link ContactInfo}
     * @param isFullDetailsRequired - boolean, set true if full details exists, aka state, address, city (for billing only, for shipping always true)
     * @throws JSONException - JSONException
     */
    private static void setShopperInfoJsonObject(@NonNull JSONObject jsonObject, @NonNull ContactInfo contactInfo, boolean isFullDetailsRequired) throws JSONException {
        checkIfNotNullAndPutInJson(jsonObject, FIRSTNAME, contactInfo.getFirstName());
        checkIfNotNullAndPutInJson(jsonObject, LASTNAME, contactInfo.getLastName());
        checkIfNotNullAndPutInJson(jsonObject, COUNTRY, contactInfo.getCountry());

        if (isFullDetailsRequired) {
            if (BlueSnapValidator.checkCountryHasState(contactInfo.getCountry()))
                checkIfNotNullAndPutInJson(jsonObject, STATE, contactInfo.getState());
            checkIfNotNullAndPutInJson(jsonObject, ADDRESS1, contactInfo.getAddress());
            checkIfNotNullAndPutInJson(jsonObject, ADDRESS2, contactInfo.getAddress2());
            checkIfNotNullAndPutInJson(jsonObject, CITY, contactInfo.getCity());
        }

        checkIfNotNullAndPutInJson(jsonObject, ZIP, contactInfo.getZip());
    }

    /**
     * check If Not Null And Put In Json
     *
     * @param jsonObject - the object to put inside
     * @param value      - the object to put
     * @param key        - the key code of the value
     * @throws JSONException - JSONException
     */
    private static void checkIfNotNullAndPutInJson(JSONObject jsonObject, Object value, String key) throws JSONException {
        if (null != value && !"".equals(value))
            jsonObject.put(key, value);
    }
}
