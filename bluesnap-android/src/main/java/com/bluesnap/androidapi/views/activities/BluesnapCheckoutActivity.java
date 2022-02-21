package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkRequestSubscriptionCharge;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.GooglePayService;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.adapters.OneLineCCViewAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy.biber on 21/02/2018.
 */

public class BluesnapCheckoutActivity extends AppCompatActivity {
    private static final String TAG = BluesnapCheckoutActivity.class.getSimpleName();
    public static final String SDK_ERROR_MSG = "SDK_ERROR_MESSAGE";
    public static final String EXTRA_PAYMENT_RESULT = "com.bluesnap.intent.BSNAP_PAYMENT_RESULT";
    public static final String EXTRA_SHIPPING_DETAILS = "com.bluesnap.intent.BSNAP_SHIPPING_DETAILS";
    public static final String EXTRA_BILLING_DETAILS = "com.bluesnap.intent.BSNAP_BILLING_DETAILS";
    public static final String EXTRA_SUBSCRIPTION_RESULT = "com.bluesnap.intent.BSNAP_SUBSCRIPTION_RESULT";
    public static final int REQUEST_CODE_DEFAULT = 1;
    public static final int REQUEST_CODE_SUBSCRIPTION = 2;
    public static final int RESULT_SDK_FAILED = -2;
    private static final int GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE = 991;

    /**
     * activity result: operation succeeded.
     */
    public static final int BS_CHECKOUT_RESULT_OK = -11;
    public static String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    public static String NEW_CC = "NEW_CC";
    public static String RETURNING_CC = "RETURNING_CC";
    protected ProgressBar progressBar;
    protected SdkRequestBase sdkRequest;
    protected SDKConfiguration sdkConfiguration;
    protected OneLineCCViewAdapter oneLineCCViewAdapter;
    protected final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    protected PaymentsClient googlePayClient;

