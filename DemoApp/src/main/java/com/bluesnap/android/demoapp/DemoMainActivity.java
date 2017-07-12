package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;

public class DemoMainActivity extends Activity {

    private static final String TAG = "DemoMainActivity";
    protected BlueSnapService bluesnapService;
    private Spinner ratesSpinner;
    private EditText productPriceEditText;
    private Currency currency;
    private TextView currencySym;
    private String currencySymbol;
    private String initialPrice;
    private String displayedCurrency;
    private String currencyName;
    private PaymentRequest paymentRequest;
    private String merchantToken;
    private Currency currencyByLocale;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutForProgressBar;
    private Switch shippingSwitch;
    private EditText taxAmountEditText;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        linearLayoutForProgressBar = (LinearLayout) findViewById(R.id.mainLinearLayout);
        linearLayoutForProgressBar.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMerchant);
        shippingSwitch = (Switch) findViewById(R.id.shippingSwitch);
        shippingSwitch.setChecked(false);
        progressBar.setVisibility(View.VISIBLE);
        productPriceEditText = (EditText) findViewById(R.id.productPriceEditText);
        taxAmountEditText = (EditText) findViewById(R.id.demoTaxEditText);
        currencySym = (TextView) findViewById(R.id.currencySym);
        ratesSpinner = (Spinner) findViewById(R.id.rateSpinner);
        showDemoAppVersion();
        try {
            Locale current = getResources().getConfiguration().locale;
            currencyByLocale = Currency.getInstance(current);
        } catch (Exception e) {
            currencyByLocale = Currency.getInstance(Locale.getDefault());
        }
        bluesnapService = BlueSnapService.getInstance();
        generateMerchantToken();
    }

    private void showDemoAppVersion() {
        TextView demoVersionTextView = (TextView) findViewById(R.id.demoVersionTextView);
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            demoVersionTextView.setText(String.format(Locale.ENGLISH, "V:%s[%d]", versionName, versionCode));
        } catch (Exception e) {
            Log.e(TAG, "cannot extract version");
        }
    }

    private void ratesAdapterSelectionListener() {
        ratesSpinner.post(new Runnable() {
            @Override
            public void run() {
                ratesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (initialPrice == null && position != 0) {
                            initialPrice = productPriceEditText.getText().toString();
                        }
                        String selectedRateName = ratesSpinner.getSelectedItem().toString();
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
        String convertedPrice = "0";
        currencySymbol = currency.getSymbol();
        currencyName = currency.getCurrencyCode();
        if (initialPrice == null) {
            initialPrice = productPriceEditText.getText().toString().trim();
        }
        convertedPrice = bluesnapService.convertUSD(initialPrice, selectedRateName).trim();
        return convertedPrice;
    }

    private void showDialog(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_view, quotesArray);
        ratesSpinner.setAdapter(adapter);
        int currentposition = -1;
        for (String rate : quotesArray) {
            currentposition++;
            if (rate.equals(currencyByLocale.getCurrencyCode())) {
                break;
            }
        }
        ratesSpinner.setSelection(currentposition);
        ratesAdapterSelectionListener();
    }

    public void onPaySubmit(View view) {
        Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);

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
        String taxString = taxAmountEditText.getText().toString().trim();
        Double taxAmountPrecentage = 0D;
        if (!taxString.isEmpty()) {
            taxAmountPrecentage = Double.valueOf(taxAmountEditText.getText().toString().trim());
        }
        paymentRequest = new PaymentRequest();
        // You can set the Amout solely
        paymentRequest.setAmount(productPrice);

        // Or you can set the Amount with tax, this will override setAmount()
        // The total purchase amount will be the sum of both numbers
        if (taxAmountPrecentage > 0D) {
            paymentRequest.setAmountWithTax(productPrice, productPrice * (taxAmountPrecentage / 100));
        } else {
            paymentRequest.setAmount(productPrice);
        }

        paymentRequest.setCurrencyNameCode(currencyName);
        paymentRequest.setCustomTitle("Demo Merchant");

        if (shippingSwitch.isChecked()) {
            paymentRequest.setShippingRequired(true);
        }
        if (!paymentRequest.verify()) {
            showDialog("PaymentRequest error");
            Log.d(TAG, paymentRequest.toString());
            finish();
        }
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        startActivityForResult(intent, BluesnapCheckoutActivity.REQUEST_CODE_DEFAULT);
    }

    //TODO: Find a mock merchant service t¡o provide this
    private void generateMerchantToken() {
        progressBar.setVisibility(View.VISIBLE);

        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setBasicAuth(SANDBOX_USER, SANDBOX_PASS);
        httpClient.post(SANDBOX_URL+ SANDBOX_TOKEN_CREATION, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString, throwable);
                BluesnapAlertDialog.setDialog(DemoMainActivity.this, "Cannot obtain token from merchant server", "Service error", new BluesnapAlertDialog.BluesnapDialogCallback() {
                    @Override
                    public void setPositiveDialog() {
                        finish();
                    }

                    @Override
                    public void setNegativeDialog() {
                        generateMerchantToken();
                    }
                }, "Close", "Retry");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                merchantToken = DemoTransactions.extractTokenFromHeaders(headers);
                initControlsAfterToken();
            }

        });
    }

    private void initControlsAfterToken() {
        bluesnapService.setup(merchantToken);
        bluesnapService.updateRates(new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                Set<String> supportedRates = bluesnapService.getSupportedRates();
                updateSpinnerAdapterFromRates(demoSupportedRates(supportedRates));

                progressBar.setVisibility(View.INVISIBLE);
                linearLayoutForProgressBar.setVisibility(View.VISIBLE);

                productPriceEditText.setVisibility(View.VISIBLE);
                productPriceEditText.requestFocus();
            }

            @Override
            public void onFailure() {
                showDialog("unable to get rates quote from service");
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
        PaymentResult paymentResult = (PaymentResult) extras.get(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);


        // If shipping information is available show it, Here we simply log the shipping info.
        ShippingInfo shippingInfo = (ShippingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        if (shippingInfo != null) {
            Log.d(TAG, shippingInfo.toString());
        }

        //Start a demo activity that shows purchase summary.
        Intent intent = new Intent(getApplicationContext(), PostPaymentActivity.class);
        intent.putExtra("MERCHANT_TOKEN", merchantToken);
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, paymentResult);
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
     * @param supportedRates
     * @return
     */

    private TreeSet<String> demoSupportedRates(Set<String> supportedRates) {
        TreeSet<String> treeSet = new TreeSet();
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
