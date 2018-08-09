package com.bluesnap.androidapi.services;

import android.support.annotation.NonNull;

import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.Shopper;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueSnapJSON {

    private static final String CHOSENPAYMENTMETHOD = "chosenPaymentMethod";
    private static final String CHOSENPAYMENTMETHODTYPE = "chosenPaymentMethodType";



    private static final String CREDITCARDINFO = "creditCardInfo";
    private static final String BILLINGCONTACTINFO = "billingContactInfo";
    private static final String CREDITCARD = "creditCard";

    private static final String TRANSACTIONFRAUDINFO = "transactionFraudInfo";
    private static final String FRAUDSESSIONID = "fraudSessionId";


    /*public static JSONObject convertShopperObject2JSON() throws JSONException {
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
        String paymentTypes = null;
        if (chosenPaymentMethod != null)
            paymentTypes = chosenPaymentMethod.getChosenPaymentMethodType();

        chosenPaymentMethodJSON.put(CHOSENPAYMENTMETHODTYPE, paymentTypes);


        if (ChosenPaymentMethod.CC.equals(paymentTypes)) {
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

*/
}
