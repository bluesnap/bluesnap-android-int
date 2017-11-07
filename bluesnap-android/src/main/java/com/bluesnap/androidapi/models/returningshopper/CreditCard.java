package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCard {
    private static final String TAG = CreditCard.class.getSimpleName();
    private static final String CARDLASTFOURDIGITS = "cardLastFourDigits";
    private static final String CARDTYPE = "cardType";
    private static final String CARDSUBTYPE = "cardSubType";
    private static final String EXPIRATIONMONTH = "expirationMonth";
    private static final String EXPIRATIONYEAR = "expirationYear";

    private String cardLastFourDigits;
    private String cardType;
    @Nullable
    private String cardSubType;
    @Nullable
    private String expirationMonth;
    @Nullable
    private String expirationYear;

    public CreditCard(JSONObject creditCardRepresentation) {
        cardLastFourDigits = (String) AndroidUtil.getObjectFromJsonObject(creditCardRepresentation, CARDLASTFOURDIGITS, TAG);
        cardType = (String) AndroidUtil.getObjectFromJsonObject(creditCardRepresentation, CARDTYPE, TAG);
        cardSubType = (String) AndroidUtil.getObjectFromJsonObject(creditCardRepresentation, CARDSUBTYPE, TAG);
        expirationMonth = (String) AndroidUtil.getObjectFromJsonObject(creditCardRepresentation, EXPIRATIONMONTH, TAG);
        expirationYear = (String) AndroidUtil.getObjectFromJsonObject(creditCardRepresentation, EXPIRATIONYEAR, TAG);
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Nullable
    public String getCardSubType() {
        return cardSubType;
    }

    public void setCardSubType(@Nullable String cardSubType) {
        this.cardSubType = cardSubType;
    }

    @Nullable
    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(@Nullable String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    @Nullable
    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(@Nullable String expirationYear) {
        this.expirationYear = expirationYear;
    }
}
