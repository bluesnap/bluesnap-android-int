package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

import java.text.DecimalFormat;


public class PostPaymentActivity extends Activity {

    private static final String TAG = PostPaymentActivity.class.getSimpleName();
    private TextView continueShippingView;
    private DemoTransactions transactions;
    private TextView transactionResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_payment);
        PaymentResult paymentResult = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
        ShippingInfo shippingInfo = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        BillingInfo billingInfo = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS);
        TextView paymentResultTextView2
                = (TextView) findViewById(R.id.paymentResultTextView2);
        continueShippingView = (TextView) findViewById(R.id.continueShippingButton);
        continueShippingView.setVisibility(View.GONE);
        transactionResultTextView = (TextView) findViewById(R.id.transactionResult);
        transactionResultTextView.setVisibility(View.INVISIBLE);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        paymentResultTextView2.setText("Your payment of  " + paymentResult.getCurrencyNameCode() + " " + decimalFormat.format(paymentResult.getAmount()) + " has been sent.");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String merchantToken = extras.getString("MERCHANT_TOKEN");
            Log.d(TAG, "Payment Result:\n " + paymentResult.toString());

            transactions = DemoTransactions.getInstance();
            transactions.setContext(this);

            if (!AndroidUtil.isBlank(paymentResult.getPaypalInvoiceId())) {
                setContinueButton(transactions.getMessage(), transactions.getTitle());
                setDialog("Transaction success with id:" + paymentResult.getPaypalInvoiceId(), "Paypal transaction");
            } else if (paymentResult.isReturningTransaction()) {
                transactions.createCreditCardTransaction(paymentResult.getShopperFirstName(), paymentResult.getShopperLastName(), merchantToken, paymentResult.getCurrencyNameCode(), paymentResult.getAmount(), true, paymentResult.getLast4Digits(), paymentResult.getCardType(), new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }

                    @Override
                    public void onFailure() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                }, paymentResult.getKountSessionId());
            } else {
                setDialog(paymentResult.toString() + "\n" + shippingInfo + "\n" + billingInfo, "Payment Result");
                transactions.createCreditCardTransaction(paymentResult.getShopperFirstName(), paymentResult.getShopperLastName(), merchantToken, paymentResult.getCurrencyNameCode(), paymentResult.getAmount(), new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }

                    @Override
                    public void onFailure() {
                        setContinueButton(transactions.getMessage(), transactions.getTitle());
                    }
                }, paymentResult.getKountSessionId());
            }
        }
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
        transactionResultTextView.setText(String.format("%s \n %s", title,message));
        transactionResultTextView.setVisibility(View.VISIBLE);
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

