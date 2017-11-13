package com.bluesnap.androidapi.services;

import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bluesnap.androidapi.BuildConfig;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.CreditCardTypes;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

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
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Core BlueSnap Service class that handles network and maintains {@link PaymentRequest}
 */
public class BlueSnapService {
    public static final String TOKEN_AUTHENTICATION = "Token-Authentication";
    private static final double BLUESNAP_VERSION_HEADER = 2.0;
    private static final String TAG = BlueSnapService.class.getSimpleName();
    private static final String CARD_TOKENIZE = "payment-fields-tokens/";
    private static final String RATES_SERVICE = "tokenized-services/rates";
    private static final String BASE_CURRENCY = "?base-currency=";
    private static final BlueSnapService INSTANCE = new BlueSnapService();
    private static final String SUPPORTED_PAYMENT_METHODS = "tokenized-services/supported-payment-methods";
    private static final String SDK_INIT = "tokenized-services/sdk-init";
    private static final String PAYPAL_SERVICE = "tokenized-services/paypal-token?amount=";
    private static final String PAYPAL_SHIPPING = "&req-confirm-shipping=0&no-shipping=2";
    private static final String RETRIEVE_TRANSACTION_SERVICE = "tokenized-services/transaction-status";

    private static final String CCNUMBER = "ccNumber";
    private static final String CVV = "cvv";
    private static final String EXPDATE = "expDate";
    private static final String BILLINGFIRSTNAME = "billingFirstName";
    private static final String BILLINGLASTNAME = "billingLastName";
    private static final String BILLINGCOUNTRY = "billingCountry";
    private static final String BILLINGSTATE = "billingState";
    private static final String BILLINGCITY = "billingCity";
    private static final String BILLINGADDRESS = "billingAddress";
    private static final String BILLINGZIP = "billingZip";
    private static final String SHIPPINGFIRSTNAME = "shippingFirstName";
    private static final String SHIPPINGLASTNAME = "shippingLastName";
    private static final String SHIPPINGCOUNTRY = "shippingCountry";
    private static final String SHIPPINGSTATE = "shippingState";
    private static final String SHIPPINGCITY = "shippingCity";
    private static final String SHIPPINGADDRESS = "shippingAddress";
    private static final String SHIPPINGZIP = "shippingZip";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String FRAUDSESSIONID = "fraudSessionId";

    private static final EventBus busInstance = new EventBus();
    private static String paypalURL;
    private static JSONObject errorDescription;
    private static String transactionStatus;
    private final AsyncHttpClient httpClient = new AsyncHttpClient();

    private PaymentResult paymentResult;
    private PaymentRequest paymentRequest;
    private BluesnapToken bluesnapToken;
    private TokenServiceCallback checkoutActivity;
    private BluesnapServiceCallback bluesnapServiceCallback;

    public SDKConfiguration getsDKConfiguration() {
        return sDKConfiguration;
    }

    private SDKConfiguration sDKConfiguration;
    private String baseCurrency;
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
     * @param tokenProvider A merchant function for requesting a new token if expired
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     *                      baseCurrency = USD
     */
    public void setup(String merchantToken, TokenProvider tokenProvider, final BluesnapServiceCallback callback) {
        setup(merchantToken, tokenProvider, SupportedPaymentMethods.USD, callback);
    }

    /**
     * Setup the service to talk to the server.
     * This will reset the previous payment request
     *
     * @param tokenProvider A merchant function for requesting a new token if expired
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     * @param baseCurrency  A Merchant base currency, obtained from the merchant.
     */
    public void setup(String merchantToken, TokenProvider tokenProvider, String baseCurrency, final BluesnapServiceCallback callback) {
        this.bluesnapServiceCallback = callback;
        this.baseCurrency = baseCurrency;
        if (null != tokenProvider)
            this.tokenProvider = tokenProvider;

        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);

        // check if paypal url is same as before
        if (!merchantToken.equals(bluesnapToken.getMerchantToken()) && null != getPayPalToken() && !"".equals(getPayPalToken())) {
            Log.d(TAG, "clearPayPalToken");
            clearPayPalToken();
        } else {
            Log.d(TAG, "PayPal token reuse");
        }

        clearPayPalToken();
        setupHttpClient();

        paymentResult = null;
        paymentRequest = null;

        sdkInit(baseCurrency, callback);

