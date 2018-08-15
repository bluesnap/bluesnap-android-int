package com.bluesnap.androidapi.views.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.KountService;
import com.bluesnap.androidapi.services.TokenServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapChoosePaymentMethodActivity extends BluesnapCheckoutActivity {
    private static final String TAG = BluesnapChoosePaymentMethodActivity.class.getSimpleName();
    public static final String NEW_SHOPPER = "NEW_SHOPPER";
    public static final String RETURNING_SHOPPER = "RETURNING_SHOPPER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        NEW_CC = NEW_SHOPPER;
        RETURNING_CC = RETURNING_SHOPPER;
        super.onCreate(savedInstanceState);
        //TODO: remove after server paypal issue is fixed
        findViewById(R.id.payPalButton).setVisibility(View.GONE);
    }

    @Override
    protected void startCreditCardActivityForResult(String intentExtraName, String intentExtravalue) {
        Intent intent = new Intent(getApplicationContext(), CreditCardActivity.class);
        intent.putExtra(intentExtraName, intentExtravalue);
        startActivityForResult(intent, CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void startPayPalActivityForResult() {
        Shopper shopper = sdkConfiguration.getShopper();
        shopper.setChosenPaymentMethod(new ChosenPaymentMethod(ChosenPaymentMethod.PAYPAL));
        updateShopperOnServer(shopper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "got result " + resultCode);
        Log.d(TAG, "got request " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            Shopper shopper = sdkConfiguration.getShopper();
            shopper.setChosenPaymentMethod(new ChosenPaymentMethod(ChosenPaymentMethod.CC, shopper.getNewCreditCardInfo().getCreditCard()));
            updateShopperOnServer(shopper);
        }
    }

    /**
     * finish Bluesnap Choose Payment Method Activity After Update Shopper Success
     *
     * @param shopper - {@link Shopper}
     */
    private void finishBlueSnapChoosePaymentMethodActivityAfterUpdateShopperSuccess(final Shopper shopper) {
        try {
            SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

            if (null != shopper.getNewCreditCardInfo().getBillingContactInfo())
                sdkResult.setBillingContactInfo(shopper.getNewCreditCardInfo().getBillingContactInfo());

            if (sdkRequest.isShippingRequired())
                sdkResult.setShippingContactInfo(shopper.getShippingContactInfo());

            sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
            sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());

            ChosenPaymentMethod chosenPaymentMethod = shopper.getChosenPaymentMethod();
            if (chosenPaymentMethod != null) {
                sdkResult.setChosenPaymentMethodType(chosenPaymentMethod.getChosenPaymentMethodType());
                if (ChosenPaymentMethod.CC.equals(chosenPaymentMethod.getChosenPaymentMethodType()) && chosenPaymentMethod.getCreditCard() != null) {
                    sdkResult.setLast4Digits(chosenPaymentMethod.getCreditCard().getCardLastFourDigits());
                    sdkResult.setCardType(chosenPaymentMethod.getCreditCard().getCardType());
                }
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
            setResult(RESULT_OK, resultIntent);
            //Only set the remember shopper here since failure can lead to missing tokenization on the server
            shopper.getNewCreditCardInfo().getCreditCard().setTokenizationSuccess();
            Log.d(TAG, "tokenization finished");
            finish();
        } catch (NullPointerException e) {
            Log.e(TAG, "", e);
            String errorMsg = String.format("Service Error %s", e.getMessage());
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));   //TODO Display error to the user
            finish();
        }
    }

    /**
     * update Shopper On Server
     *
     * @param shopper - {@link Shopper}
     */
    private void updateShopperOnServer(final Shopper shopper) {
        blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapService.submitUpdatedShopperDetails(shopper);
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    finishBlueSnapChoosePaymentMethodActivityAfterUpdateShopperSuccess(shopper);
                } else if (response.getResponseCode() == 400 && null != blueSnapService.getTokenProvider() && !"".equals(response.getResponseString())) {
                    try {
                        JSONObject errorResponse = new JSONObject(response.getResponseString());
                        JSONArray rs2 = (JSONArray) errorResponse.get("message");
                        JSONObject rs3 = (JSONObject) rs2.get(0);
                        if ("EXPIRED_TOKEN".equals(rs3.get("errorName"))) {
                            blueSnapService.getTokenProvider().getNewToken(new TokenServiceCallback() {
                                @Override
                                public void complete(String newToken) {
                                    blueSnapService.setNewToken(newToken);
                                    updateShopperOnServer(shopper);
                                }
                            });
                        } else {
                            String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                            Log.e(TAG, errorMsg);
                            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                    }
                } else {
                    String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                    Log.e(TAG, errorMsg);
                    setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
                    finish();
                }
            }
        });
    }
}