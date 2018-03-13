package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.KountService;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.components.AmountTaxShippingComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.fragments.NewCreditCardFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperBillingFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperCreditCardFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperShippingFragment;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class CreditCardActivity extends AppCompatActivity {
    private static final String TAG = CreditCardActivity.class.getSimpleName();
    public static final int CREDIT_CARD_ACTIVITY_REQUEST_CODE = 3;
    public static final String EXTRA_PAYMENT_RESULT = "com.bluesnap.intent.BSNAP_PAYMENT_RESULT";
    public static final String EXTRA_SHIPPING_DETAILS = "com.bluesnap.intent.BSNAP_SHIPPING_DETAILS";
    public static final String EXTRA_BILLING_DETAILS = "com.bluesnap.intent.BSNAP_BILLING_DETAILS";
    public static final String SDK_ERROR_MSG = "SDK_ERROR_MESSAGE";
    private static final int RESULT_SDK_FAILED = -2;
    private String fragmentType;
    private TextView headerTextView;
    private String sharedCurrency;
    private ImageButton hamburgerMenuButton;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_card_activity);

        fragmentType = getIntent().getStringExtra(ChoosePaymentMethodActivity.FRAGMENT_TYPE);
        if (ChoosePaymentMethodActivity.NEW_CC.equals(fragmentType))
            startActivityWithNewCreditCardFragment();
        else if (ChoosePaymentMethodActivity.RETURNING_CC.equals(fragmentType))
            startActivityWithReturningShopperCreditCardFragment();

        headerTextView = (TextView) findViewById(R.id.headerTextView);
        hamburgerMenuButton = (ImageButton) findViewById(R.id.hamburger_button);
        hamburgerMenuButton.setOnClickListener(new hamburgerMenuListener(hamburgerMenuButton));

        BlueSnapService.getBus().register(this);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_REQUEST, broadcastReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlueSnapLocalBroadcastManager.unregisterReceiver(this, broadcastReceiver);
    }

    /**
     * start this Activity With the New Credit Card Fragment
     */
    private void startActivityWithNewCreditCardFragment() {
        NewCreditCardFragment newCreditCardFragment = NewCreditCardFragment.newInstance(CreditCardActivity.this, new Bundle());
        getFragmentManager().beginTransaction()
                .add(R.id.creditCardFrameLayout, newCreditCardFragment).commit();
    }

    /**
     * start this Activity With the Returning Shopper Credit Card Fragment
     */
    private void startActivityWithReturningShopperCreditCardFragment() {
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT, broadcastReceiver);

        ReturningShopperCreditCardFragment returningShopperCreditCardFragment = ReturningShopperCreditCardFragment.newInstance(CreditCardActivity.this, new Bundle());
        getFragmentManager().beginTransaction()
                .add(R.id.creditCardFrameLayout, returningShopperCreditCardFragment).commit();
    }

    /**
     * set Header Text View text according to fragment class simple name
     *
     * @param fragmentClassSimpleName - fragment.getClass().getSimpleName()
     */
    public void setHeaderTextView(String fragmentClassSimpleName) {
        String text = getResources().getString(R.string.payment);

        if (fragmentClassSimpleName.equals(ReturningShopperBillingFragment.TAG))
            text = getResources().getString(R.string.billing);
        else if (fragmentClassSimpleName.equals(ReturningShopperShippingFragment.TAG))
            text = getResources().getString(R.string.shipping);

        headerTextView.setText(text);
    }

    /**
     * replace Fragment in Activity
     *
     * @param fragment - {@link Fragment}
     */
    private void replaceFragmentPlacement(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.creditCardFrameLayout, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commit();

        setHeaderTextView(fragment.getClass().getSimpleName());

    }

    /**
     * Broadcast Receiver for Credit Card Activity
     * Handles actions
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String event = intent.getAction();
            Log.d(TAG, event);

            if (BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE.equals(event)
                    || BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE.equals(event)) {
                getFragmentManager().popBackStack();
                setHeaderTextView(ReturningShopperCreditCardFragment.TAG);
            } else if (BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT.equals(event)) {
                replaceFragmentPlacement(ReturningShopperBillingFragment.newInstance(CreditCardActivity.this, new Bundle()));
            } else if (BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT.equals(event)) {
                replaceFragmentPlacement(ReturningShopperShippingFragment.newInstance(CreditCardActivity.this, new Bundle()));
            } else {
                Intent newIntent = new Intent(getApplicationContext(), CountryActivity.class);
                String countryString = intent.getStringExtra(event);
                newIntent.putExtra(getString(R.string.COUNTRY_STRING), countryString);
                startActivityForResult(newIntent, Activity.RESULT_FIRST_USER);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
            BlueSnapLocalBroadcastManager.sendMessage(getApplicationContext(), BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_RESPONSE, data.getStringExtra("result"), TAG);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    private Boolean menuSwitch(int i) {
        Intent newIntent;

        if (i == R.id.id_currency) {
            newIntent = new Intent(getApplicationContext(), CurrencyActivity.class);
            newIntent.putExtra(getString(R.string.CURRENCY_STRING), sharedCurrency);
            startActivity(newIntent);
            return true;
        } else {
            return false;
        }
    }

    private class hamburgerMenuListener implements View.OnClickListener {
        private ImageButton hamburgerMenuButton;

        public hamburgerMenuListener(ImageButton hamburgerMenuButton) {
            this.hamburgerMenuButton = hamburgerMenuButton;
        }

        public void onClick(final View v) {
            sharedCurrency = BlueSnapService.getInstance().getSdkRequest().getCurrencyNameCode();
            invalidateOptionsMenu();
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    hamburgerMenuButton.setImageResource(R.drawable.menu_button);
                    popupMenu.dismiss();
                }
            });
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Handle item selection
                    int i = item.getItemId();
                    return menuSwitch(i);
                }
            });

            popupMenu.inflate(R.menu.menu_main);
            String currentCurrency = getString(R.string.currency) + " " + sharedCurrency;
            popupMenu.getMenu().add(1, R.id.id_currency, 1, currentCurrency);
            popupMenu.show();
        }
    }

    @Subscribe
    public void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent) {
        BlueSnapLocalBroadcastManager.sendMessage(this, BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, TAG);
    }

    public void finishFromFragment(final Shopper shopper) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SHIPPING_DETAILS, shopper.getShippingContactInfo());
        resultIntent.putExtra(EXTRA_BILLING_DETAILS, shopper.getNewCreditCardInfo().getBillingContactInfo());

        Log.d(TAG, "Testing if card requires server tokenization:" + shopper.getNewCreditCardInfo().getCreditCard().toString());
        try {
            tokenizeCardOnServer(shopper, resultIntent);
        } catch (UnsupportedEncodingException | JSONException e) {
            String errorMsg = "SDK service error";
            Log.e(TAG, errorMsg, e);
            setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
            finish();
        }
    }

    private void tokenizeCardOnServer(final Shopper shopper, final Intent resultIntent) throws UnsupportedEncodingException, JSONException {

        blueSnapService.submitTokenizedDetails(shopper, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    String Last4;
                    String ccType;
                    SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

                    if (shopper.getNewCreditCardInfo().getCreditCard().getIsNewCreditCard()) {
                        // New Card
                        JSONObject response = new JSONObject(responseString);
                        Last4 = response.getString("last4Digits");
                        ccType = response.getString("ccType");
                        Log.d(TAG, "tokenization of new credit card");
                    } else {
                        // Reused Card
                        sdkResult.setShopperID(String.valueOf(shopper.getVaultedShopperId()));
                        Last4 = shopper.getNewCreditCardInfo().getCreditCard().getCardLastFourDigits();
                        ccType = shopper.getNewCreditCardInfo().getCreditCard().getCardType();
                        Log.d(TAG, "tokenization of previous used credit card");
                    }

                    sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
                    sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
                    // update last4 from server result
                    sdkResult.setLast4Digits(Last4);
                    // update card type from server result
                    sdkResult.setCardType(ccType);
                    resultIntent.putExtra(EXTRA_PAYMENT_RESULT, sdkResult);
                    setResult(RESULT_OK, resultIntent);
                    //Only set the remember shopper here since failure can lead to missing tokenization on the server
                    shopper.getNewCreditCardInfo().getCreditCard().setTokenizationSuccess();
                    Log.d(TAG, "tokenization finished");
                    finish();
                } catch (NullPointerException | JSONException e) {
                    Log.e(TAG, "", e);
                    String errorMsg = String.format("Service Error %s", e.getMessage());
                    setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));   //TODO Display error to the user
                    finish();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // check if failure is EXPIRED_TOKEN if so activating the create new token mechanism.
                if (statusCode == 400 && null != blueSnapService.getTokenProvider() && !"".equals(responseString)) {
                    try {
                        JSONObject errorResponse = new JSONObject(responseString);
                        JSONArray rs2 = (JSONArray) errorResponse.get("message");
                        JSONObject rs3 = (JSONObject) rs2.get(0);
                        if ("EXPIRED_TOKEN".equals(rs3.get("errorName"))) {
                            blueSnapService.getTokenProvider().getNewToken(new TokenServiceCallback() {
                                @Override
                                public void complete(String newToken) {
                                    blueSnapService.setNewToken(newToken);
                                    try {
                                        tokenizeCardOnServer(shopper, resultIntent);
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(TAG, "Unsupported Encoding Exception", e);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "json parsing exception", e);
                                    }
                                }
                            });
                        } else {
                            String errorMsg = String.format("Service Error %s, %s", statusCode, responseString);
                            Log.e(TAG, errorMsg, throwable);
                            setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                    }
                } else {
                    String errorMsg = String.format("Service Error %s, %s", statusCode, responseString);
                    Log.e(TAG, errorMsg, throwable);
                    setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
                    finish();
                }
            }
        });

    }
}
