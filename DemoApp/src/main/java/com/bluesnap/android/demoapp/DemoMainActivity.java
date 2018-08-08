package com.bluesnap.android.demoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.services.*;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.bluesnap.android.demoapp.DemoToken.*;
import static java.net.HttpURLConnection.HTTP_CREATED;

public class DemoMainActivity extends AppCompatActivity {

    private static final String TAG = "DemoMainActivity";
    private static final int HTTP_MAX_RETRIES = 2;
    private static final int HTTP_RETRY_SLEEP_TIME_MILLIS = 3750;
    protected BlueSnapService bluesnapService;
    protected TokenProvider tokenProvider;
    private Spinner ratesSpinner;
    private Spinner merchantStoreCurrencySpinner;
    private EditText productPriceEditText;
    private Currency currency;
    private TextView currencySym;
    private String currencySymbol;
    private String initialPrice;
    private String displayedCurrency;
    private String currencyName;
    private String merchantToken;
    private Currency currencyByLocale;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutForProgressBar;
    private Switch shippingSwitch;
    private Switch billingSwitch;
    private Switch emailSwitch;
    private Switch allowCurrencyChangeSwitch;
    Switch returningShopperSwitch;
    EditText returningShopperEditText;
    private TextView shopperDetailsTextView;
    private HttpsURLConnection myURLConnection;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        linearLayoutForProgressBar = findViewById(R.id.mainLinearLayout);
        linearLayoutForProgressBar.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBarMerchant);
        shippingSwitch = findViewById(R.id.shippingSwitch);
        shippingSwitch.setChecked(false);
        billingSwitch = findViewById(R.id.billingSwitch);
        billingSwitch.setChecked(false);
        emailSwitch = findViewById(R.id.emailSwitch);
        emailSwitch.setChecked(false);
        allowCurrencyChangeSwitch = findViewById(R.id.allowCurrencyChangeSwitch);
        allowCurrencyChangeSwitch.setChecked(true);
        returningShopperSwitch = findViewById(R.id.returningShopperSwitch);
        returningShopperSwitch.setChecked(false);
        returningShopperEditText = findViewById(R.id.returningShopperEditText);
        shopperDetailsTextView = findViewById(R.id.shopperDetailsTextView);
        shopperDetailsTextView.setVisibility(View.INVISIBLE);
        updateReturningShopperViews(false);

        progressBar.setVisibility(View.VISIBLE);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        currencySym = findViewById(R.id.currencySym);
        ratesSpinner = findViewById(R.id.rateSpinner);
        merchantStoreCurrencySpinner = findViewById(R.id.merchantStoreCurrencySpinner);

        showDemoAppVersion();
        try {
            Locale current = getResources().getConfiguration().locale;
            currencyByLocale = Currency.getInstance(current);
        } catch (Exception e) {
            currencyByLocale = Currency.getInstance("USD");
        }


        bluesnapService = BlueSnapService.getInstance();

        generateMerchantToken();


        returningShopperEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && returningShopperEditText.getText().toString().length() > 0) {
                    generateMerchantToken();
                }
            }
        });

        returningShopperSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                updateReturningShopperViews(isChecked);
                if (isChecked && returningShopperEditText.getText().toString().length() == 0) {
                    return;
                }
                generateMerchantToken();
            }
        });
    }

    private void updateReturningShopperViews(Boolean showReturningShopper) {

        TextView shopperIdLabel = findViewById(R.id.shopperIdLabel);
        if (showReturningShopper) {
            shopperIdLabel.setVisibility(View.VISIBLE);
            returningShopperEditText.setVisibility(View.VISIBLE);
        } else {
            shopperIdLabel.setVisibility(View.INVISIBLE);
            returningShopperEditText.setVisibility(View.INVISIBLE);
        }
    }

    private void showDemoAppVersion() {
        TextView demoVersionTextView = findViewById(R.id.demoVersionTextView);
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            demoVersionTextView.setText(String.format(Locale.ENGLISH, "V:%s[%d]", versionName, versionCode));
        } catch (Exception e) {
            Log.e(TAG, "cannot extract version");
        }
    }

    private void ratesAdapterSelectionListener() {
        merchantStoreCurrencySpinner.post(new Runnable() {
            @Override
            public void run() {
                merchantStoreCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (initialPrice == null && position != 0) {
                            initialPrice = productPriceEditText.getText().toString();
                        }
                        String selectedRateName = merchantStoreCurrencySpinner.getSelectedItem().toString();
                        String convertedPrice = readCurencyFromSpinner(selectedRateName);
                        if (convertedPrice == null) return;
                        //Avoid Rotation renew
                        if (selectedRateName.equals(displayedCurrency)) {
                            return;
                        }
                        displayedCurrency = currency.getCurrencyCode();
                        if (convertedPrice.equals("0")) {
                            productPriceEditText.setHint("0");
                        } else {
                            if (currency != null) {
                                currencySymbol = currency.getSymbol();
                                currencySym.setText(currencySymbol);
                                currencyName = currency.getCurrencyCode();
                            }
                            productPriceEditText.setText(convertedPrice);
                        }
                        initControlsAfterToken();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
    }

    private String readCurencyFromSpinner(String selectedRateName) {
        currency = Currency.getInstance(selectedRateName);
        currencySymbol = currency.getSymbol();
        currencyName = currency.getCurrencyCode();
        if (initialPrice == null) {
            initialPrice = productPriceEditText.getText().toString().trim();
        }
        return bluesnapService.convertUSD(initialPrice, selectedRateName).trim();
    }

    private void showDialog(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DemoMainActivity.this);
            builder.setMessage(message);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            try {
                dialog.show();
            } catch (Exception e) {
                Log.d(TAG, "Dialog cannot be shown", e);
            }
        } catch (Exception e) {
            Log.d(TAG, "Dialog cannot be shown", e);
        }
    }

    private void updateSpinnerAdapterFromRates(final Set<String> supportedRates) {
        String[] quotesArray = new String[supportedRates.size()];
        supportedRates.toArray(quotesArray);
        ArrayAdapter<String> priceCurrencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_view, quotesArray.clone());
        ArrayAdapter<String> merchantStoreCurrencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_view, quotesArray.clone());
        ratesSpinner.setAdapter(priceCurrencyAdapter);
        merchantStoreCurrencySpinner.setAdapter(merchantStoreCurrencyAdapter);
        int currentposition = 0;
        for (String rate : quotesArray) {
            if (rate.equals(currencyByLocale.getCurrencyCode())) {
                break;
            }
            currentposition++;
        }
        ratesSpinner.setSelection(currentposition);
        merchantStoreCurrencySpinner.setSelection(currentposition);
        ratesAdapterSelectionListener();
    }

    public void onPaySubmit(View view) {

        String productPriceStr = AndroidUtil.stringify(productPriceEditText.getText());
        if (TextUtils.isEmpty(productPriceStr)) {
            Toast.makeText(getApplicationContext(), "null payment", Toast.LENGTH_LONG).show();
            return;
        }

        Double productPrice = Double.valueOf(productPriceStr);
        if (productPrice <= 0D) {
            Toast.makeText(getApplicationContext(), "0 payment", Toast.LENGTH_LONG).show();
            return;
        }

        readCurencyFromSpinner(ratesSpinner.getSelectedItem().toString());
        Double taxAmount = 0D;
        // You can set the Amouut solely
        SdkRequest sdkRequest = new SdkRequest(productPrice, ratesSpinner.getSelectedItem().toString(), billingSwitch.isChecked(), emailSwitch.isChecked(), shippingSwitch.isChecked());

//        // Or you can set the Amount with tax, this will override setAmount()
//        // The total purchase amount will be the sum of both numbers
//        if (taxAmountPrecentage > 0D) {
//            sdkRequest.setAmountWithTax(productPrice, productPrice * (taxAmountPrecentage / 100));
//        } else {
//            sdkRequest.setAmountNoTax(productPrice);
//        }


        sdkRequest.setAllowCurrencyChange(allowCurrencyChangeSwitch.isChecked());
        try {
            sdkRequest.verify();
        } catch (BSPaymentRequestException e) {
            showDialog("SdkRequest error:" + e.getMessage());
            Log.d(TAG, sdkRequest.toString());
            finish();
        }

        // Set special tax policy: non-US pay no tax; MA pays 10%, other US states pay 5%
        sdkRequest.setTaxCalculator(new TaxCalculator() {
            @Override
            public void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails) {
                if ("us".equalsIgnoreCase(shippingCountry)) {
                    Double taxRate = 0.05;
                    if ("ma".equalsIgnoreCase(shippingState)) {
                        taxRate = 0.1;
                    }
                    priceDetails.setTaxAmount(priceDetails.getSubtotalAmount() * taxRate);
                } else {
                    priceDetails.setTaxAmount(0D);
                }
            }
        });

        try {
            bluesnapService.setSdkRequest(sdkRequest);
            Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
            startActivityForResult(intent, BluesnapCheckoutActivity.REQUEST_CODE_DEFAULT);
        } catch (BSPaymentRequestException e) {
            Log.e(TAG, "payment request not validated: ", e);
            finish();
        }
    }

    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

        String returningOrNewShopper = "";
        String returningShopperId = returningShopperEditText.getText().toString();
        if (returningShopperSwitch.isChecked() && returningShopperId.length() >= 4) {
            returningOrNewShopper = "?shopperId=" + returningShopperId;
        }

        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                    ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
                    headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                    BlueSnapHTTPResponse post = HTTPOperationController.post(SANDBOX_URL + SANDBOX_TOKEN_CREATION + returningOrNewShopper, null, "application/json", "application/json", headerParams);
                    if (post.getResponseCode() == HTTP_CREATED && post.getHeaders() != null) {
                        String location = post.getHeaders().get("Location").get(0);
                        merchantToken = location.substring(location.lastIndexOf('/') + 1);
                        tokenServiceInterface.onServiceSuccess();

                    } else {

                        tokenServiceInterface.onServiceFailure();
                    }

                } catch (Exception e) {
                    tokenServiceInterface.onServiceFailure();
                }

            }
        };


        MainApplication.mainHandler.post(myRunnable);

    }

    /**
     * This method gets called after we create a token, and we have a returning shopper, we show how
     * to call bluesnapService.getShopperConfiguration() and get the shopper details.
     * In this case, we display the name and chosen payment method on the screen.
     */
    private void updateReturningShopperDetails() {
        if (returningShopperSwitch.isChecked()) {
            ShopperConfiguration shopperInfo = bluesnapService.getShopperConfiguration();
            String shopperInfoText = "";
            if (shopperInfo != null) {
                BillingInfo billingInfo = shopperInfo.getBillingInfo();
                shopperInfoText = billingInfo.getFullName();
                ChosenPaymentMethod chosenPaymentMethod = shopperInfo.getChosenPaymentMethod();
                if (chosenPaymentMethod != null) {
                    shopperInfoText += "; Payment method: " + chosenPaymentMethod.getChosenPaymentMethodType().name();
                    CreditCard creditCard = chosenPaymentMethod.getCreditCard();
                    if (creditCard != null) {
                        shopperInfoText += " " + creditCard.getCardLastFourDigits();
                    }
                }
            }
            shopperDetailsTextView.setText(shopperInfoText);
            shopperDetailsTextView.setVisibility(View.VISIBLE);
        } else {
            shopperDetailsTextView.setVisibility(View.INVISIBLE);
        }
    }

    //TODO: Find a mock merchant service t¡o provide this
    private void generateMerchantToken() {

        // create the interface for activating the token creation from server

        progressBar.setVisibility(View.VISIBLE);

        merchantTokenService(new TokenServiceInterface() {
            @Override
            public void onServiceSuccess() {
                initControlsAfterToken();
            }

            @Override
            public void onServiceFailure() {
                BluesnapAlertDialog.setDialog(DemoMainActivity.this, "Cannot obtain token from merchant server", "Service error", new BluesnapAlertDialog.BluesnapDialogCallback() {
                    @Override
                    public void setPositiveDialog() {
                        finish();
                    }

                    @Override
                    public void setNegativeDialog() {
                        //generateMerchantToken();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }, "Close", "Cancel");
            }
        });
    }

    private void initControlsAfterToken() {
        final String merchantStoreCurrency = null != merchantStoreCurrencySpinner && null != merchantStoreCurrencySpinner.getSelectedItem() ? merchantStoreCurrencySpinner.getSelectedItem().toString() : "USD";
        //final String merchantStoreCurrency = (null == currency || null == currency.getCurrencyCode()) ? "USD" : currency.getCurrencyCode();
        bluesnapService.setup(merchantToken, tokenProvider, merchantStoreCurrency, getApplicationContext(), new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (null == currency || null == currency.getCurrencyCode()) {
                            Set<String> supportedRates = bluesnapService.getSupportedRates();
                            if (supportedRates != null) {
                                updateSpinnerAdapterFromRates(demoSupportedRates(supportedRates));
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        linearLayoutForProgressBar.setVisibility(View.VISIBLE);
                        productPriceEditText.setVisibility(View.VISIBLE);
                        productPriceEditText.requestFocus();
                        updateReturningShopperDetails();

                    }
                });


            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Failed to setup sdk");

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (data != null) {
                String sdkErrorMsg = "SDK Failed to process the request:";
                sdkErrorMsg += data.getStringExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG);
                showDialog(sdkErrorMsg);
            } else {
                showDialog("Purchase canceled");
            }
            return;
        }

        // Here we can access the payment result
        Bundle extras = data.getExtras();
        SdkResult sdkResult = data.getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);

        //Start a demo activity that shows purchase summary.
        Intent intent = new Intent(getApplicationContext(), PostPaymentActivity.class);
        intent.putExtra("MERCHANT_TOKEN", merchantToken);
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);

        /*// If shipping information is available show it, Here we simply log the shipping info.
        ShippingContactInfo shippingInfo = (ShippingContactInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        if (shippingInfo != null) {
            Log.d(TAG, "ShippingContactInfo " + shippingInfo.toString());
            intent.putExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS, shippingInfo);
        }

        // If billing information is available show it, Here we simply log the billing info.
        BillingContactInfo billingInfo = (BillingContactInfo) extras.get(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS);
        if (billingInfo != null) {
            Log.d(TAG, "BillingContactInfo " + billingInfo.toString());
            intent.putExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS, billingInfo);
        }*/

        startActivity(intent);

        //Recreate the demo activity
        merchantToken = null;
        recreate();
    }

    public String getMerchantToken() {
        if (merchantToken == null) {

            generateMerchantToken();
        }
        return merchantToken;
    }

    /**
     * We only show a subset of all available rates in our demo app.
     *
     * @param supportedRates - Set<String> supportedRates
     * @return - TreeSet<String>
     */

    private TreeSet<String> demoSupportedRates(Set<String> supportedRates) {
        TreeSet<String> treeSet = new TreeSet<>();
        if (supportedRates.contains("USD")) {
            treeSet.add("USD");
        }
        if (supportedRates.contains("CAD")) {
            treeSet.add("CAD");
        }
        if (supportedRates.contains("EUR")) {
            treeSet.add("EUR");
        }
        if (supportedRates.contains("GBP")) {
            treeSet.add("GBP");
        }
        if (supportedRates.contains("ILS")) {
            treeSet.add("ILS");
        }
        return treeSet;
    }
}
