package com.bluesnap.androidapi;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthRequest;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CardinalJWT;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static com.bluesnap.androidapi.InstrumentedCardTest.CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED;
import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oz on 10/30/17.
 */

public class CardinalAPITest extends BSAndroidTestsBase {

    static final String CARD_NUMBER_3DS_CARDIANL_CARD = "4000000000000002"; //Other card numbers fail in tests
    static final String CARDINAL_CARD_CVV = "123";
    static final String CARDINAL_CARD_EXP = "01/2022";


    @Test
    public void cardinal_token_tests() throws Exception {
        CardinalJWT cardinalJWT;
        CardinalManager cardinalManager = CardinalManager.getInstance();

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD");
        blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.configureCardinal(getTestContext());
        cardinalJWT = cardinalManager.createCardinalJWT();
        assertTrue(cardinalJWT.getJWT().length() > 10);
        cardinalManager.init(cardinalJWT);
    }


    @Test
    public void cardinal_tx_test() throws Exception {
        final PurchaseDetails purchaseDetails = new PurchaseDetails();
        final BillingContactInfo billingContactInfo = new BillingContactInfo();
        purchaseDetails.setBillingContactInfo(billingContactInfo);
        billingContactInfo.setFullName("John Doe");
        final CreditCard card = new CreditCard();
        Double amount = 30.5D;
        String currency = "USD";
        card.update(CARD_NUMBER_3DS_CARDIANL_CARD, CARDINAL_CARD_EXP, CARDINAL_CARD_CVV);
        purchaseDetails.setCreditCard(card);
        CardinalManager cardinalManager = CardinalManager.getInstance();
        SdkRequest sdkRequest = new SdkRequest(amount, currency);
        blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.createCardinalJWT();
        BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
        assertEquals(HTTP_OK, blueSnapHTTPResponse.getResponseCode());
        JSONObject jsonObject = new JSONObject(blueSnapHTTPResponse.getResponseString());
        String Last4 = jsonObject.getString("last4Digits");
        String ccType = jsonObject.getString("ccType");
        assertEquals("VISA", ccType);
        assertEquals("0002", Last4);
        BS3DSAuthResponse authResponse = cardinalManager.authWith3DS("USD", amount);
        assertEquals("CHALLENGE_REQUIRED", authResponse.getEnrollmentStatus());
        assertNotNull("No transactionID from cardinal", authResponse.getTransactionId());

    }
}



