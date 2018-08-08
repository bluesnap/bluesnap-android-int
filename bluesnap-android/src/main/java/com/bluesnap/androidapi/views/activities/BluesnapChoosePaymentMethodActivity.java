package com.bluesnap.androidapi.views.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapChoosePaymentMethodActivity extends BluesnapCheckoutActivity {
    private static final String TAG = BluesnapChoosePaymentMethodActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void startCreditCardActivityForResult(String intentExtraName, String intentExtravalue) {
        super.startCreditCardActivityForResult(intentExtraName, intentExtravalue);
    }

    @Override
    protected void startPayPalActivityForResult() {
        //TODO: choose paypal and done
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "got result " + resultCode);
        Log.d(TAG, "got request " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE) {
                setResult(Activity.RESULT_OK, data);
                finish();
            } else if (requestCode == WebViewActivity.PAYPAL_REQUEST_CODE) {
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }
}