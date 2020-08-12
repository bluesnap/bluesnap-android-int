package com.bluesnap.androidapi.views.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.PaymentSources;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.KountService;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapChoosePaymentMethodActivity extends BluesnapCheckoutActivity {
    private static final String TAG = BluesnapChoosePaymentMethodActivity.class.getSimpleName();
    /**
     * activity result: operation succeeded.
     */
    public static final int BS_CHOOSE_PAYMENT_METHOD_RESULT_OK = -12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void startPayPalActivityForResult() {
        Shopper shopper = sdkConfiguration.getShopper();
        shopper.setChosenPaymentMethod(new ChosenPaymentMethod(ChosenPaymentMethod.PAYPAL));
        updateShopperOnServer(shopper);
    }

    @Override
    protected void startGooglePayActivityForResult() {
        Shopper shopper = sdkConfiguration.getShopper();
        shopper.setChosenPaymentMethod(new ChosenPaymentMethod(ChosenPaymentMethod.GOOGLE_PAY));
        updateShopperOnServer(shopper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CreditCardActivity.CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE) { // Only new credit card as supported payment method- hide New Card button
                LinearLayout newCardButton = findViewById(R.id.newCardButton);
                newCardButton.setVisibility(View.INVISIBLE);
            }

            progressBar.setVisibility(View.VISIBLE);
            Shopper shopper = sdkConfiguration.getShopper();
            CreditCardInfo newCreditCardInfo = shopper.getNewCreditCardInfo();
            if (null != newCreditCardInfo) {
                shopper.setNewPaymentSources(new PaymentSources(newCreditCardInfo));
                // AS-155: update shopper email
                String email = newCreditCardInfo.getBillingContactInfo().getEmail();
                if (email != null) {
                    shopper.setEmail(email);
                }
                shopper.setChosenPaymentMethod(new ChosenPaymentMethod(ChosenPaymentMethod.CC, newCreditCardInfo.getCreditCard()));
                updateShopperOnServer(shopper);
            } else {
                String errorMsg = "update Shopper newCreditCardInfo is null Error";
                Log.e(TAG, errorMsg);
                setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
                finish();
            }
        }

        // Only new credit card as supported payment method, and flow wasn't completed- finish activity
        else if (requestCode == CreditCardActivity.CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    /**
     * finish Bluesnap Choose Payment Method Activity After Update Shopper Success
     *
     * @param shopper - {@link Shopper}
     */
    private void finishAfterUpdateShopperSuccess(@NonNull final Shopper shopper) {
        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

        if (null != shopper.getNewCreditCardInfo() && null != shopper.getNewCreditCardInfo().getBillingContactInfo())
            sdkResult.setBillingContactInfo(shopper.getNewCreditCardInfo().getBillingContactInfo());

        if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
            sdkResult.setShippingContactInfo(shopper.getShippingContactInfo());

        sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
        //sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());

        ChosenPaymentMethod chosenPaymentMethod = shopper.getChosenPaymentMethod();
        sdkResult.setChosenPaymentMethodType(chosenPaymentMethod.getChosenPaymentMethodType());
        if (ChosenPaymentMethod.CC.equals(chosenPaymentMethod.getChosenPaymentMethodType()) && chosenPaymentMethod.getCreditCard() != null) {
            sdkResult.setLast4Digits(chosenPaymentMethod.getCreditCard().getCardLastFourDigits());
            sdkResult.setCardType(chosenPaymentMethod.getCreditCard().getCardType());
        }


        Intent resultIntent = new Intent();
        resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
        setResult(Activity.RESULT_OK, resultIntent);
        //Only set the remember shopper here since failure can lead to missing tokenization on the server
        shopper.getNewCreditCardInfo().getCreditCard().setTokenizationSuccess();
        Log.d(TAG, "tokenization finished");
        finish();
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
                blueSnapService.submitUpdatedShopperDetails(shopper, new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        finishAfterUpdateShopperSuccess(shopper);
                    }

                    @Override
                    public void onFailure() {
                        String errorMsg = "update Shopper on Server Service Error";
                        Log.e(TAG, errorMsg);
                        setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
                        finish();

                    }
                });
            }
        });
    }
}