package com.bluesnap.androidapi;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by oz on 10/30/17.
 */

public class EndToEndIntegrationTests extends BSAndroidIntegrationTestsBase {
    private static final String TAG = EndToEndIntegrationTests.class.getSimpleName();
    final String CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED = "5568111111111116";
    final IntegrationTestsHelper IntegrationTestsHelper = new IntegrationTestsHelper(TAG);


    @Test
    public void test_end_to_end_credit_card_checkout_flow() throws InterruptedException {
//        final String CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE = "1234123412341238";
        final Double amount = 55.0;
        final String currencyNameCode = "USD";

        IntegrationTestsHelper.endToEndCreditCardCheckoutFlow(amount, currencyNameCode, CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED);

    }
}



