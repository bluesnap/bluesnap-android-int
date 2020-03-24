package com.bluesnap.android.demoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import org.json.JSONException;

import java.text.DecimalFormat;


public class PostPaymentActivity extends AppCompatActivity {

    private static final String TAG = PostPaymentActivity.class.getSimpleName();
    private TextView continueShippingView;
    private DemoTransactions transactions;
    private TextView transactionResultTextView;
    private TextView shopperIdTextView;
    private TextView tokenSuffixTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_payment);
        final SdkResult sdkResult = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
        boolean isSubscription = getIntent().getBooleanExtra(BluesnapCheckoutActivity.EXTRA_SUBSCRIPTION_RESULT, false);
        Bundle extras = getIntent().getExtras();
        ShippingContactInfo shippingContactInfo = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        BillingContactInfo billingContactInfo = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS);
        TextView paymentResultTextView2 = (TextView) findViewById(R.id.paymentResultTextView2);
        TextView threeDSResultTextView = (TextView) findViewById(R.id.threeDSResult);
        continueShippingView = (TextView) findViewById(R.id.continueShippingButton);
        continueShippingView.setVisibility(View.GONE);
        transactionResultTextView = (TextView) findViewById(R.id.transactionResult);
        transactionResultTextView.setVisibility(View.INVISIBLE);
        shopperIdTextView = (TextView) findViewById(R.id.shopperId);
        shopperIdTextView.setVisibility(View.INVISIBLE);
        tokenSuffixTextView = (TextView) findViewById(R.id.tokenSuffix);
        tokenSuffixTextView.setVisibility(View.INVISIBLE);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        paymentResultTextView2.setText("Your payment of  " + (sdkResult.getCurrencyNameCode() == null ? " " : sdkResult.getCurrencyNameCode())
                + " " + (sdkResult.getAmount().isNaN() ? " " : decimalFormat.format(sdkResult.getAmount())) + " has been sent.");
        threeDSResultTextView.setText("3DS Result: " + sdkResult.getThreeDSAuthenticationResult());
//        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // String merchantToken = extras.getString("MERCHANT_TOKEN");
            Log.d(TAG, "Payment Result:\n " + sdkResult.toString());

            transactions = DemoTransactions.getInstance();
            transactions.setContext(this);

            if (!TextUtils.isEmpty(sdkResult.getPaypalInvoiceId())) {
                setContinueButton(transactions.getMessage(), transactions.getTitle());
                //setDialog("Transaction success with id:" + sdkResult.getPaypalInvoiceId(), "Paypal transaction");
            } else if (isSubscription) {
                MainApplication.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkMerchantSubscription(sdkResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                //setDialog(sdkResult.toString() + "\n" + shippingContactInfo + "\n" + billingContactInfo, "Payment Result");
                MainApplication.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkMerchantTX(sdkResult);
                    }
                });


            }
        }
    }

    private void checkMerchantTX(SdkResult sdkResult) {
        transactions.createCreditCardTransaction(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                });

            }
        });
    }

    private void checkMerchantSubscription(SdkResult sdkResult) throws JSONException {
        transactions.createSubscriptionCharge(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                });

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        continueShippingView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement'
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackFromThankyou(View view) {
        onBackPressed();
    }

    @MainThread
    public void setContinueButton(String message, String title) {
        transactionResultTextView.setText(String.format("%s \n %s", title, message));
        transactionResultTextView.setVisibility(View.VISIBLE);
        shopperIdTextView.setText(String.format("Shopper ID:\t %s", transactions.getShopperId()));
        shopperIdTextView.setVisibility(View.VISIBLE);
        tokenSuffixTextView.setText(String.format("Token Suffix:\t %s", transactions.getTokenSuffix()));
        tokenSuffixTextView.setVisibility(View.VISIBLE);
        continueShippingView.setVisibility(View.VISIBLE);
        continueShippingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setDialog(String dialogMessage, String dialogTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogMessage).setTitle(dialogTitle);
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
            Log.w(TAG, "failed to show dialog");
        }

    }
}

