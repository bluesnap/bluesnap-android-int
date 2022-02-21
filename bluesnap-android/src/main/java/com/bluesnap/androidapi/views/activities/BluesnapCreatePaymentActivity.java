package com.bluesnap.androidapi.views.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.WindowManager;
import androidx.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.KountService;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapCreatePaymentActivity extends BluesnapCheckoutActivity {
    private static final String TAG = BluesnapCreatePaymentActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


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

        if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.CC) && null != chosenPaymentMethod.getCreditCard()) {
            try {
                Intent resultIntent = new Intent();
                sdkRequest = BlueSnapService.getInstance().getSdkRequest();
                if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                    resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS, shopperConfiguration.getShippingContactInfo());
                resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS, shopperConfiguration.getBillingContactInfo());
                SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
                Log.d(TAG, "tokenization of previous used credit card");

                sdkResult.setBillingContactInfo(shopperConfiguration.getBillingContactInfo());
                if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                    sdkResult.setShippingContactInfo(shopperConfiguration.getShippingContactInfo());
                sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
                sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
                // update last4 from server result
                sdkResult.setLast4Digits(chosenPaymentMethod.getCreditCard().getCardLastFourDigits());
                // update card type from server result
                sdkResult.setCardType(chosenPaymentMethod.getCreditCard().getCardType());
                Log.d(TAG, sdkResult.toString());
                resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
                setResult(Activity.RESULT_OK, resultIntent);
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

        } else if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.PAYPAL)) {
            startPayPalActivityForResult();
        } else if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.GOOGLE_PAY)) {
            Log.d(TAG, "Waiting for Google-Pay to check availavblility");
        } else {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
        }
    }

    @Override
    protected void setGooglePayAvailable(boolean available) {
        final ShopperConfiguration shopperConfiguration = blueSnapService.getShopperConfiguration();
        final ChosenPaymentMethod chosenPaymentMethod = shopperConfiguration.getChosenPaymentMethod();
        if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.GOOGLE_PAY)) {
            if (available) {
                startGooglePayActivityForResult();
            } else {
                Log.e(TAG, "ChosenPaymentMethod=GOOGLE_PAY but is not available");
                setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The chosen payment method is not availbale."));
                finish();
            }
        }
    }
}