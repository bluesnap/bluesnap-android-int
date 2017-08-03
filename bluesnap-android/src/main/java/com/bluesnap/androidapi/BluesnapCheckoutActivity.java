package com.bluesnap.androidapi;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bluesnap.androidapi.models.Card;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.PrefsStorage;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.BluesnapFragment;
import com.bluesnap.androidapi.views.CurrencyActivity;
import com.bluesnap.androidapi.views.ExpressCheckoutFragment;
import com.bluesnap.androidapi.views.ShippingFragment;
import com.bluesnap.androidapi.views.WebViewActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;


/**
 * A user Activity to carry out a purchase.
 * A {@link PaymentRequest} must be provided as a parcelableExtra with the name BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST;
 */
public class BluesnapCheckoutActivity extends Activity {
    public final static String EXTRA_PAYMENT_REQUEST = "com.bluesnap.intent.BSNAP_PAYMENT_REQUEST";
    public static final String EXTRA_PAYMENT_RESULT = "com.bluesnap.intent.BSNAP_PAYMENT_RESULT";
    public final static String MERCHANT_TOKEN = "com.bluesnap.intent.BSNAP_CLIENT_PRIVATE_KEY";
    public static final String EXTRA_SHIPPING_DETAILS = "com.bluesnap.intent.BSNAP_SHIPPING_DETAILS";
    public static final String SDK_ERROR_MSG = "SDK_ERROR_MESSAGE";
    public static final int REQUEST_CODE_DEFAULT = 1;
    private static final String TAG = BluesnapCheckoutActivity.class.getSimpleName();
    private static final int RESULT_SDK_FAILED = -2;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private BluesnapFragment bluesnapFragment;
    private PrefsStorage prefsStorage;
    private FragmentManager fragmentManager;
    private PaymentRequest paymentRequest;
    private ExpressCheckoutFragment expressCheckoutFragment;
    private String sharedCurrency;
    private ShippingInfo shippingInfo;
    private Card card;
    private ShippingFragment shippingFragment;
    private Intent resultIntent;
    private boolean rememberShopper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluesnap_default_ui);
        paymentRequest = getIntent().getParcelableExtra(EXTRA_PAYMENT_REQUEST);
        BlueSnapService.getInstance().setPaymentRequest(paymentRequest);
        setMerchantCustomText();
        fragmentManager = getFragmentManager();
        bluesnapFragment = (BluesnapFragment) fragmentManager.findFragmentById(R.id.fraglyout);
        expressCheckoutFragment = ExpressCheckoutFragment.newInstance(BluesnapCheckoutActivity.this, new Bundle());
        prefsStorage = new PrefsStorage(this);
        final ImageButton hamburgerMenuButton = (ImageButton) findViewById(R.id.hamburger_button);
        sharedCurrency = paymentRequest.getCurrencyNameCode();
        setFragmentButtonsListeners();
        hamburgerMenuButton.setOnClickListener(new hamburgerMenuListener(hamburgerMenuButton));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!paymentRequest.verify()) {
            String errorMsg = "payment request not validated";
            Log.e(TAG, errorMsg);
            setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
            finish();
        }
    }

    private void setFragmentButtonsListeners() {
        final Button expressCheckoutButton = (Button) findViewById(R.id.expressCheckoutButton);
        final Button creditCardButton = (Button) findViewById(R.id.creditCardButton);
        final Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable(EXTRA_PAYMENT_REQUEST, paymentRequest);
        expressCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (expressCheckoutFragment == null)
                    expressCheckoutFragment = ExpressCheckoutFragment.newInstance(BluesnapCheckoutActivity.this, fragmentBundle);

                expressCheckoutFragment.setUserVisibleHint(true);
                fragmentTransaction.replace(R.id.fraglyout, expressCheckoutFragment, ExpressCheckoutFragment.TAG);
                fragmentTransaction.commit();
                expressCheckoutButton.setBackgroundResource(R.drawable.bg_tab_expresscheckout_sel);
                creditCardButton.setBackgroundResource(R.drawable.bg_tab_creditcard);
                expressCheckoutButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPureWhite));
                creditCardButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBluesnapBlue));
                Button buyNowButton = (Button) findViewById(R.id.buyNowButton);
                buyNowButton.setVisibility(View.INVISIBLE);
            }
        });

        creditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fraglyout, bluesnapFragment, "BluesnapFragment");
                fragmentTransaction.commit();
                expressCheckoutButton.setBackgroundResource(R.drawable.bg_tab_expresscheckout);
                creditCardButton.setBackgroundResource(R.drawable.bg_tab_creditcard_sel);
                creditCardButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPureWhite));
                expressCheckoutButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBluesnapBlue));
                Button buyNowButton = (Button) findViewById(R.id.buyNowButton);
                buyNowButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setMerchantCustomText() {
        TextView title = (TextView) findViewById(R.id.merchant_title);
        title.setText(paymentRequest.getCustomTitle());
    }

    public ShippingFragment createShippingFragment(Bundle bundle) {
        if (shippingFragment == null) {
            // Create a new fragment.
            shippingFragment = new ShippingFragment();
            shippingFragment.setArguments(bundle);
            BlueSnapService.getBus().register(shippingFragment);
        }
        return shippingFragment;
    }

    public void finishFromShippingFragment(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
        finishFromFragment();
    }

    public void finishFromFragment() {
        Intent resultIntent = new Intent();
        boolean rememberShopper = prefsStorage.getBoolean(Constants.REMEMBER_SHOPPER);
        resultIntent.putExtra(EXTRA_SHIPPING_DETAILS, shippingInfo);

        if (rememberShopper) {
            if (shippingInfo != null)
                prefsStorage.putObject(Constants.SHIPPING_INFO, shippingInfo);
        } else {
            prefsStorage.remove(Constants.SHIPPING_INFO);
            prefsStorage.remove(Constants.RETURNING_SHOPPER);
        }
        Log.d(TAG, "Testing if card requires server tokenization:" + card.toString());
        if (!card.isModified()) {
            PaymentResult paymentResult = BlueSnapService.getInstance().getPaymentResult();
            paymentResult.setLast4Digits(card.getLast4());
            paymentResult.setCardType(card.getType());
            paymentResult.setExpDate(card.getExpDate());
            paymentResult.setCardZipCode(card.getAddressZip());
            paymentResult.setAmount(paymentRequest.getAmount());
            paymentResult.setCurrencyNameCode(paymentRequest.getCurrencyNameCode());
            paymentResult.setReturningTransaction(true);
            prefsStorage.putObject(Constants.RETURNING_SHOPPER, card);
            prefsStorage.putBoolean(Constants.REMEMBER_SHOPPER, rememberShopper);
            resultIntent.putExtra(EXTRA_PAYMENT_RESULT, paymentResult);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            try {
                tokenizeCardOnServer(resultIntent, rememberShopper);
            } catch (UnsupportedEncodingException | JSONException e) {
                String errorMsg = "SDK service error";
                Log.e(TAG, errorMsg, e);
                setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
                finish();
            }
        }
    }

    private void tokenizeCardOnServer(final Intent resultIntent, final boolean rememberShopper) throws UnsupportedEncodingException, JSONException {
        this.rememberShopper = rememberShopper;
        this.resultIntent = resultIntent;

        blueSnapService.tokenizeCard(card, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String Last4 = response.getString("last4Digits");
                    String ccType = response.getString("ccType");
                    PaymentResult paymentResult = BlueSnapService.getInstance().getPaymentResult();
                    // update last4 from server result
                    paymentResult.setLast4Digits(Last4);
                    // update card type from server result
                    paymentResult.setCardType(ccType);
                    paymentResult.setReturningTransaction(false);
                    resultIntent.putExtra(EXTRA_PAYMENT_RESULT, paymentResult);
                    setResult(RESULT_OK, resultIntent);
                    //Only set the remember shopper here since failure can lead to missing tokenization on the server
                    card.setTokenizationSucess();
                    Log.d(TAG, "tokenization finished");
                    if (rememberShopper)
                        prefsStorage.putObject(Constants.RETURNING_SHOPPER, card);

                    prefsStorage.putBoolean(Constants.REMEMBER_SHOPPER, rememberShopper);
                    finish();
                } catch (NullPointerException | JSONException e) {
                    Log.e(TAG, "", e);
                    String errorMsg = String.format("Service Error %s", e.getMessage());
                    setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));   //TODO Display error to the user
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // check if failure is EXPIRED_TOKEN if so activating the create new token mechanism.
                if (statusCode == 400) {
                    try {
                        JSONArray rs2 = (JSONArray) errorResponse.get("message");
                        JSONObject rs3 = (JSONObject) rs2.get(0);
                        if ("EXPIRED_TOKEN".equals(rs3.get("errorName")))
                            blueSnapService.getTokenInterface().getNewToken(
                                    new TokenServiceCallback() {
                                        @Override
                                        public void complete(String newToken) {
                                            blueSnapService.setNewToken(newToken);
                                            try {
                                                tokenizeCardOnServer(resultIntent, rememberShopper);
                                            } catch (UnsupportedEncodingException e) {
                                                Log.e(TAG, "Unsupported Encoding Exception", e);
                                            } catch (JSONException e) {
                                                Log.e(TAG, "json parsing exception", e);
                                            }
                                        }
                                    }
                            );
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                    }
                } else {
                    String errorMsg = String.format("Service Error %s, %s", statusCode);
                    Log.e(TAG, errorMsg, throwable);
                    setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String errorMsg = String.format("Service Error %s, %s", statusCode, responseString);
                Log.e(TAG, errorMsg, throwable);
                setResult(RESULT_SDK_FAILED, new Intent().putExtra(SDK_ERROR_MSG, errorMsg));
                finish();
            }
        });

    }


    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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
        } else if (i == R.id.id_privacy_policy) {
            newIntent = new Intent(getApplicationContext(), WebViewActivity.class);
            newIntent.putExtra(getString(R.string.WEBVIEW_STRING), getResources().getString(R.string.privacy_policy));
            newIntent.putExtra(getString(R.string.WEBVIEW_URL), getResources().getString(R.string.privacy_policy_url));
            startActivity(newIntent);
            return true;
        } else if (i == R.id.id_refund_policy) {
            newIntent = new Intent(getApplicationContext(), WebViewActivity.class);
            newIntent.putExtra(getString(R.string.WEBVIEW_STRING), getResources().getString(R.string.refund_policy));
            newIntent.putExtra(getString(R.string.WEBVIEW_URL), getResources().getString(R.string.refund_policy_url));
            startActivity(newIntent);
            return true;
        } else if (i == R.id.id_terms_conditions) {
            newIntent = new Intent(getApplicationContext(), WebViewActivity.class);
            newIntent.putExtra(getString(R.string.WEBVIEW_STRING), getResources().getString(R.string.terms_conditions));
            newIntent.putExtra(getString(R.string.WEBVIEW_URL), getResources().getString(R.string.terms_conditions_url));
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
            sharedCurrency = paymentRequest.getCurrencyNameCode();
            invalidateOptionsMenu();
            hamburgerMenuButton.setImageResource(R.drawable.ic_close_white_36dp);
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    hamburgerMenuButton.setImageResource(R.drawable.ic_menu_white_36dp);
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
}

