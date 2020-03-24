package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCardInfo extends BSModel {

    public static final String BILLING_CONTACT_INFO = "billingContactInfo";
    public static final String CREDIT_CARD = "creditCard";
    //@SerializedName("billingContactInfo")
    private BillingContactInfo billingContactInfo;
    //@SerializedName("creditCard")
    private CreditCard creditCard;
    public ProcessingInfo processingInfo;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public CreditCardInfo() {
        creditCard = new CreditCard();
        billingContactInfo = new BillingContactInfo();
    }

    @Nullable
    public static CreditCardInfo fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        CreditCardInfo creditCardInfo = new CreditCardInfo();
        creditCardInfo.setBillingContactInfo(BillingContactInfo.fromJson(JsonParser.getOptionalObject(jsonObject, BILLING_CONTACT_INFO)));
        creditCardInfo.setCreditCard(CreditCard.fromJson(JsonParser.getOptionalObject(jsonObject, CREDIT_CARD)));
        //TODO: processingInfo can also be parsed here..

        return creditCardInfo;

    }


    public BillingContactInfo getBillingContactInfo() {
        return billingContactInfo;
    }

    public void setBillingContactInfo(BillingContactInfo billingContactInfo) {
        this.billingContactInfo = billingContactInfo;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * @param creditCard
     * @param billingContactInfo
     * @param processingInfo
     */
    public CreditCardInfo(BillingContactInfo billingContactInfo, CreditCard creditCard, ProcessingInfo processingInfo) {
        super();
        this.billingContactInfo = billingContactInfo;
        this.creditCard = creditCard;
        this.processingInfo = processingInfo;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    @NonNull
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, CREDIT_CARD, getCreditCard());
        putJSONifNotNull(jsonObject, BILLING_CONTACT_INFO, getBillingContactInfo());
        return jsonObject;
    }
}
