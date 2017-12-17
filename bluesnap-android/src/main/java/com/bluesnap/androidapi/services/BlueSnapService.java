package com.bluesnap.androidapi.services;

import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.Rates;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Core BlueSnap Service class that handles network and maintains {@link SdkRequest}
 */
public class BlueSnapService {
    private static final String TAG = BlueSnapService.class.getSimpleName();
    private static final BlueSnapService INSTANCE = new BlueSnapService();
    private final BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private final KountService kountService = KountService.getInstance();

    private static final String FRAUDSESSIONID = "fraudSessionId";

    private static final EventBus busInstance = new EventBus();
    private static String paypalURL;
    private static JSONObject errorDescription;
    private static String transactionStatus;

    private SdkResult sdkResult;
    private SdkRequest sdkRequest;
    private BluesnapToken bluesnapToken;
    private TokenServiceCallback checkoutActivity;
    private BluesnapServiceCallback bluesnapServiceCallback;

    public SDKConfiguration getsDKConfiguration() {
        return sDKConfiguration;
    }

    private SDKConfiguration sDKConfiguration;
    private String merchantStoreCurrency;
    private TokenProvider tokenProvider;

    public static BlueSnapService getInstance() {
        return INSTANCE;
    }

    public static String getPayPalToken() {
        return paypalURL;
    }

    public static JSONObject getErrorDescription() {
        return errorDescription;
    }

    public static EventBus getBus() {
        return busInstance;
    }


    public boolean isexpressCheckoutActive() {
        return sDKConfiguration.getSupportedPaymentMethods().isPaymentMethodActive(SupportedPaymentMethods.PAYPAL);
    }

    public TokenProvider getTokenProvider() {
        return tokenProvider;
    }

    public void clearPayPalToken() {
        paypalURL = "";
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * Setup the service to talk to the server.
     * This will reset the previous payment request
     *
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     * @param tokenProvider A merchant function for requesting a new token if expired
     *                      merchantStoreCurrency = USD
     * @param context       A Merchant Application Context
     * @param callback      A {@link BluesnapServiceCallback}
     */
    public void setup(String merchantToken, TokenProvider tokenProvider, Context context, final BluesnapServiceCallback callback) {
        setup(merchantToken, tokenProvider, SupportedPaymentMethods.USD, context, callback);
    }

    /**
     * Setup the service to talk to the server.
     * This will reset the previous payment request
     *
     * @param merchantToken         A Merchant SDK token, obtained from the merchant.
     * @param tokenProvider         A merchant function for requesting a new token if expired
     * @param merchantStoreCurrency A Merchant base currency, obtained from the merchant.
     * @param context               A Merchant Application Context
     * @param callback              A {@link BluesnapServiceCallback}
     */
    public void setup(String merchantToken, TokenProvider tokenProvider, String merchantStoreCurrency, final Context context, final BluesnapServiceCallback callback) {
        this.bluesnapServiceCallback = callback;
        this.merchantStoreCurrency = merchantStoreCurrency;
        if (null != tokenProvider)
            this.tokenProvider = tokenProvider;

        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);

        blueSnapAPI.setupMerchantToken(bluesnapToken.getMerchantToken(), bluesnapToken.getUrl());

        sdkResult = null;
        sdkRequest = null;

        clearPayPalToken();
        sdkInit(merchantStoreCurrency, context, callback);

        if (!busInstance.isRegistered(this)) busInstance.register(this);
        Log.d(TAG, "Service setup with token" + merchantToken.substring(merchantToken.length() - 5, merchantToken.length()));
    }

    private void initPayPal(String merchantToken) {
        // check if paypal url is same as before
        if (!merchantToken.equals(bluesnapToken.getMerchantToken()) && !"".equals(getPayPalToken())) {
            Log.d(TAG, "clearPayPalToken");
            clearPayPalToken();
        } else {
            Log.d(TAG, "PayPal token reuse");
        }
    }

