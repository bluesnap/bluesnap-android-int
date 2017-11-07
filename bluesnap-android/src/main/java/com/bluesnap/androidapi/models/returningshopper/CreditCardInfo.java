package com.bluesnap.androidapi.models.returningshopper;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCardInfo {
    private static final String TAG = CreditCardInfo.class.getSimpleName();
    private static final String BILLINGCONTACTINFO = "billingContactInfo";
    private static final String CREDITCARD = "creditCard";

    private ContactInfo billingContactInfo;
    private CreditCard creditCard;

    public CreditCardInfo(JSONObject creditCardInfoRepresentation) {
        billingContactInfo = new ContactInfo((JSONObject) AndroidUtil.getObjectFromJsonObject(creditCardInfoRepresentation, BILLINGCONTACTINFO, TAG));
        creditCard = new CreditCard((JSONObject) AndroidUtil.getObjectFromJsonObject(creditCardInfoRepresentation, CREDITCARD, TAG));
    }

    public ContactInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    public void setBillingContactInfo(ContactInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