        if (!busInstance.isRegistered(this)) busInstance.register(this);
        Log.d(TAG, "Service setup with token" + merchantToken.substring(merchantToken.length() - 5, merchantToken.length()));
    }

    /**
     * Change the token after expiration occurred.
     *
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     */
    protected void changeExpiredToken(String merchantToken) {
        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);
        bluesnapToken.setToken(merchantToken);
        clearPayPalToken();
        // after expired token is replaced - placing new token in payment result
        if (null != paymentResult)
            paymentResult.setToken(merchantToken);
        Log.d(TAG, "Service change with token" + merchantToken.substring(merchantToken.length() - 5, merchantToken.length()));

    }

    public void setNewToken(String newToken) {
        changeExpiredToken(newToken);
    }


    private void setupHttpClient() {
        httpClient.setMaxRetriesAndTimeout(2, 2000);
        httpClient.setResponseTimeout(60000);
        httpClient.setConnectTimeout(20000);
        httpClient.addHeader("ANDROID_SDK_VERSION_NAME", BuildConfig.VERSION_NAME);
        httpClient.addHeader("ANDROID_SDK_VERSION_CODE", String.valueOf(BuildConfig.VERSION_CODE));
        httpClient.addHeader("BLUESNAP_VERSION_HEADER", String.valueOf(BLUESNAP_VERSION_HEADER));
    }

    /**
     * Update details on the BlueSnap Server
     *
     * @param shopper  {@link Shopper}
     * @param fraudSessionId    {@link String}
     * @param responseHandler {@link AsyncHttpResponseHandler}
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    public void tokenizeCard(Shopper shopper, String fraudSessionId, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        CreditCard creditCard = shopper.getCreditCardInfo().getCreditCard();
        BillingInfo billingInfo = shopper.getCreditCardInfo().getBillingContactInfo();
        //TODO: add full billing, email and optional shipping
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        JSONObject postData = new JSONObject();
        postData.put(CCNUMBER, creditCard.getNumber());
        postData.put(CVV, creditCard.getCvc());
        postData.put(EXPDATE, creditCard.getExpirationDate());

        postData.put(BILLINGFIRSTNAME, billingInfo.getFirstName());
        postData.put(BILLINGLASTNAME, billingInfo.getLastName());
        postData.put(BILLINGCOUNTRY, billingInfo.getCountry());

        if (null != billingInfo.getZip() && !"".equals(billingInfo.getZip()))
            postData.put(BILLINGZIP, billingInfo.getZip());

        if (paymentRequest.isBillingRequired()) {
            postData.put(BILLINGSTATE, billingInfo.getState());
            postData.put(BILLINGCITY, billingInfo.getCity());
            postData.put(BILLINGADDRESS, billingInfo.getAddress());
        }

        postData.put(FRAUDSESSIONID, fraudSessionId);

        if (paymentRequest.isEmailRequired())
            postData.put(EMAIL, billingInfo.getEmail());

        //postData.put(PHONE, creditCardInfo.getBillingContactInfo().getPhone());

        if (paymentRequest.isShippingRequired()) {
            ShippingInfo shippingInfo= shopper.getShippingContactInfo();
            postData.put(SHIPPINGFIRSTNAME, shippingInfo.getFirstName());
            postData.put(SHIPPINGLASTNAME, shippingInfo.getLastName());
            postData.put(SHIPPINGCOUNTRY, shippingInfo.getCountry());
            postData.put(SHIPPINGSTATE, shippingInfo.getState());
            postData.put(SHIPPINGCITY, shippingInfo.getCity());
            postData.put(SHIPPINGADDRESS, shippingInfo.getAddress());
            postData.put(SHIPPINGZIP, shippingInfo.getZip());
        }

        ByteArrayEntity entity = new ByteArrayEntity(postData.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpClient.put(null, bluesnapToken.getUrl() + CARD_TOKENIZE + bluesnapToken.getMerchantToken(), entity, "application/json", responseHandler);
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
        JSONObject postData = new JSONObject();
        ByteArrayEntity entity = new ByteArrayEntity(postData.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpClient.put(null, bluesnapToken.getUrl() + CARD_TOKENIZE + bluesnapToken.getMerchantToken(), entity, "application/json", responseHandler);
    }

    /**
     * SDK Init.
     * baseCurrency = USD
     */
    public void sdkInit(final BluesnapServiceCallback callback) {
        sdkInit(SupportedPaymentMethods.USD, callback);
    }

    /**
     * SDK Init.
     *
     * @param baseCurrency All rates are derived from baseCurrency. baseCurrency * AnyRate = AnyCurrency
     */
    protected void sdkInit(final String baseCurrency, final BluesnapServiceCallback callback) {
        httpClient.addHeader(TOKEN_AUTHENTICATION, bluesnapToken.getMerchantToken());
        httpClient.get(bluesnapToken.getUrl() + SDK_INIT + BASE_CURRENCY + baseCurrency, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    sDKConfiguration = new Gson().fromJson(String.valueOf(response), SDKConfiguration.class);
                    sDKConfiguration.getRates().setInitialRates();
                    // activate the credit card type method finder
                    new CreditCardTypes(sDKConfiguration.getSupportedPaymentMethods().getCreditCardRegex());

                    callback.onSuccess();
                } catch (Exception e) {
                    Log.e(TAG, "exception: ", e);
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "SDK Init service error", throwable);
                // try to PUT empty {} to check if token is expired
                try {
                    checkTokenIsExpired(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.e(TAG, "SDK Init service error, checkTokenIsExpired successful");
                            callback.onFailure();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            // check if failure is EXPIRED_TOKEN if so activating the create new token mechanism.
                            if (statusCode == 400 && null != tokenProvider) {
                                try {
                                    JSONArray rs2 = (JSONArray) errorResponse.get("message");
                                    JSONObject rs3 = (JSONObject) rs2.get(0);
                                    if ("EXPIRED_TOKEN".equals(rs3.get("errorName")))
                                        getTokenProvider().getNewToken(
                                                new TokenServiceCallback() {
                                                    @Override
                                                    public void complete(String newToken) {
                                                        setNewToken(newToken);
                                                        sdkInit(baseCurrency, bluesnapServiceCallback);
                                                    }
                                                }
                                        );
                                } catch (JSONException e) {
                                    Log.e(TAG, "json parsing exception", e);
                                }
                            } else {
                                String errorMsg = String.format("Service Error %s, %s", statusCode);
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
        });
    }

    public ArrayList<Currency> getRatesArray() {
        return sDKConfiguration.getRates().getCurrencies();
    }

    public void createPayPalToken(final Double amount, final String currency, final BluesnapServiceCallback callback) {
        httpClient.addHeader(TOKEN_AUTHENTICATION, bluesnapToken.getMerchantToken());
        httpClient.addHeader("Accept", "application/json");
        String url = bluesnapToken.getUrl() + PAYPAL_SERVICE + amount + "&currency=" + currency;
        if (paymentRequest.isShippingRequired())
            url += PAYPAL_SHIPPING;

        httpClient.get(url, new JsonHttpResponseHandler() {

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

                // try to PUT empty {} to check if token is expired
                try {
                    checkTokenIsExpired(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            callback.onFailure();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            // check if failure is EXPIRED_TOKEN if so activating the create new token mechanism.
                            if (statusCode == 400 && null != tokenProvider) {
                                try {
                                    JSONArray rs2 = (JSONArray) errorResponse.get("message");
                                    JSONObject rs3 = (JSONObject) rs2.get(0);
                                    if ("EXPIRED_TOKEN".equals(rs3.get("errorName")))
                                        getTokenProvider().getNewToken(
                                                new TokenServiceCallback() {
                                                    @Override
                                                    public void complete(String newToken) {
                                                        setNewToken(newToken);
                                                        createPayPalToken(amount, currency, callback);
                                                    }
                                                }
                                        );
                                } catch (JSONException e) {
                                    Log.e(TAG, "json parsing exception", e);
                                }
                            } else {
                                String errorMsg = String.format("Service Error %s, %s", statusCode);
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
        });
    }

    public void retrieveTransactionStatus(final BluesnapServiceCallback callback) {
        httpClient.addHeader("Accept", "application/json");
        httpClient.addHeader(TOKEN_AUTHENTICATION, bluesnapToken.getMerchantToken());
        httpClient.get(bluesnapToken.getUrl() + RETRIEVE_TRANSACTION_SERVICE, new JsonHttpResponseHandler() {

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

        Currency rate = sDKConfiguration.getRates().getRatesMap().get(convertTo);
        Double result = Double.valueOf(usdPrice) * rate.getConversionRate();
        return String.valueOf(AndroidUtil.getDecimalFormat().format(result));
    }

    /**
     * Convert a price in currentCurrencyNameCode to newCurrencyNameCode
     *
     * @param basePrice               the requested price
     * @param currentCurrencyNameCode The currency of basePrice
     * @param newCurrencyNameCode     The ISO 4217 currency name
     * @return
     */
    public Double convertPrice(Double basePrice, String currentCurrencyNameCode, String newCurrencyNameCode) {
        if (!checkCurrencyCompatibility(currentCurrencyNameCode) && !checkCurrencyCompatibility(newCurrencyNameCode))
            throw new IllegalArgumentException("not an ISO 4217 compatible 3 letter currency representation");

        String baseCurrency = paymentRequest.getBaseCurrency();
        if (baseCurrency.equals(newCurrencyNameCode)) {
            return paymentRequest.getBaseAmount();
        }
        Double baseConversionRate = sDKConfiguration.getRates().getRatesMap().get(baseCurrency).getConversionRate();
        Double usdPRice = baseCurrency.equals(SupportedPaymentMethods.USD) ? basePrice * baseConversionRate : basePrice * (1 / baseConversionRate);
        Double newPrice = sDKConfiguration.getRates().getRatesMap().get(newCurrencyNameCode).getConversionRate() * usdPRice;
        return newPrice;
    }

    public synchronized PaymentResult getPaymentResult() {
        if (paymentResult == null) {
            paymentResult = new PaymentResult();

            try {
                paymentResult.setToken(bluesnapToken.getMerchantToken());
                // Copy values from request
                paymentResult.setAmount(paymentRequest.getAmount());
                paymentResult.setCurrencyNameCode(paymentRequest.getCurrencyNameCode());
                paymentResult.setShopperID(paymentRequest.getShopperID());
            } catch (Exception e) {
                Log.e(TAG, "paymentResult set Token, Amount, Currency or ShopperId resulted in an error");
            }
        }
        return paymentResult;
    }

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    /**
     * Set a paymentRequest and call {@link #verifyPaymentRequest} on  it.
     *
     * @param newPaymentRequest
     * @throws BSPaymentRequestException
     */
    public void setPaymentRequest(PaymentRequest newPaymentRequest) throws BSPaymentRequestException {
        if (newPaymentRequest == null)
            throw new NullPointerException("null paymentRequest");

        if (paymentRequest != null) {
            Log.w(TAG, "paymentrequest override");
        }
        verifyPaymentRequest(newPaymentRequest);
        paymentRequest = newPaymentRequest;
    }


    /**
     * Check that a payment request is valid, meaning amount is positive and currency exist in the SDL rates map
     * see {@link #getSupportedRates()} or {@link #getRatesArray()} for a list of supported rates
     *
     * @param paymentRequest a {@link #paymentRequest}
     * @throws BSPaymentRequestException
     */
    public void verifyPaymentRequest(PaymentRequest paymentRequest) throws BSPaymentRequestException {
        paymentRequest.verify();
        if (sDKConfiguration.getRates().getRatesMap() == null) {
            throw new BSPaymentRequestException("rates map is not populated. did you forget to call updateRates?");
        }
        if (sDKConfiguration.getRates().getRatesMap().get(paymentRequest.getCurrencyNameCode()) == null) {
            throw new BSPaymentRequestException("Currency not found");
        }
    }

    @Subscribe
    public synchronized void onCurrencyChange(Events.CurrencySelectionEvent currencySelectionEvent) {
        String baseCurrency = paymentRequest.getBaseCurrency();
        if (currencySelectionEvent.newCurrencyNameCode.equals(baseCurrency)) {
            paymentRequest.setAmount(paymentRequest.getBaseAmount());
            paymentRequest.setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);
            paymentRequest.setSubtotalAmount(paymentRequest.getBaseSubtotalAmount());
            paymentRequest.setTaxAmount(paymentRequest.getBaseTaxAmount());
            busInstance.post(new Events.CurrencyUpdatedEvent(paymentRequest.getBaseAmount(),
                    currencySelectionEvent.newCurrencyNameCode,
                    paymentRequest.getBaseTaxAmount(),
                    paymentRequest.getBaseSubtotalAmount()));
        } else {
            Double newPrice = convertPrice(paymentRequest.getBaseAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);

            paymentRequest.setAmount(newPrice);
            paymentRequest.setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);
            getPaymentResult().setAmount(newPrice);
            getPaymentResult().setCurrencyNameCode(currencySelectionEvent.newCurrencyNameCode);

            Double newTaxValue = convertPrice(paymentRequest.getBaseTaxAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);
            Double newSubtotal = convertPrice(paymentRequest.getBaseSubtotalAmount(), baseCurrency, currencySelectionEvent.newCurrencyNameCode);
            if (paymentRequest.isSubtotalTaxSet()) {
                paymentRequest.setSubtotalAmount(newSubtotal);
                paymentRequest.setTaxAmount(newTaxValue);
            }
            busInstance.post(new Events.CurrencyUpdatedEvent(newPrice, currencySelectionEvent.newCurrencyNameCode, newTaxValue, newSubtotal));
        }
        paymentResult.setAmount(paymentRequest.getAmount());
        paymentResult.setCurrencyNameCode(paymentRequest.getCurrencyNameCode());
        paymentResult.setShopperID(paymentRequest.getShopperID());


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