    private boolean showPayPal;
    private boolean showGooglePay;

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
                startCreditCardActivityForResult(FRAGMENT_TYPE, NEW_CC, CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE);
            }
        });

        LinearLayout payPalButton = findViewById(R.id.payPalButton);
        progressBar = findViewById(R.id.progressBar);
        SupportedPaymentMethods supportedPaymentMethods = sdkConfiguration.getSupportedPaymentMethods();

        // update payment methods according to merchant configurations
        supportedPaymentMethods.setPaymentMethods(sdkRequest.getPaymentMethodsConfiguration());

        showPayPal = supportedPaymentMethods.isPaymentMethodActive(SupportedPaymentMethods.PAYPAL) && !(sdkRequest instanceof SdkRequestSubscriptionCharge);
        showGooglePay = supportedPaymentMethods.isPaymentMethodActive(SupportedPaymentMethods.GOOGLE_PAY_TOKENIZED_CARD)
                || supportedPaymentMethods.isPaymentMethodActive(SupportedPaymentMethods.GOOGLE_PAY);

        if (!showPayPal) {
            payPalButton.setVisibility(View.GONE);
        } else {
            payPalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPayPalActivityForResult();
                }
            });
        }

        if (showGooglePay) {
            checkIsGooglePayAvailable();
        } else {
            setGooglePayAvailable(false);
        }

        boolean shopperHasExistingCC = false;
        final Shopper shopper = sdkConfiguration.getShopper();

        if (shopper.getPreviousPaymentSources() != null && shopper.getPreviousPaymentSources().getCreditCardInfos() != null) {
            List<CreditCardInfo> returningShopperCreditCardInfoArray = shopper.getPreviousPaymentSources().getCreditCardInfos();
            shopperHasExistingCC = !returningShopperCreditCardInfoArray.isEmpty();
        }


        if (!shopperHasExistingCC && !showPayPal && !showGooglePay) {
            startCreditCardActivityForResult(FRAGMENT_TYPE, NEW_CC, CreditCardActivity.CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE);
        }
    }

    private void checkIsGooglePayAvailable() {

        GooglePayService googlePayService = GooglePayService.getInstance();

        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        googlePayClient = googlePayService.createPaymentsClient(this);
        if (googlePayClient == null) {
            setGooglePayAvailable(false);

        } else {
            // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
            // OnCompleteListener to be triggered when the result of the call is known.
            googlePayService.isReadyToPay(googlePayClient).addOnCompleteListener(
                    new OnCompleteListener<Boolean>() {
                        public void onComplete(Task<Boolean> task) {
                            try {
                                boolean result = task.getResult(ApiException.class);
                                setGooglePayAvailable(result);
                            } catch (ApiException exception) {
                                // Process error
                                Log.w(TAG, "isReadyToPay failed", exception);
                                setGooglePayAvailable(false);
                            }
                        }
                    });
        }
    }

    protected void setGooglePayAvailable(boolean available) {
        LinearLayout googlePayButton = findViewById(R.id.googlePayButton);
        if (available) {
            showGooglePay = true;
            googlePayButton.setVisibility(View.VISIBLE);
            googlePayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startGooglePayActivityForResult();
                }
            });
        } else {
            showGooglePay = false;
            googlePayButton.setVisibility(View.GONE);
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
    protected void startCreditCardActivityForResult(String intentExtraName, String intentExtravalue, int requestCode) {
        Intent intent = new Intent(getApplicationContext(), CreditCardActivity.class);
        intent.putExtra(intentExtraName, intentExtravalue);
        startActivityForResult(intent, requestCode);
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
     * start GooglePay Activity For Result
     */
    protected void startGooglePayActivityForResult() {

        Log.d(TAG, "start GooglePay flow");

        // Disables the button to prevent multiple clicks.
        LinearLayout googlePayButton = findViewById(R.id.googlePayButton);
        if (googlePayButton != null) {
            googlePayButton.setClickable(false);
        }

        Task<PaymentData> futurePaymentData = GooglePayService.getInstance().createPaymentDataRequest(googlePayClient);

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(futurePaymentData, this, GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE);
    }

    /**
     * gets Shopper from SDK configuration and populate returning shopper cards (from populateFromCard function) or create a new shopper if shopper object does not exists
     */
    protected void loadShopperFromSDKConfiguration() {
        final Shopper shopper = sdkConfiguration.getShopper();
        updateShopperCCViews(shopper);
    }

    protected void updateShopperCCViews(@NonNull final Shopper shopper) {
        if (null == shopper.getPreviousPaymentSources() || null == shopper.getPreviousPaymentSources().getCreditCardInfos()) {
            Log.d(TAG, "Existing shopper contains no previous paymentSources or Previous card info");
            return;
        }
        //create an ArrayList<CreditCardInfo> for the ListView.
        List<CreditCardInfo> returningShopperCreditCardInfoArray = shopper.getPreviousPaymentSources().getCreditCardInfos();

        //create an adapter to describe how the items are displayed.
        ListView oneLineCCViewComponentsListView = findViewById(R.id.oneLineCCViewComponentsListView);
        oneLineCCViewAdapter = new OneLineCCViewAdapter(this, returningShopperCreditCardInfoArray);
        //set the spinners adapter to the previously created one.
        oneLineCCViewComponentsListView.setAdapter(oneLineCCViewAdapter);

        oneLineCCViewComponentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shopper.setNewCreditCardInfo((CreditCardInfo) oneLineCCViewAdapter.getItem(position));
                BillingContactInfo billingContactInfo = shopper.getNewCreditCardInfo().getBillingContactInfo();
                if (!sdkRequest.getShopperCheckoutRequirements().isEmailRequired())
                    billingContactInfo.setEmail(null);
                else
                    billingContactInfo.setEmail(shopper.getEmail());
                if (!sdkRequest.getShopperCheckoutRequirements().isBillingRequired()) {
                    billingContactInfo.setAddress(null);
                    billingContactInfo.setCity(null);
                    billingContactInfo.setState(null);
                }
                startCreditCardActivityForResult(FRAGMENT_TYPE, RETURNING_CC, CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

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
                        /* { "message": [ { "errorName": "PAYPAL_UNSUPPORTED_CURRENCY", "code": "20027", "description": "The given currency 'ILS' is not supported with PayPal." } ] } */
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
                    } else if (errorDescription.getString("code").equals("14050")) {
                        /*  { "message": [ { "errorName": "PAYPAL_TOKEN_ALREADY_USED", "code": "14050", "description": "PayPal Token already used" } ] } */
                        blueSnapService.getTokenProvider().getNewToken(new TokenServiceCallback() {
                            @Override
                            public void complete(String newToken) {
                                blueSnapService.setNewToken(newToken);
                                try {
                                    startPayPal(priceDetails);
                                } catch (Exception e) {
                                    Log.e(TAG, "json parsing exception", e);
                                    showDialogInUIThread("Paypal service error", "Error");
                                }
                            }
                        });

                    } else if (errorDescription.getString("code").equals("90015")) {
                        /* { "message": [ { "errorName": "INVALID_CURRENCY", "code": "90015", "description": "Currency: GGG is not a valid currency code according to the ISO 4217 standard." } ] } */
                        message = "INVALID CURRENCY";
                        title = getString(R.string.ERROR);

                    } else {
                        message = getString(R.string.SUPPORT_PLEASE)
                                + " "
                                + getString(R.string.SUPPORT);

                        title = getString(R.string.ERROR);
                    }
                    if (null != message) {
                        showDialogInUIThread(message, title);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "json parsing exception", e);
                    showDialogInUIThread("Paypal service error", "Error");
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    /**
     * start WebView Activity for PayPal Checkout
     *
     * @param payPalUrl - received from createPayPalToken
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
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "got result " + resultCode);
        Log.d(TAG, "got request " + requestCode);
        switch (requestCode) {
            case GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE: {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handleGooglePaySuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleGooglePayError(status.getStatusCode());
                        break;
                }

                // Re-enables the Pay with Google button.
                LinearLayout googlePayButton = findViewById(R.id.googlePayButton);
                if (googlePayButton != null) {
                    googlePayButton.setClickable(true);
                }
                break;
            }
            case CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE: {
                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(resultCode, data);
                    finish();
                }
                break;
            }
            case CreditCardActivity.CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE: {
                setResult(resultCode, data);
                finish();
                break;
            }
            case WebViewActivity.PAYPAL_REQUEST_CODE: {
                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(resultCode, data);
                    finish();
                }
                break;
            }
            default: {
            }

        }

    }

    /**
     * In case of success from the Google-Pay button, we create the token we will need
     * to send to BlueSnap to actually create the payment transaction
     *
     * @param paymentData
     */
    private void handleGooglePaySuccess(PaymentData paymentData) {

        SdkResult sdkResult = GooglePayService.getInstance().createSDKResult(paymentData);
        String encodedToken = sdkResult == null ? null : sdkResult.getGooglePayToken();
        if (encodedToken == null) {
            showDialogInUIThread("Error handling GPay, please try again or use a different payment method", "Error");
        } else {
            blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    // post the token
                    try {
                        BlueSnapHTTPResponse response = BlueSnapService.getInstance().submitTokenenizedPayment(encodedToken, SupportedPaymentMethods.GOOGLE_PAY);
                        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Log.d(TAG, "GPay token submitted successfully");

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
                            setResult(Activity.RESULT_OK, resultIntent);

                            finish();

                        } else {
                            String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                            Log.e(TAG, errorMsg);
                            showDialogInUIThread("Error handling GPay, please try again or use a different payment method", "Error");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error submitting GPay details", e);
                        showDialogInUIThread("Error handling GPay, please try again or use a different payment method", "Error");
                    }
                }
            });
        }
    }

    private void handleGooglePayError(int statusCode) {
        // At this stage, the user has already seen a popup informing them an error occurred.
        // Normally, only logging is required.
        // statusCode will hold the value of any constant from CommonStatusCode or one of the
        // WalletConstants.ERROR_CODE_* constants.
        Log.w(TAG, "loadPaymentData failed; " + String.format("Error code: %d", statusCode));
        showDialogInUIThread("GPay service error", "Error");
    }

    private void showDialogInUIThread(String message, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BluesnapAlertDialog.setDialog(BluesnapCheckoutActivity.this, message, title);
            }
        });
    }
}