    /**
     * Change the token after expiration occurred.
     *
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     */
    protected void changeExpiredToken(String merchantToken) {
        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);
        bluesnapToken.setToken(merchantToken);
        initPayPal(merchantToken);
        blueSnapAPI.setupMerchantToken(bluesnapToken.getMerchantToken(), bluesnapToken.getUrl());
        // after expired token is replaced - placing new token in payment result
        if (null != sdkResult)
            sdkResult.setToken(merchantToken);
        Log.d(TAG, "Service change with token" + merchantToken.substring(merchantToken.length() - 5, merchantToken.length()));

    }

    public void setNewToken(String newToken) {
        changeExpiredToken(newToken);
    }

    /**
     * Update details on the BlueSnap Server
     *
     * @param shopper         {@link Shopper}
     * @param responseHandler {@link AsyncHttpResponseHandler}
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    public void tokenizeCard(Shopper shopper, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        blueSnapAPI.tokenizeCard(createDataObject(shopper), responseHandler);
    }

    private JSONObject createDataObject(Shopper shopper) throws JSONException {
        CreditCard creditCard = shopper.getNewCreditCardInfo().getCreditCard();
        BillingInfo billingInfo = shopper.getNewCreditCardInfo().getBillingContactInfo();
        JSONObject postData = new JSONObject();

        if (creditCard.getIsNewCreditCard()) {
            postData.put(CreditCard.CCNUMBER, creditCard.getNumber());
            postData.put(CreditCard.CVV, creditCard.getCvc());
            postData.put(CreditCard.EXPDATE, creditCard.getExpirationDate());
        } else {
            postData.put(CreditCard.CARDTYPE, creditCard.getCardType());
            postData.put(CreditCard.LAST4DIGITS, creditCard.getCardLastFourDigits());

        }

        postData.put(BillingInfo.BILLINGFIRSTNAME, billingInfo.getFirstName());
        postData.put(BillingInfo.BILLINGLASTNAME, billingInfo.getLastName());
        postData.put(BillingInfo.BILLINGCOUNTRY, billingInfo.getCountry());

        if (null != billingInfo.getZip() && !"".equals(billingInfo.getZip()))
            postData.put(BillingInfo.BILLINGZIP, billingInfo.getZip());

        if (sdkRequest.isBillingRequired()) {
            postData.put(BillingInfo.BILLINGSTATE, billingInfo.getState());
            postData.put(BillingInfo.BILLINGCITY, billingInfo.getCity());
            postData.put(BillingInfo.BILLINGADDRESS, billingInfo.getAddress());
        }

        if (sdkRequest.isEmailRequired())
            postData.put(BillingInfo.EMAIL, billingInfo.getEmail());

        //postData.put(PHONE, creditCardInfo.getBillingContactInfo().getPhone());

        if (sdkRequest.isShippingRequired()) {
            ShippingInfo shippingInfo = shopper.getShippingContactInfo();
            assert shippingInfo != null;
            postData.put(ShippingInfo.SHIPPINGFIRSTNAME, shippingInfo.getFirstName());
            postData.put(ShippingInfo.SHIPPINGLASTNAME, shippingInfo.getLastName());
            postData.put(ShippingInfo.SHIPPINGCOUNTRY, shippingInfo.getCountry());
            postData.put(ShippingInfo.SHIPPINGSTATE, shippingInfo.getState());
            postData.put(ShippingInfo.SHIPPINGCITY, shippingInfo.getCity());
            postData.put(ShippingInfo.SHIPPINGADDRESS, shippingInfo.getAddress());
            postData.put(ShippingInfo.SHIPPINGZIP, shippingInfo.getZip());
        }

        if (null != kountService.getKountSessionId()) {
            String fraudSessionId = kountService.getKountSessionId();
            postData.put(FRAUDSESSIONID, fraudSessionId);
        }

        return postData;
    }

    /**
     * Check if Token is Expired on the BlueSnap Server
     *
     * @param responseHandler {@link AsyncHttpResponseHandler}
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    private void checkTokenIsExpired(AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Check if Token is Expired" + bluesnapToken.toString());
        blueSnapAPI.tokenizeCard(new JSONObject(), responseHandler);
    }

    private interface AfterNewTokenCreatedAction {
        void complete();
    }

    private void tokenExpiredAction(final BluesnapServiceCallback callback, final AfterNewTokenCreatedAction afterNewTokenCreatedAction) {
        // try to PUT empty {} to check if token is expired
        try {
            checkTokenIsExpired(new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.e(TAG, "SDK Init service error, checkTokenIsExpired successful");
                    callback.onFailure();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // check if failure is EXPIRED_TOKEN if so activating the create new token mechanism.
                    if (statusCode == 400 && null != getTokenProvider() && !"".equals(responseString)) {
                        try {
                            JSONObject errorResponse = new JSONObject(responseString);
                            JSONArray rs2 = (JSONArray) errorResponse.get("message");
                            JSONObject rs3 = (JSONObject) rs2.get(0);
                            if ("EXPIRED_TOKEN".equals(rs3.get("errorName")))
                                getTokenProvider().getNewToken(
                                        new TokenServiceCallback() {
                                            @Override
                                            public void complete(String newToken) {
                                                setNewToken(newToken);
                                                afterNewTokenCreatedAction.complete();
                                            }
                                        }
                                );
                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing exception", e);
                        }
                    } else {
                        String errorMsg = String.format("Service Error %s, %s", statusCode, responseString);
                        Log.e(TAG, errorMsg, throwable);
                        callback.onFailure();
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "json parsing exception", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unsupported Encoding Exception", e);
        }
    }

    /**
     * SDK Init.
     *
     * @param merchantStoreCurrency All rates are derived from merchantStoreCurrency. merchantStoreCurrency * AnyRate = AnyCurrency
     */
    private void sdkInit(final String merchantStoreCurrency, final Context context, final BluesnapServiceCallback callback) {
        blueSnapAPI.sdkInit(merchantStoreCurrency, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    sDKConfiguration = new Gson().fromJson(String.valueOf(response), SDKConfiguration.class);
                    sDKConfiguration.getRates().setInitialRates();

                    try {
                        if (null != context)
                            kountService.setupKount(sDKConfiguration.getKountMerchantId(), context, getBlueSnapToken().isProduction());
                    } catch (Exception e) {
                        Log.e(TAG, "Kount SDK initialization error");
                    }

                    callback.onSuccess();
                } catch (Exception e) {
                    Log.e(TAG, "exception: ", e);
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "SDK Init service error", throwable);
                tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                    @Override
                    public void complete() {
                        sdkInit(merchantStoreCurrency, context, bluesnapServiceCallback);
                    }
                });
            }
        });
    }

    public ArrayList<Currency> getRatesArray() {
        return sDKConfiguration.getRates().getCurrencies();
    }

    public void createPayPalToken(final Double amount, final String currency, final BluesnapServiceCallback callback) {
        blueSnapAPI.createPayPalToken(amount, currency, sdkRequest.isShippingRequired(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    paypalURL = response.getString("paypalUrl");
                    callback.onSuccess();
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                    callback.onFailure();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                errorDescription = new JSONObject();
                try {
                    JSONArray errorResponseJSONArray = errorResponse.getJSONArray("message");
                    JSONObject errorJson = errorResponseJSONArray.getJSONObject(0);
                    errorDescription = errorJson;
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                }
                Log.e(TAG, "PayPal service error", throwable);
                callback.onFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                errorDescription = new JSONObject();
                try {
                    errorDescription.put("errorName", responseString.replaceAll("\"", "").toUpperCase());
                    errorDescription.put("code", statusCode);
                    errorDescription.put("description", responseString.replaceAll("\"", ""));
                    Log.e(TAG, "PayPal service error", throwable);
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                }

                tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                    @Override
                    public void complete() {
                        createPayPalToken(amount, currency, callback);
                    }
                });
            }
        });
    }

    public void retrieveTransactionStatus(final BluesnapServiceCallback callback) {
        blueSnapAPI.retrieveTransactionStatus(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    transactionStatus = response.getString("processingStatus");
                    callback.onSuccess();
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "PayPal service error", throwable);
                callback.onFailure();
            }
        });
    }

    /**
     * Get a set of strings of the supported conversion rates
     *
     * @return {@link Set<String>}
     */
    @Nullable
    public Set<String> getSupportedRates() {
        if (sDKConfiguration.getRates().getRatesMap() != null)
            return sDKConfiguration.getRates().getRatesMap().keySet();
        else return null;
    }

    /**
     * check currency received from merchant and verify it actually exists
     *
     * @param currencyNameCode ISO 4217 compatible  3 letter currency representation
     * @return boolean
     */
    public boolean checkCurrencyCompatibility(String currencyNameCode) {
        return (null != getSupportedRates() && getSupportedRates().contains(currencyNameCode));
    }

    /**
     * Convert a price in USD to a price in another currency  in ISO 4217 Code.
     * Before
     *
     * @param usdPrice  A String representation of a USD price which will be converted to a double value.
     * @param convertTo ISO 4217 compatible  3 letter currency representation
     * @return String representation of converted price.
     */
    public String convertUSD(String usdPrice, String convertTo) {
        if (usdPrice == null || usdPrice.isEmpty())
            return "0";

        Double result = convertPrice(Double.valueOf(usdPrice), SupportedPaymentMethods.USD, convertTo);
        return String.valueOf(AndroidUtil.getDecimalFormat().format(result));
    }

    /**
     * Convert a price in currentCurrencyNameCode to newCurrencyNameCode
     *
     * @param currentPrice            the requested price
     * @param currentCurrencyNameCode The currency of basePrice
     * @param newCurrencyNameCode     The ISO 4217 currency name
     * @return
     */
    public Double convertPrice(Double currentPrice, String currentCurrencyNameCode, String newCurrencyNameCode) {
        if (!checkCurrencyCompatibility(currentCurrencyNameCode) && !checkCurrencyCompatibility(newCurrencyNameCode))
            throw new IllegalArgumentException("not an ISO 4217 compatible 3 letter currency representation");

        // get Rates
        Rates rates = sDKConfiguration.getRates();
        // check if currentCurrencyNameCode is MerchantStoreCurrency
        Double currentRate = rates.getRatesMap().get(currentCurrencyNameCode).getConversionRate();
        Double newRate = rates.getRatesMap().get(newCurrencyNameCode).getConversionRate();

        if (!currentCurrencyNameCode.equals(rates.getMerchantStoreCurrency())) {
            currentPrice = (1 / currentRate) * currentPrice;
            currentCurrencyNameCode = rates.getMerchantStoreCurrency();
        }
        return (newRate) * currentPrice;
    }

    public synchronized SdkResult getSdkResult() {
        if (sdkResult == null) {
            sdkResult = new SdkResult();
        }

        try {
            sdkResult.setToken(bluesnapToken.getMerchantToken());
            // Copy values from request
            sdkResult.setAmount(sdkRequest.getAmount());
            sdkResult.setCurrencyNameCode(sdkRequest.getCurrencyNameCode());
        } catch (Exception e) {
            Log.e(TAG, "sdkResult set Token, Amount, Currency or ShopperId resulted in an error");
        }
        return sdkResult;
    }

    public SdkRequest getSdkRequest() {
        return sdkRequest;
    }

    /**
     * Set a sdkRequest and call {@link #verifyPaymentRequest} on  it.
     *
     * @param newSdkRequest
     * @throws BSPaymentRequestException
     */
    public void setSdkRequest(SdkRequest newSdkRequest) throws BSPaymentRequestException {
        if (newSdkRequest == null)
            throw new BSPaymentRequestException("null sdkRequest");

        if (sdkRequest != null) {
            Log.w(TAG, "sdkRequest override");
        }
        sdkRequest = newSdkRequest;
        sdkResult = new SdkResult();
        // Copy values from request
        sdkResult.setAmount(sdkRequest.getAmount());
        sdkResult.setCurrencyNameCode(sdkRequest.getCurrencyNameCode());
        sdkResult.setShopperID(sdkRequest.getShopperID());
    }


    @Subscribe
    public synchronized void onCurrencyChange(Events.CurrencySelectionEvent currencySelectionEvent) {
        String baseCurrency = sdkRequest.getBaseCurrency();
        if (currencySelectionEvent.newCurrencyNameCode.equals(baseCurrency)) {
            sdkRequest.setAmount(sdkRequest.getBaseAmount());
            sdkRequest.setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);
            sdkRequest.setSubtotalAmount(sdkRequest.getBaseSubtotalAmount());
            sdkRequest.setTaxAmount(sdkRequest.getBaseTaxAmount());
            busInstance.post(new Events.CurrencyUpdatedEvent(sdkRequest.getBaseAmount(),
                    currencySelectionEvent.newCurrencyNameCode,
                    sdkRequest.getBaseTaxAmount(),
                    sdkRequest.getBaseSubtotalAmount()));
        } else {
            Double newPrice = convertPrice(sdkRequest.getBaseAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);

            sdkRequest.setAmount(newPrice);
            sdkRequest.setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);
            getSdkResult().setAmount(newPrice);
            getSdkResult().setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);

            Double newTaxValue = convertPrice(sdkRequest.getBaseTaxAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);
            Double newSubtotal = convertPrice(sdkRequest.getBaseSubtotalAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);
            if (sdkRequest.isSubtotalTaxSet()) {
                sdkRequest.setSubtotalAmount(newSubtotal);
                sdkRequest.setTaxAmount(newTaxValue);
            }
            busInstance.post(new Events.CurrencyUpdatedEvent(newPrice, currencySelectionEvent.newCurrencyNameCode, newTaxValue, newSubtotal));
        }
        sdkResult.setAmount(sdkRequest.getAmount());
        sdkResult.setCurrencyNameCode(sdkRequest.getCurrencyNameCode());
        sdkResult.setShopperID(sdkRequest.getShopperID());


    }

    public void setCheckoutActivity(TokenServiceCallback checkoutActivity) {
        this.checkoutActivity = checkoutActivity;
    }

    public BluesnapToken getBlueSnapToken() {
        return bluesnapToken;
    }

    public boolean doesCountryhaveZip(Context context) {
        return (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getUserCountry(context)));
    }

    public String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                return simCountry.toUpperCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "TelephonyManager, getSimCountryIso or getNetworkCountryIso failed");
        }

        return Locale.US.getCountry();
    }
}