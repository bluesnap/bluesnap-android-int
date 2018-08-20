package com.bluesnap.androidapi.views.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.KountService;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.CARDTYPE;
import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.LAST4DIGITS;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapCreatePaymentActivity extends BluesnapCheckoutActivity {
    private static final String TAG = BluesnapCreatePaymentActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && BlueSnapService.getInstance().getSdkRequest() == null) {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
            return;
        }

        setContentView(R.layout.create_payment_for_configured_shopper);
        //progressBar = findViewById(R.id.progressBarLoadingData);

        sdkRequest = blueSnapService.getSdkRequest();
        sdkConfiguration = blueSnapService.getsDKConfiguration();
        final ShopperConfiguration shopperConfiguration = blueSnapService.getShopperConfiguration();
        final ChosenPaymentMethod chosenPaymentMethod = shopperConfiguration.getChosenPaymentMethod();

        // validate the SDK request, finish the activity with error result in case of failure.
        if (!verifySDKRequest() || null == chosenPaymentMethod) {
            Log.d(TAG, "Closing Activity");
            return;
        }

        //TODO: change the CC behavior according to not needing tokenization
        if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.CC) && null != chosenPaymentMethod.getCreditCard()) {
            Intent resultIntent = new Intent();
            sdkRequest = BlueSnapService.getInstance().getSdkRequest();
            if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS, shopperConfiguration.getShippingContactInfo());
            resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS, shopperConfiguration.getBillingContactInfo());
            tokenizeChosenPaymentCC(shopperConfiguration, chosenPaymentMethod, resultIntent);

        } else if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.PAYPAL)) {
            startPayPalActivityForResult();
        } else {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
        }
    }

    private void tokenizeChosenPaymentCC(final ShopperConfiguration shopperConfiguration, final ChosenPaymentMethod chosenPaymentMethod, final Intent resultIntent) {
        blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapService.submitCreditCardDetailsForShopperConfiguration(chosenPaymentMethod.getCreditCard());
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        String Last4;
                        String ccType;
                        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

                        Last4 = chosenPaymentMethod.getCreditCard().getCardLastFourDigits();
                        ccType = chosenPaymentMethod.getCreditCard().getCardType();
                        Log.d(TAG, "tokenization of previous used credit card");

                        sdkResult.setBillingContactInfo(shopperConfiguration.getBillingContactInfo());
                        if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                            sdkResult.setShippingContactInfo(shopperConfiguration.getShippingContactInfo());
                        sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
                        sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
                        // update last4 from server result
                        sdkResult.setLast4Digits(Last4);
                        // update card type from server result
                        sdkResult.setCardType(ccType);
                        Log.d(TAG, sdkResult.toString());
                        resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
                        setResult(BS_CHECKOUT_RESULT_OK, resultIntent);
                        //Only set the remember shopper here since failure can lead to missing tokenization on the server
                        chosenPaymentMethod.getCreditCard().setTokenizationSuccess();
                        Log.d(TAG, "tokenization finished");
                        finish();
                    } catch (NullPointerException e) {
                        Log.e(TAG, "", e);
                        String errorMsg = String.format("Service Error %s", e.getMessage());
                        setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));   //TODO Display error to the user
                        finish();
                    }
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
                                    tokenizeChosenPaymentCC(shopperConfiguration, chosenPaymentMethod, resultIntent);
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