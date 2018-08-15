package com.bluesnap.androidapi.views.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

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
        ShopperConfiguration shopperConfiguration = blueSnapService.getShopperConfiguration();
        ChosenPaymentMethod chosenPaymentMethod = shopperConfiguration.getChosenPaymentMethod();

        // validate the SDK request, finish the activity with error result in case of failure.
        if (!verifySDKRequest() || null == chosenPaymentMethod) {
            Log.d(TAG, "Closing Activity");
            return;
        }

        if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.CC) && null != chosenPaymentMethod.getCreditCard()) {
            blueSnapService.submitCreditCardDetailsForShopperConfiguration(chosenPaymentMethod.getCreditCard());
        } else if (chosenPaymentMethod.getChosenPaymentMethodType().equals(SupportedPaymentMethods.PAYPAL)) {
            startPayPalActivityForResult();
        } else {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
        }
    }

}