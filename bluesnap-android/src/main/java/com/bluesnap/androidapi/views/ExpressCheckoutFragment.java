package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by oz on 12/2/15.
 */
public class ExpressCheckoutFragment extends Fragment implements BluesnapPaymentFragment {
    public static final String TAG = ExpressCheckoutFragment.class.getSimpleName();
    private static PaymentRequest paymentRequest;
    private static FragmentManager fragmentManager;
    private Double paypalPurchaseAmount;
    private String paypalCurrencyNumCode;
    private ProgressBar progressBar;
    private TextView totalAmount;
    //private DecimalFormat decimalFormat;

    public ExpressCheckoutFragment() {
        BlueSnapService.getBus().register(this);
    }


    public static ExpressCheckoutFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
        ExpressCheckoutFragment bsFragment =
                (ExpressCheckoutFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            // Create a new fragment.
            bsFragment = new ExpressCheckoutFragment();
            bsFragment.setArguments(bundle);
        }

        return bsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.bluesnap_expresscheckout, container, false);
        progressBar = (ProgressBar) inflate.findViewById(R.id.progressBarExpressCheckout);
        totalAmount = (TextView) inflate.findViewById(R.id.express_co_total);
        if (BlueSnapService.getInstance().getsDKConfiguration().getSupportedPaymentMethods().isPaymentMethodActive(SupportedPaymentMethods.PAYPAL)) {
            ImageButton paypalBtn = (ImageButton) inflate.findViewById(R.id.express_co_btn_paypal);
            paypalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String payPalToken = BlueSnapService.getPayPalToken();
                    if ("".equals(payPalToken)) {
                        Log.d(TAG, "create payPalToken");
                        startPayPal();
                    } else {
                        Log.d(TAG, "startWebViewActivity");
                        startWebViewActivity(payPalToken);
                    }

                }
            });
        }
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFromPaymentRequest();
        View currentFocusView = getActivity().getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    private void updateFromPaymentRequest() {
        paymentRequest = BlueSnapService.getInstance().getPaymentRequest();
        if (paymentRequest == null)
            return;
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        paypalPurchaseAmount = paymentRequest.getAmount();
        paypalCurrencyNumCode = paymentRequest.getCurrencyNameCode();
        totalAmount.setText(getResources().getString(R.string.total) + " " + AndroidUtil.getCurrencySymbol(paypalCurrencyNumCode) + " " + decimalFormat.format(paypalPurchaseAmount));
    }

    @Subscribe
    public void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent) {
        String currencySymbol = AndroidUtil.getCurrencySymbol(currencyUpdatedEvent.newCurrencyNameCode);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        paypalCurrencyNumCode = currencyUpdatedEvent.newCurrencyNameCode;
        paypalPurchaseAmount = currencyUpdatedEvent.updatedPrice;

        totalAmount.setText(getResources().getString(R.string.total) + " " + AndroidUtil.getCurrencySymbol(currencyUpdatedEvent.newCurrencyNameCode) + " " + decimalFormat.format(currencyUpdatedEvent.updatedPrice));

    }

    private void startPayPal() {
        progressBar.setVisibility(View.VISIBLE);
        BlueSnapService.getInstance().createPayPalToken(paymentRequest.getAmount(), paymentRequest.getCurrencyNameCode(), new BluesnapServiceCallback() {
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
                    String message;
                    String title;
                    if (errorDescription.getString("code").equals("20027")) {
                        // ToDo change to string.xml for translations, use string palceholders
                        //message = errorDescription.getString("description") + " please change to a PayPal supported Currency or contact Support for additional assistance";
                        message = getString(R.string.CURRENCY_NOT_SUPPORTED_PART_1)
                                + " "
                                + paymentRequest.getCurrencyNameCode()
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

                    } else {
                        message = getString(R.string.SUPPORT_PLEASE)
                                + " "
                                + getString(R.string.SUPPORT);

                        title = getString(R.string.ERROR);
                    }
                    BluesnapAlertDialog.setDialog(getActivity(), message, title);
                } catch (Exception e) {
                    Log.e(TAG, "json parsing exception", e);
                    BluesnapAlertDialog.setDialog(getActivity(), "Paypal service error", "Error"); //TODO: friendly error
                } finally {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void startWebViewActivity(String payPalUrl) {
        Intent newIntent;
        newIntent = new Intent(getActivity(), WebViewActivity.class);
        // Todo change paypal header name to merchant name from payment request
        newIntent.putExtra(getString(R.string.WEBVIEW_STRING), "PayPal");
        newIntent.putExtra(getString(R.string.WEBVIEW_URL), payPalUrl);
        newIntent.putExtra(getString(R.string.SET_JAVA_SCRIPT_ENABLED), true);
        startActivityForResult(newIntent, WebViewActivity.PAYPAL_REQUEST_CODE);
        //startActivity(newIntent);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "got result " + resultCode);
        Log.d(TAG, "got request " + requestCode);
        if (requestCode == WebViewActivity.PAYPAL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }
}
