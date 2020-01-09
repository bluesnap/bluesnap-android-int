package com.bluesnap.androidapi;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by oz on 10/30/17.
 */

public class CardTokenizationTests extends BSAndroidTestsBase {

    @Test
    public void tokenize_card_test() throws BSPaymentRequestException {
        final String CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE = "1234123412341238";
        final String CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED = "5568111111111116";

        final PurchaseDetails purchaseDetails = new PurchaseDetails();
        final BillingContactInfo billingContactInfo = new BillingContactInfo();
        purchaseDetails.setBillingContactInfo(billingContactInfo);
        billingContactInfo.setFullName("John Doe");
        purchaseDetails.setStoreCard(true);
        final CreditCard card = new CreditCard();
        purchaseDetails.setCreditCard(card);
        String number = CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED;
        card.update(number, "11/25", "123");

        //assertTrue("this should be a valid luhn", BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardFullValidation(card));
        assertNotNull(card.getCardType());
        assertFalse(card.getCardType().isEmpty());

        try {
            BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
            assertEquals(HTTP_OK, blueSnapHTTPResponse.getResponseCode());
            JSONObject jsonObject = new JSONObject(blueSnapHTTPResponse.getResponseString());
            String Last4 = jsonObject.getString("last4Digits");
            String ccType = jsonObject.getString("ccType");
            assertEquals("MASTERCARD", ccType);
            assertEquals("1116", Last4);

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
            fail("Exceptions while parsing response");
        }
    }
}



