package com.bluesnap.androidapi.services;

import android.util.Log;

import com.bluesnap.androidapi.BuildConfig;
import com.bluesnap.androidapi.models.Card;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.ExchangeRate;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.PaymentResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.List;
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
    private static final String TAG = BlueSnapService.class.getSimpleName();
    private static final String CARD_TOKENIZE = "payment-fields-tokens/";
    private static final String RATES_SERVICE = "tokenized-services/rates";
    private static final BlueSnapService INSTANCE = new BlueSnapService();
    private static final String PAYPAL_SERVICE = "tokenized-services/paypal-token?amount=";
    private static final String PAYPAL_SHIPPING = "&req-confirm-shipping=0&no-shipping=2";
    private static final String RETRIEVE_TRANSACTION_SERVICE = "tokenized-services/transaction-status";
    private static final EventBus busInstance = new EventBus();
    private static String paypalURL;
    private static JSONObject errorDescription;
    private static String transactionStatus;
    private final AsyncHttpClient httpClient = new AsyncHttpClient();
    private HashMap<String, ExchangeRate> ratesMap;
    private ArrayList<ExchangeRate> ratesArray;
    private PaymentResult paymentResult;
    private PaymentRequest paymentRequest;
    private BluesnapToken bluesnapToken;
    private TokenServiceCallback checkoutActivity;
    private BluesnapServiceCallback bluesnapServiceCallback;

    public TokenProvider getTokenProvider() {
        return tokenProvider;
    }

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

    public void clearPayPalToken() {
        paypalURL = "";
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setup(String merchantToken) {
        setup(merchantToken, null);
    }

    /**
     * Setup the service to talk to the server.
     * This will reset the previous payment request
     *
     * @param tokenProvider A merchant function for requesting a new token if expired
     * @param merchantToken  A Merchant SDK token, obtained from the merchant.
     */
    public void setup(String merchantToken, TokenProvider tokenProvider) {
        if (null != tokenProvider)
            this.tokenProvider = tokenProvider;
        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);
        bluesnapToken.setToken(merchantToken);
        clearPayPalToken();
        setupHttpClient();
        paymentResult = null;
        paymentRequest = null;
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
    }

    /**
     * Update the Credit {@link Card} details on the BlueSnap Server
     *
     * @param card            {@link Card}
     * @param responseHandler {@link AsyncHttpResponseHandler}
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    public void tokenizeCard(Card card, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        JSONObject postData = new JSONObject();
        postData.put("ccNumber", card.getNumber());
        postData.put("cvv", card.getCVC());
        postData.put("expDate", card.getExpDate());
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
     * Update the Conversion rates map from the server.
     * The rates are merchant specific, the merchantToken is used to identify the merchant.
     *
     * @param callback A {@link BluesnapServiceCallback}
     */
    public void updateRates(final BluesnapServiceCallback callback) {
        this.bluesnapServiceCallback = callback;
        httpClient.addHeader(TOKEN_AUTHENTICATION, bluesnapToken.getMerchantToken());
        httpClient.get(bluesnapToken.getUrl() + RATES_SERVICE, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray exchangeRate = response.getJSONArray("exchangeRate");
                    //TODO: this can be optimized to create the ratesMap directly from the response
                    ratesArray = new Gson().fromJson(exchangeRate.toString(), new TypeToken<List<ExchangeRate>>() {
                    }.getType());
                    ratesMap = new HashMap<>(ratesArray.size() + 1);
                    ExchangeRate usdExchangeRate = new ExchangeRate();
                    usdExchangeRate.setConversionRate(1.0);
                    usdExchangeRate.setQuoteCurrency("USD");
                    ratesMap.put("USD", usdExchangeRate);
                    for (ExchangeRate r : ratesArray) {
                        ratesMap.put(r.getQuoteCurrency(), r);
                    }
                    callback.onSuccess();
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Rates convert service error", throwable);
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
                                                        updateRates(bluesnapServiceCallback);
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

    public ArrayList<ExchangeRate> getRatesArray() {
        return ratesArray;
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
    public Set<String> getSupportedRates() {
        return ratesMap.keySet();
    }

    /**
     * Convert a price in USD to a price in another currency  in ISO 4217 Code.
     *
     * @param usdPrice  A String representation of a USD price which will be converted to a double value.
     * @param convertTo ISO 4217 compatible  3 letter currency representation
     * @return String representation of converted price.
     */
    public String convertUSD(String usdPrice, String convertTo) {
        if (usdPrice == null || usdPrice.isEmpty())
            return "0";

        ExchangeRate rate = ratesMap.get(convertTo);
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
        Double usdPRice = basePrice / ratesMap.get(currentCurrencyNameCode).getInverseConversionRate();
        Double newPrice = ratesMap.get(newCurrencyNameCode).getConversionRate() * usdPRice;
        return newPrice;
    }

    public synchronized PaymentResult getPaymentResult() {
        if (paymentResult == null) {
            paymentResult = new PaymentResult();
            // Copy vallues from request
            paymentResult.setAmount(paymentRequest.getAmount());
            paymentResult.setCurrencyNameCode(paymentRequest.getCurrencyNameCode());
            paymentResult.setShopperID(paymentRequest.getShopperID());
        }
        return paymentResult;
    }

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest newPaymentRequest) {
        if (newPaymentRequest == null)
            throw new NullPointerException("null paymentRequest");

        if (paymentRequest != null) {
            Log.w(TAG, "paymentrequest override");
        }
        paymentRequest = newPaymentRequest;

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
}