package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.views.adapters.OneLineCCViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapCheckoutActivity extends AppCompatActivity {
    private static final String TAG = BluesnapCheckoutActivity.class.getSimpleName();
    public static final String SDK_ERROR_MSG = "SDK_ERROR_MESSAGE";
    public static final String EXTRA_PAYMENT_RESULT = "com.bluesnap.intent.BSNAP_PAYMENT_RESULT";
    public static final String EXTRA_SHIPPING_DETAILS = "com.bluesnap.intent.BSNAP_SHIPPING_DETAILS";
    public static final String EXTRA_BILLING_DETAILS = "com.bluesnap.intent.BSNAP_BILLING_DETAILS";
    public static final int REQUEST_CODE_DEFAULT = 1;
    public static final int RESULT_SDK_FAILED = -2;
    public static String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    public static String NEW_CC = "NEW_CC";
    public static String RETURNING_CC = "RETURNING_CC";
    protected ProgressBar progressBar;
    protected SdkRequest sdkRequest;
    protected SDKConfiguration sdkConfiguration;
    protected OneLineCCViewAdapter oneLineCCViewAdapter;
    protected final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && BlueSnapService.getInstance().getSdkRequest() == null) {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
            return;
        }

        setContentView(R.layout.choose_payment_method);
        sdkRequest = blueSnapService.getSdkRequest();
        sdkConfiguration = blueSnapService.getsDKConfiguration();

        // validate the SDK request, finish the activity with error result in case of failure.
        if (!verifySDKRequest()) {
            Log.d(TAG, "Closing Activity");
            return;
        }

        loadShopperFromSDKConfiguration();
        LinearLayout newCardButton = findViewById(R.id.newCardButton);
        newCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreditCardActivityForResult(FRAGMENT_TYPE, NEW_CC);
            }
        });

        LinearLayout payPalButton = findViewById(R.id.payPalButton);
        progressBar = findViewById(R.id.progressBar);
        if (!sdkConfiguration.getSupportedPaymentMethods().isPaymentMethodActive(SupportedPaymentMethods.PAYPAL)) {
            payPalButton.setVisibility(View.GONE);
        } else {
            payPalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPayPalActivityForResult();
                }
            });
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        verifySDKRequest();
    }

    /**
     * start Credit Card Activity For Result
     *
     * @param intentExtraName  - The name of the extra data, with package prefix.
     * @param intentExtravalue -  The String data value.
     */
    protected void startCreditCardActivityForResult(String intentExtraName, String intentExtravalue) {
        Intent intent = new Intent(getApplicationContext(), CreditCardActivity.class);
        intent.putExtra(intentExtraName, intentExtravalue);
        startActivityForResult(intent, CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE);
    }

    /**
     * start PayPal Activity For Result
     */
    protected void startPayPalActivityForResult() {
        String payPalToken = BlueSnapService.getPayPalToken();
        if ("".equals(payPalToken)) {
            Log.d(TAG, "create payPalToken");
            startPayPal();
        } else {
            Log.d(TAG, "startWebViewActivity");
            startWebViewActivity(payPalToken);
        }
    }

    /**
     * gets Shopper from SDK configuration and populate returning shopper cards (from populateFromCard function) or create a new shopper if shopper object does not exists
     */
    protected void loadShopperFromSDKConfiguration() {
        final Shopper shopper = sdkConfiguration.getShopper();
        updateShopperCCViews(shopper);
    }

    protected void updateShopperCCViews(@NonNull final Shopper shopper) {
        if (null == shopper.getPreviousPaymentSources() || null == shopper.getPreviousPaymentSources().getPreviousCreditCardInfos()) {
            Log.d(TAG, "Existing shopper contains no previous paymentSources or Previous card info");
            return;
        }
        //create an ArrayList<CreditCardInfo> for the ListView.
        ArrayList<CreditCardInfo> returningShopperCreditCardInfoArray = shopper.getPreviousPaymentSources().getPreviousCreditCardInfos();

        //create an adapter to describe how the items are displayed.
        ListView oneLineCCViewComponentsListView = findViewById(R.id.oneLineCCViewComponentsListView);
        oneLineCCViewAdapter = new OneLineCCViewAdapter(this, returningShopperCreditCardInfoArray);
        //set the spinners adapter to the previously created one.
        oneLineCCViewComponentsListView.setAdapter(oneLineCCViewAdapter);

        oneLineCCViewComponentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shopper.setNewCreditCardInfo((CreditCardInfo) oneLineCCViewAdapter.getItem(position));
                BillingInfo billingInfo = shopper.getNewCreditCardInfo().getBillingContactInfo();
                if (!sdkRequest.isEmailRequired())
                    billingInfo.setEmail(null);
                else
                    billingInfo.setEmail(shopper.getEmail());
                if (!sdkRequest.isBillingRequired()) {
                    billingInfo.setAddress(null);
                    billingInfo.setCity(null);
                    billingInfo.setState(null);
                }
                startCreditCardActivityForResult(FRAGMENT_TYPE, RETURNING_CC);
            }
        });
        oneLineCCViewComponentsListView.setVisibility(View.VISIBLE);
    }

    /**
     * verify SDK Request
     *
     * @return boolean
     */
    protected boolean verifySDKRequest() {
        if (sdkRequest == null) {
            Log.e(TAG, "sdkrequest is null");

            setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, "Activity has been aborted."));
            finish();
            return false;
        } else try {
            return sdkRequest.verify();
        } catch (BSPaymentRequestException e) {
            String errorMsg = "payment request not validated:" + e.getMessage();
            e.printStackTrace();
            Log.d(TAG, errorMsg);
            setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
            finish();
            return false;
        }
    }

    /**
     * start PayPal
     * createsPayPal Token and redirects to Web View Activity
     */
    protected void startPayPal() {
        startPayPal(sdkRequest.getPriceDetails());
    }

    /**
     * start PayPal
     * createsPayPal Token and redirects to Web View Activity
     *
     * @param priceDetails {@link PriceDetails}
     */
    protected void startPayPal(final PriceDetails priceDetails) {
        progressBar.setVisibility(View.VISIBLE);
        BlueSnapService.getInstance().createPayPalToken(priceDetails.getAmount(), priceDetails.getCurrencyCode(), new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                try {
                    startWebViewActivity(BlueSnapService.getPayPalToken());
                } catch (Exception e) {
                    Log.w(TAG, "Unable to start webview activity", e);
                }
            }


            @Override
            public void onFailure() {
                try {
                    JSONObject errorDescription = BlueSnapService.getErrorDescription();
                    Log.e(TAG, errorDescription.toString());
                    String message = null;
                    String title = null;
                    if (errorDescription.getString("code").equals("20027")) {
                        // not supported PayPal currency
                        String errorCurrency = priceDetails.getCurrencyCode();
                        // all supported PayPal currencies
                        ArrayList<String> payPalCurrencies = blueSnapService.getsDKConfiguration().getSupportedPaymentMethods().getPaypalCurrencies();
                        // check if array is either bigger than one or if equals one than that it is not the already tried currency
                        if (null != payPalCurrencies && (payPalCurrencies.size() > 1 || !payPalCurrencies.contains(errorCurrency))) {
                            // get merchant store currency
                            String currency = blueSnapService.getsDKConfiguration().getRates().getMerchantStoreCurrency();
                            /* If a store currency is supported in PP - fallback to that currency
                            Otherwise, if USD is supported in PP - fallback to USD
                            Otherwise, fallback to any supported PP currency */
                            currency = payPalCurrencies.contains(currency)
                                    ? currency
                                    : (
                                    payPalCurrencies.contains(SupportedPaymentMethods.USD) && !SupportedPaymentMethods.USD.equals(errorCurrency)
                                            ? SupportedPaymentMethods.USD
                                            : payPalCurrencies.get(0)
                            );

                            Log.d(TAG, "Given currency not supported with PayPal - changing to PayPal supported Currency: " + currency);
                            PriceDetails localPriceDetails = blueSnapService.getConvertedPriceDetails(sdkRequest.getPriceDetails(), currency);
                            startPayPal(localPriceDetails);
                        } else {
                            message = getString(R.string.CURRENCY_NOT_SUPPORTED_PART_1)
                                    + " "
                                    + sdkRequest.getPriceDetails().getCurrencyCode()
                                    + " "
                                    + getString(R.string.CURRENCY_NOT_SUPPORTED_PART_2)
                                    + " "
                                    + getString(R.string.SUPPORT_PLEASE)
                                    + " "
                                    + getString(R.string.CURRENCY_NOT_SUPPORTED_PART_3)
                                    + " "
                                    + getString(R.string.SUPPORT_OR)
                                    + " "
                                    + getString(R.string.SUPPORT);

                            title = getString(R.string.CURRENCY_NOT_SUPPORTED_PART_TITLE);
                        }
                    } else {
                        message = getString(R.string.SUPPORT_PLEASE)
                                + " "
                                + getString(R.string.SUPPORT);

                        title = getString(R.string.ERROR);
                    }
                    if (null != message)
                        BluesnapAlertDialog.setDialog(BluesnapCheckoutActivity.this
                                , message, title);
                } catch (Exception e) {
                    Log.e(TAG, "json parsing exception", e);
                    BluesnapAlertDialog.setDialog(BluesnapCheckoutActivity.this, "Paypal service error", "Error"); //TODO: friendly error
                } finally {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    /**
     * start WebView Activity for PayPal Checkout
     *
     * @param payPalUrl - received from createPayPalToken {@link com.bluesnap.androidapi.services.BlueSnapAPI}
     */
    protected void startWebViewActivity(String payPalUrl) {
        Intent newIntent;
        newIntent = new Intent(getApplicationContext(), WebViewActivity.class);
        // Todo change paypal header name to merchant name from payment request
        newIntent.putExtra(getString(R.string.WEBVIEW_STRING), "PayPal");
        newIntent.putExtra(getString(R.string.WEBVIEW_URL), payPalUrl);
        newIntent.putExtra(getString(R.string.SET_JAVA_SCRIPT_ENABLED), true);
        startActivityForResult(newIntent, WebViewActivity.PAYPAL_REQUEST_CODE);
        progressBar.setVisibility(View.INVISIBLE);
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
