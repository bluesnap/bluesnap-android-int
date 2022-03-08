package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkRequestShopperRequirements;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.CardinalManager;
import com.bluesnap.androidapi.services.KountService;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.fragments.BlueSnapFragment;
import com.bluesnap.androidapi.views.fragments.NewCreditCardFragment;
import com.bluesnap.androidapi.views.fragments.NewCreditCardShippingFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperBillingFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperCreditCardFragment;
import com.bluesnap.androidapi.views.fragments.ReturningShopperShippingFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class CreditCardActivity extends AppCompatActivity {
    private static final String TAG = CreditCardActivity.class.getSimpleName();
    public static final int CREDIT_CARD_ACTIVITY_REQUEST_CODE = 3;
    public static final int CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE = 4;
    public static final int RESULT_COUNTRY = 11;
    public static final int RESULT_STATE = 12;
    private String fragmentType;
    private TextView headerTextView;
    private String sharedCurrency;
    private ImageButton hamburgerMenuButton;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private SdkRequestBase sdkRequest;
    private NewCreditCardShippingFragment newCreditCardShippingFragment;
    private String cardinalResult;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && BlueSnapService.getInstance().getSdkRequest() == null) {
            Log.e(TAG, "savedInstanceState missing");
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            finish();
            return;
        }

        setContentView(R.layout.credit_card_activity);

        // recovering the instance state
        if (null != savedInstanceState && null != savedInstanceState.getString("fragmentType"))
            fragmentType = savedInstanceState.getString("fragmentType");
        else
            fragmentType = getIntent().getStringExtra(BluesnapCheckoutActivity.FRAGMENT_TYPE);

        if (BluesnapCheckoutActivity.NEW_CC.equals(fragmentType))
            startActivityWithNewCreditCardFragment();
        else if (NewCreditCardShippingFragment.TAG.equals(fragmentType)) {
            newCreditCardShippingFragment = NewCreditCardShippingFragment.newInstance(CreditCardActivity.this, new Bundle());
            replaceBlueSnapFragment(R.id.creditCardFrameLayout, newCreditCardShippingFragment);
        } else {
            startActivityWithReturningShopperCreditCardFragment();
        }

        headerTextView = findViewById(R.id.headerTextView);
        hamburgerMenuButton = findViewById(R.id.hamburger_button);
        if (BlueSnapService.getInstance().getSdkRequest().isAllowCurrencyChange()) {
            hamburgerMenuButton.setOnClickListener(new hamburgerMenuListener(hamburgerMenuButton));
        } else {
            setHamburgerMenuButtonVisibility(View.INVISIBLE);
        }

        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_REQUEST, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.STATE_CHANGE_REQUEST, broadcastReceiver);

        progressBar = findViewById(R.id.payProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    /**
     * invoked when the activity may be temporarily destroyed, save the instance state here
     *
     * @param outState {@link Bundle}
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("fragmentType", fragmentType);
        getBlueSnapFragment().onActivitySavedInstanceState(outState);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlueSnapLocalBroadcastManager.unregisterReceiver(this, broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        getBlueSnapFragment().onActivityBackPressed();

        super.onBackPressed();

        if (NewCreditCardShippingFragment.TAG.equals(fragmentType)) {
            getBlueSnapFragment().registerBlueSnapLocalBroadcastReceiver();
            if (NewCreditCardFragment.class.getSimpleName().equals(getBlueSnapFragmentClassSimpleName()))
                setHeaderTextView(NewCreditCardFragment.TAG);
        } else if (ReturningShopperCreditCardFragment.TAG.equals(getBlueSnapFragmentClassSimpleName())) {
            setHeaderTextView(ReturningShopperCreditCardFragment.TAG);
            setHamburgerMenuButtonVisibility(View.VISIBLE);
        }
    }

    /**
     * start this Activity With the New Credit Card Fragment
     */
    private void startActivityWithNewCreditCardFragment() {
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.NEW_CARD_SHIPPING_CHANGE, broadcastReceiver);

        NewCreditCardFragment newCreditCardFragment = NewCreditCardFragment.newInstance(CreditCardActivity.this, new Bundle());
        replaceBlueSnapFragment(R.id.creditCardFrameLayout, newCreditCardFragment);
    }

    /**
     * start this Activity With the Returning Shopper Credit Card Fragment
     */
    private void startActivityWithReturningShopperCreditCardFragment() {
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(this, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT, broadcastReceiver);

        BlueSnapFragment blueSnapFragment = ReturningShopperCreditCardFragment.newInstance(CreditCardActivity.this, new Bundle());

        if (fragmentType.equals(ReturningShopperBillingFragment.TAG))
            blueSnapFragment = ReturningShopperBillingFragment.newInstance(CreditCardActivity.this, new Bundle());
        else if (fragmentType.equals(ReturningShopperShippingFragment.TAG))
            blueSnapFragment = ReturningShopperShippingFragment.newInstance(CreditCardActivity.this, new Bundle());

        replaceBlueSnapFragment(R.id.creditCardFrameLayout, blueSnapFragment);
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
        else if (fragmentClassSimpleName.equals(ReturningShopperShippingFragment.TAG) || fragmentClassSimpleName.equals(NewCreditCardShippingFragment.TAG))
            text = getResources().getString(R.string.shipping);

        headerTextView.setText(text);
    }

    /**
     * set HamburgerMenuButton Visibility
     *
     * @param visibility - View.VISIBLE, View.INVISIBLE, View.GONE {@link View}
     */
    public void setHamburgerMenuButtonVisibility(int visibility) {
        if (BlueSnapService.getInstance().getSdkRequest().isAllowCurrencyChange())
            this.hamburgerMenuButton.setVisibility(visibility);
        else
            this.hamburgerMenuButton.setVisibility(View.INVISIBLE);
    }

    /**
     * replace Fragment in Activity
     *
     * @param fragment - {@link Fragment}
     */
    private void replaceFragmentPlacement(Fragment fragment) {
        fragmentType = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (!fragmentType.equals(NewCreditCardShippingFragment.TAG))
            fragmentTransaction.replace(R.id.creditCardFrameLayout, fragment);
        else
            fragmentTransaction.add(R.id.creditCardFrameLayout, fragment);
        fragmentTransaction.addToBackStack(fragmentType);
        fragmentTransaction.commit();

        setHeaderTextView(fragmentType);
    }

    /**
     * Broadcast Receiver for Credit Card Activity;
     * Handles actions
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String event = intent.getAction();
            Log.d(TAG, event);

            if (BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE.equals(event)
                    || BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE.equals(event)) {
                replaceBlueSnapFragment(R.id.creditCardFrameLayout, ReturningShopperCreditCardFragment.newInstance(CreditCardActivity.this, new Bundle()));
                setHeaderTextView(ReturningShopperCreditCardFragment.TAG);
                setHamburgerMenuButtonVisibility(View.VISIBLE);
            } else if (BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT.equals(event)) {
                replaceFragmentPlacement(ReturningShopperBillingFragment.newInstance(CreditCardActivity.this, new Bundle()));
                setHamburgerMenuButtonVisibility(View.INVISIBLE);
            } else if (BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT.equals(event)) {
                replaceFragmentPlacement(ReturningShopperShippingFragment.newInstance(CreditCardActivity.this, new Bundle()));
                setHamburgerMenuButtonVisibility(View.INVISIBLE);
            } else if (BlueSnapLocalBroadcastManager.NEW_CARD_SHIPPING_CHANGE.equals(event)) {
                newCreditCardShippingFragment = NewCreditCardShippingFragment.newInstance(CreditCardActivity.this, new Bundle());
                replaceFragmentPlacement(newCreditCardShippingFragment);
            } else {
                Intent newIntent;
                int requestCode;
                if (BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_REQUEST.equals(event)) {
                    newIntent = new Intent(getApplicationContext(), CountryActivity.class);
                    String countryString = intent.getStringExtra(event);
                    newIntent.putExtra(getString(R.string.COUNTRY_STRING), countryString);
                    requestCode = RESULT_COUNTRY;
                } else {
                    newIntent = new Intent(getApplicationContext(), StateActivity.class);
                    String countryString = intent.getStringExtra(getString(R.string.COUNTRY_STRING));
                    newIntent.putExtra(getString(R.string.COUNTRY_STRING), countryString);
                    String stateString = intent.getStringExtra(getString(R.string.STATE_STRING));
                    newIntent.putExtra(getString(R.string.STATE_STRING), stateString);
                    requestCode = RESULT_STATE;
                }
                startActivityForResult(newIntent, requestCode);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_COUNTRY)
                BlueSnapLocalBroadcastManager.sendMessage(getApplicationContext(), BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_RESPONSE, data.getStringExtra("result"), TAG);
            else if (requestCode == RESULT_STATE)
                BlueSnapLocalBroadcastManager.sendMessage(getApplicationContext(), BlueSnapLocalBroadcastManager.STATE_CHANGE_RESPONSE, data.getStringExtra("result"), TAG);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * menu Switch
     *
     * @param i - Id pressed on the menu the switch accordingly
     * @return boolean if exists
     */
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

    /**
     * hamburger Menu Listener
     * handles the menu button actions and update currency accordingly (View)
     */
    private class hamburgerMenuListener implements View.OnClickListener {
        private ImageButton hamburgerMenuButton;

        hamburgerMenuListener(ImageButton hamburgerMenuButton) {
            this.hamburgerMenuButton = hamburgerMenuButton;
        }

        public void onClick(final View v) {
            sharedCurrency = BlueSnapService.getInstance().getSdkRequest().getPriceDetails().getCurrencyCode();
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

    public void finishFromFragment(final Shopper shopper) {
        progressBar.setVisibility(View.VISIBLE);

        Intent resultIntent = new Intent();
        sdkRequest = BlueSnapService.getInstance().getSdkRequest();
        if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
            resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS, shopper.getShippingContactInfo());
        resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_BILLING_DETAILS, shopper.getNewCreditCardInfo().getBillingContactInfo());

        try {
            if (sdkRequest instanceof SdkRequestShopperRequirements) {
                setResult(RESULT_OK);
                blueSnapService.getsDKConfiguration().setShopper(shopper);
                finish();
            } else
                tokenizeCardOnServer(shopper, resultIntent);
        } catch (UnsupportedEncodingException | JSONException e) {
            String errorMsg = "SDK service error";
            Log.e(TAG, errorMsg, e);
            setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
            finish();
        }
    }

    /**
     * tokenize Card On Server,
     * receive shopper and activate api tokenization to the server according to SDK Request {@link com.bluesnap.androidapi.models.SdkRequest} spec
     *
     * @param shopper      - {@link Shopper}
     * @param resultIntent - {@link Intent}
     * @throws UnsupportedEncodingException - UnsupportedEncodingException
     * @throws JSONException                - JSONException
     */
    private void tokenizeCardOnServer(final Shopper shopper, final Intent resultIntent) throws UnsupportedEncodingException, JSONException {
        final PurchaseDetails purchaseDetails = new PurchaseDetails(
                shopper.getNewCreditCardInfo().getCreditCard(),
                shopper.getNewCreditCardInfo().getBillingContactInfo(),
                shopper.getShippingContactInfo(),
                shopper.isStoreCard());

        blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BlueSnapHTTPResponse response = blueSnapService.submitTokenizedDetails(purchaseDetails);
                    if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        if (sdkRequest.isActivate3DS()) {
                            cardinal3DS(purchaseDetails, shopper, resultIntent, response);
                        } else {
                            finishFromActivity(shopper, resultIntent, response);
                        }

                    } else if (response.getResponseCode() == 400 && null != blueSnapService.getTokenProvider() && !"".equals(response.getResponseString())) {
                        try {
                            JSONObject errorResponse = new JSONObject(response.getResponseString());
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
                                            Log.e(TAG, "json parsing exception");
                                        }
                                    }
                                });
                            } else {
                                finishFromActivityWithFailure(response.toString());
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing exception", e);
                        }
                    } else {
                        finishFromActivityWithFailure(response.toString());
                    }

                } catch (JSONException ex) {
                    Log.e(TAG, "JsonException");

                }
            }
        });
    }

    private void cardinal3DS(PurchaseDetails purchaseDetails, Shopper shopper, final Intent resultIntent, BlueSnapHTTPResponse response) {

        // Request auth with 3DS
        CardinalManager cardinalManager = CardinalManager.getInstance();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got broadcastReceiver intent");

                if (cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_CANCELED.name())) {
                    Log.d(TAG, "Cardinal challenge canceled");
                    progressBar.setVisibility(View.INVISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BluesnapAlertDialog.setDialog(CreditCardActivity.this, "3DS Authentication is required", "");
                        }
                    });

                } else if (cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_FAILED.name())
                        || cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.THREE_DS_ERROR.name())) { //cardinal internal error or authentication failure

                    // TODO: Change this after receiving "proceed with/without 3DS" from server in init API call
                    String error = intent.getStringExtra(CardinalManager.THREE_DS_AUTH_DONE_EVENT_NAME);
                    finishFromActivityWithFailure(error);

                } else { //cardinal success (success/bypass/unavailable/unsupported)
                    Log.d(TAG, "3DS Flow ended properly");
                    finishFromActivity(shopper, resultIntent, response);
                }
            }
        };

        BlueSnapLocalBroadcastManager.registerReceiver(this, CardinalManager.THREE_DS_AUTH_DONE_EVENT, broadcastReceiver);

        try {

            cardinalManager.authWith3DS(blueSnapService.getSdkResult().getCurrencyNameCode(), blueSnapService.getSdkResult().getAmount(), this, purchaseDetails.getCreditCard());

        } catch (JSONException e) {
            Log.d(TAG, "Error in parsing authWith3DS API response");
            finishFromActivityWithFailure();
        }

    }

    private void finishFromActivityWithFailure() {
        finishFromActivityWithFailure(null);
    }

    private void finishFromActivityWithFailure(String response) {
        String errorMsg;

        if (response != null) {
            errorMsg = "Service Error: " + response;
        } else {
            errorMsg = "SDK Error";
        }

        Log.e(TAG, errorMsg);
        setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
        finish();
    }

    /**
     * 3DS flow
     */
    private void finishFromActivity(Shopper shopper, final Intent resultIntent, BlueSnapHTTPResponse response) {
        try {
            String Last4;
            String ccType;
            SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

            if (shopper.getNewCreditCardInfo().getCreditCard().getIsNewCreditCard()) {
                // New Card
                JSONObject jsonObject = new JSONObject(response.getResponseString());
                Last4 = jsonObject.getString("last4Digits");
                ccType = jsonObject.getString("ccType");
                Log.d(TAG, "tokenization of new credit card");
            } else {
                // Reused Card
                Last4 = shopper.getNewCreditCardInfo().getCreditCard().getCardLastFourDigits();
                ccType = shopper.getNewCreditCardInfo().getCreditCard().getCardType();
                Log.d(TAG, "tokenization of previous used credit card");
            }

            sdkResult.setBillingContactInfo(shopper.getNewCreditCardInfo().getBillingContactInfo());
            if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                sdkResult.setShippingContactInfo(shopper.getShippingContactInfo());
            sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
            sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
            // update last4 from server result
            sdkResult.setLast4Digits(Last4);
            // update card type from server result
            sdkResult.setCardType(ccType);
            sdkResult.setChosenPaymentMethodType(SupportedPaymentMethods.CC);
            sdkResult.setThreeDSAuthenticationResult(CardinalManager.getInstance().getThreeDSAuthResult());

            resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, sdkResult);
            setResult(RESULT_OK, resultIntent);
            //Only set the remember shopper here since failure can lead to missing tokenization on the server
            shopper.getNewCreditCardInfo().getCreditCard().setTokenizationSuccess();
            Log.d(TAG, "tokenization finished");
            finish();
        } catch (NullPointerException | JSONException e) {
            finishFromActivityWithFailure();
        }

    }

    /**
     * get current BlueSnap Fragment view
     *
     * @return BlueSnapFragment current view
     */
    private BlueSnapFragment getBlueSnapFragment() {
        return (BlueSnapFragment) getFragmentManager().findFragmentById(R.id.creditCardFrameLayout);
    }

    /**
     * replace BlueSnap Fragment and set to submit if arrived from choose payment method shopper config
     */
    private void replaceBlueSnapFragment(@IdRes int containerViewId, BlueSnapFragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(containerViewId, fragment).commit();
    }

    /**
     * get BlueSnap Fragment Class SimpleName
     *
     * @return BlueSnapFragment().getClass().getSimpleName();
     */
    private String getBlueSnapFragmentClassSimpleName() {
        return getBlueSnapFragment().getClass().getSimpleName();
    }
}
