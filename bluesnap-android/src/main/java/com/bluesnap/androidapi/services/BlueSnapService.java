package com.bluesnap.androidapi.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.http.AppExecutors;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.*;
import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.CARDTYPE;
import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.FRAUDSESSIONID;
import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.LAST4DIGITS;
import static com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory.createDataObject;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * Core BlueSnap Service class that handles network and maintains {@link SdkRequest}
 */
public class BlueSnapService {
    private static final String TAG = BlueSnapService.class.getSimpleName();
    private static final BlueSnapService INSTANCE = new BlueSnapService();
    private static String paypalURL;
    private static JSONObject errorDescription;
    private static String transactionStatus;
    private final BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private final KountService kountService = KountService.getInstance();
    private SdkResult sdkResult;
    private SdkRequest sdkRequest;
    private ShopperInfoConfig shopperInfoConfig;
    private BluesnapToken bluesnapToken;
    private BluesnapServiceCallback bluesnapServiceCallback;
    private SDKConfiguration sDKConfiguration;
    private TokenProvider tokenProvider;
    private AppExecutors appExecutors;

    public static BlueSnapService getInstance() {
        return INSTANCE;
    }

    public static String getPayPalToken() {
        return paypalURL;
    }

    public static JSONObject getErrorDescription() {
        return errorDescription;
    }

    public SDKConfiguration getsDKConfiguration() {
        return sDKConfiguration;
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
    public void setup(String merchantToken, TokenProvider tokenProvider, String merchantStoreCurrency, @NonNull Context context, final BluesnapServiceCallback callback) {
        this.bluesnapServiceCallback = callback;
        if (null != tokenProvider)
            this.tokenProvider = tokenProvider;

        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);

        blueSnapAPI.setupMerchantToken(bluesnapToken.getMerchantToken(), bluesnapToken.getUrl());
        sdkResult = null;

        clearPayPalToken();
        sdkInit(merchantStoreCurrency, context, callback);

        Log.d(TAG, "Service setup with token" + merchantToken.substring(merchantToken.length() - 5));
    }

    /**
     * check if paypal url is same as before and clears it if so
     *
     * @param merchantToken
     */
    private void initPayPal(String merchantToken) {
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
    private void changeExpiredToken(String merchantToken) {
        bluesnapToken = new BluesnapToken(merchantToken, tokenProvider);
        bluesnapToken.setToken(merchantToken);
        initPayPal(merchantToken);
        blueSnapAPI.setupMerchantToken(bluesnapToken.getMerchantToken(), bluesnapToken.getUrl());
        // after expired token is replaced - placing new token in payment result
        if (null != sdkResult)
            sdkResult.setToken(merchantToken);
        Log.d(TAG, "Service change with token" + merchantToken.substring(merchantToken.length() - 5));

    }

    public void setNewToken(String newToken) {
        changeExpiredToken(newToken);
    }

    /**
     * Update shopper details on the BlueSnap Server
     */
    public BlueSnapHTTPResponse submitUpdatedShopperDetails(final Shopper shopper) {
        Log.d(TAG, "update Shopper on token " + bluesnapToken.toString());
        String kountSessionId = getKountSessionId();
        JSONObject ShopperJsonObject = (null != kountSessionId || !"".equals(getKountSessionId())) ? shopper.toJsonWithFraudSessionId(kountSessionId) : shopper.toJson();
        return blueSnapAPI.updateShopper(ShopperJsonObject.toString());
    }

    /**
     * check Credit Card Number In Server
     *
     * @param creditCardNumber - credit Card Number String {@link CreditCard}
     * @throws JSONException                in case of invalid JSON object (should not happen)
     * @throws UnsupportedEncodingException should not happen
     */
    public BlueSnapHTTPResponse submitTokenizedCCNumber(final String creditCardNumber) throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        JSONObject postData = new JSONObject();
        postData.put(BSTokenizeDetailsJsonFactory.CCNUMBER, creditCardNumber);
        return blueSnapAPI.tokenizeDetails(postData.toString());
    }

    /**
     * Update details on the BlueSnapValidator Server
     *
     * @param purchaseDetails {@link PurchaseDetails}
     * @throws JSONException                in case of invalid JSON object (should not happen)
     * @throws UnsupportedEncodingException should not happen
     */
    public BlueSnapHTTPResponse submitTokenizedDetails(PurchaseDetails purchaseDetails) throws JSONException {
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        return blueSnapAPI.tokenizeDetails(createDataObject((ShopperInfoConfig) sdkRequest, purchaseDetails, getKountSessionId()).toString());
    }

    /**
     * Update details on the BlueSnapValidator Server
     *
     * @param creditCard {@link CreditCard}
     */
    public BlueSnapHTTPResponse submitCreditCardDetailsForShopperConfiguration(CreditCard creditCard) {
        Log.d(TAG, "Tokenizing card on token " + bluesnapToken.toString());
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, CARDTYPE, creditCard.getCardType());
        putJSONifNotNull(jsonObject, LAST4DIGITS, creditCard.getCardLastFourDigits());
        putJSONifNotNull(jsonObject, FRAUDSESSIONID, getKountSessionId());
        return blueSnapAPI.tokenizeDetails(jsonObject.toString());
    }

    /**
     * Check if Token is Expired on the BlueSnap Server
     *
     * @throws JSONException                in case of invalid JSON object (should not happen)
     * @throws UnsupportedEncodingException should not happen
     */
    private BlueSnapHTTPResponse checkTokenIsExpired() throws UnsupportedEncodingException {
        Log.d(TAG, "Check if Token is Expired: " + bluesnapToken.toString());
        return blueSnapAPI.tokenizeDetails(null);
    }

    /**
     * check Token Is Expired and tries to create a new one if so
     *
     * @param callback                   - {@link BluesnapServiceCallback}
     * @param afterNewTokenCreatedAction - {@link AfterNewTokenCreatedAction}
     */
    private void tokenExpiredAction(final BluesnapServiceCallback callback, final AfterNewTokenCreatedAction afterNewTokenCreatedAction) {
        // try to PUT empty {} to check if token is expired
        getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BlueSnapHTTPResponse response = checkTokenIsExpired();
                    if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "SDK Init service error, checkTokenIsExpired successful");
                        callback.onFailure();
                    } else if (response.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST && null != getTokenProvider() && !"".equals(response.getResponseString())) {
                        try {
                            JSONObject errorResponse = new JSONObject(response.getResponseString());
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
                            callback.onFailure();
                        }
                    } else {
                        String errorMsg = String.format("Service Error for tokenExpiredAction [%s], [%s]", response.getResponseCode(), response.getResponseString());
                        Log.e(TAG, errorMsg);
                        callback.onFailure();
                    }

                } catch (UnsupportedEncodingException ex) {
                    Log.e(TAG, ex.getMessage());
                    callback.onFailure();
                }
            }
        });
    }

    /**
     * SDK Init.
     *
     * @param merchantStoreCurrency All rates are derived from merchantStoreCurrency. merchantStoreCurrency * AnyRate = AnyCurrency
     */
    private void sdkInit(final String merchantStoreCurrency, final Context context, final BluesnapServiceCallback callback) {
        getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapAPI.sdkInit(merchantStoreCurrency);
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        sDKConfiguration = JsonParser.parseSdkConfiguration(response.getResponseString());
                        //sDKConfiguration.getRates().setInitialRates();

                        try {
                            if (null != context)
                                kountService.setupKount(sDKConfiguration.getKountMerchantId(), context, getBlueSnapToken().isProduction());
                        } catch (Exception e) {
                            Log.e(TAG, "Kount SDK initialization error " + e.getMessage());
                        }
                        callback.onSuccess();
                    } catch (Exception e) {
                        Log.e(TAG, "exception: ", e);
                        callback.onFailure();
                    }
                } else {
                    Log.e(TAG, "SDK Init service error");
                    tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                        @Override
                        public void complete() {
                            sdkInit(merchantStoreCurrency, context, bluesnapServiceCallback);
                        }
                    });
                }
            }
        });
    }


    /**
     * retrieve Rates Array
     *
     * @return Currency Rate Array
     */
    public ArrayList<Currency> getRatesArray() {
        return sDKConfiguration.getRates().getCurrencies();
    }

    /**
     * activates creation of PayPal Token(URL) {@link BlueSnapAPI}
     *
     * @param amount   - amount to change
     * @param currency - currency to charge with
     * @param callback - what to do when done
     */
    public void createPayPalToken(final Double amount, final String currency, final BluesnapServiceCallback callback) {
        getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapAPI.createPayPalToken(amount, currency, sdkRequest.isShippingRequired());
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        paypalURL = new JSONObject(response.getResponseString()).getString("paypalUrl");
                        callback.onSuccess();
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                        errorDescription = new JSONObject();
                        getAppExecutors().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure();
                            }
                        });
                    }
                } else if (response.getErrorResponseString() != null) {
                    errorDescription = new JSONObject();
                    try {
                        JSONArray errorResponseJSONArray = new JSONObject(response.getErrorResponseString()).getJSONArray("message");
                        JSONObject errorJson = errorResponseJSONArray.getJSONObject(0);
                        errorDescription = errorJson;
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                    }
                    Log.e(TAG, "PayPal service error");
                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });

                } else {
                    errorDescription = new JSONObject();
                    try {
                        //todo
                        // errorDescription.put("errorName", responseString.replaceAll("\"", "").toUpperCase());
                        errorDescription.put("code", response.getResponseCode());
                        //  errorDescription.put("description", responseString.replaceAll("\"", ""));
                        Log.e(TAG, "PayPal service error");
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
            }
        });
    }

    /**
     * check transaction status after PayPal transaction occurred
     *
     * @param callback - what to do when done
     */
    public void retrieveTransactionStatus(final BluesnapServiceCallback callback) {
        getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapAPI.retrieveTransactionStatus();
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        transactionStatus = new JSONObject(response.getResponseString()).getString("processingStatus");
                        callback.onSuccess();
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                        callback.onFailure();
                    }
                } else {
                    Log.e(TAG, "PayPal service error");
                    callback.onFailure();
                }
            }
        });
    }

    /**
     * Get a set of strings of the supported conversion rates
     *
     * @return {@link Set < String >}
     */
    @Nullable
    public Set<String> getSupportedRates() {
        return sDKConfiguration.getRates().getCurrencyCodes();
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

        PriceDetails priceDetails = new PriceDetails(Double.valueOf(usdPrice), SupportedPaymentMethods.USD, 0D);
        convertPrice(priceDetails, convertTo);
        return String.valueOf(AndroidUtil.getDecimalFormat().format(priceDetails.getAmount()));
    }

    /**
     * Convert a price in currentCurrencyNameCode to newCurrencyNameCode locally and return it
     *
     * @param receivedPriceDetails The price details before conversion
     * @param newCurrencyCode      The ISO 4217 currency name
     * @return priceDetails {@link PriceDetails}
     */
    public PriceDetails getConvertedPriceDetails(PriceDetails receivedPriceDetails, String newCurrencyCode) {
        PriceDetails localPriceDetails = new PriceDetails(receivedPriceDetails.getSubtotalAmount(), receivedPriceDetails.getCurrencyCode(), receivedPriceDetails.getTaxAmount());

        String currentCurrencyCode = localPriceDetails.getCurrencyCode();
        if (!checkCurrencyCompatibility(currentCurrencyCode) || !checkCurrencyCompatibility(newCurrencyCode))
            throw new IllegalArgumentException("not an ISO 4217 compatible 3 letter currency representation");

        // get Rates
        Rates rates = sDKConfiguration.getRates();

        // check if currentCurrencyNameCode is MerchantStoreCurrency
        Double currentRate = rates.getCurrencyByCode(currentCurrencyCode).getConversionRate();
        Double newRate = rates.getCurrencyByCode(newCurrencyCode).getConversionRate() / currentRate;

        Double newSubtotal = localPriceDetails.getSubtotalAmount() * newRate;
        Double taxAmount = localPriceDetails.getTaxAmount();
        Double newTaxAmount = (taxAmount == null) ? null : taxAmount * newRate;

        localPriceDetails.set(newSubtotal, newCurrencyCode, newTaxAmount);
        return localPriceDetails;
    }

    /**
     * Convert a price in currentCurrencyNameCode to newCurrencyNameCode
     *
     * @param priceDetails    The price details before conversion
     * @param newCurrencyCode The ISO 4217 currency name
     */
    public void convertPrice(PriceDetails priceDetails, String newCurrencyCode) {
        PriceDetails localPriceDetails = getConvertedPriceDetails(priceDetails, newCurrencyCode);
        priceDetails.set(localPriceDetails.getSubtotalAmount(), localPriceDetails.getCurrencyCode(), localPriceDetails.getTaxAmount());

    }

    /**
     * get SdkResult with token, amount and currency set
     *
     * @return {@link SdkResult}
     */
    public synchronized SdkResult getSdkResult() {
        if (sdkResult == null) {
            sdkResult = new SdkResult();
        }

        try {
            sdkResult.setToken(bluesnapToken.getMerchantToken());
            // Copy values from request
            final PriceDetails priceDetails = sdkRequest.getPriceDetails();
            sdkResult.setAmount(priceDetails.getAmount());
            sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
        } catch (Exception e) {
            Log.e(TAG, "sdkResult set Token, Amount, Currency or ShopperId resulted in an error");
        }
        return sdkResult;
    }

    @NonNull
    public SdkRequest getSdkRequest() {
        return sdkRequest;
    }

    /**
     * Set a sdkRequest and call on  it.
     *
     * @param newSdkRequest SdkRequest an Sdk request to uses
     * @throws BSPaymentRequestException in case of invalid SdkRequest
     */
    public synchronized void setSdkRequest(@NonNull SdkRequest newSdkRequest) throws BSPaymentRequestException {

        if (sdkRequest != null) {
            Log.w(TAG, "sdkRequest override");
        }
        sdkRequest = newSdkRequest;
        sdkResult = new SdkResult();
        // Copy values from request
        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        sdkResult.setAmount(priceDetails.getAmount());
        sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
    }

    /**
     * @param newCurrencyNameCode - String, new Currency Name Code
     * @param context             - Context
     */
    public void onCurrencyChange(String newCurrencyNameCode, Context context) {
        Log.d(TAG, "onCurrencyChange= " + newCurrencyNameCode);
        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        convertPrice(priceDetails, newCurrencyNameCode);
        sdkResult.setAmount(priceDetails.getAmount());
        sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
        BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, TAG);
    }

    public BluesnapToken getBlueSnapToken() {
        return bluesnapToken;
    }

    /**
     * check if country has zip according to countries without zip array {@link Constants}
     *
     * @param context - {@link Context}
     * @return boolean
     */
    public boolean doesCountryhaveZip(Context context) {
        return (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getUserCountry(context)));
    }

    /**
     * returns user country according to {@link TelephonyManager} sim or network
     *
     * @param context - {@link Context}
     * @return Country - ISO 3166-1 alpha-2 standard, default value is US
     */
    public String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                Log.w(TAG, "TelephonyManager is null");
            } else {
                final String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    return simCountry.toUpperCase(Locale.US);
                } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2) {
                        return networkCountry.toUpperCase(Locale.US);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "TelephonyManager, getSimCountryIso or getNetworkCountryIso failed");
        }

        return Locale.US.getCountry();
    }

    /**
     * Update the roce details according to shipping country and state, by calling the provided TaxCalculator.
     *
     * @param shippingCountry
     * @param shippingState
     * @param context
     */
    public void updateTax(String shippingCountry, String shippingState, Context context) {

        SdkRequest sdkRequest = getSdkRequest();
        TaxCalculator taxCalculator = sdkRequest.getTaxCalculator();
        if (sdkRequest.isShippingRequired() && taxCalculator != null) {
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            Log.d(TAG, "Calling taxCalculator; shippingCountry=" + shippingCountry + ", shippingState=" + shippingState + ", priceDetails=" + priceDetails);
            taxCalculator.updateTax(shippingCountry, shippingState, priceDetails);
            Log.d(TAG, "After calling taxCalculator; priceDetails=" + priceDetails);
            // send event to update amount in UI
            BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, TAG);
        }
    }


    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }

    /**
     * get Shopper Info Config
     *
     * @return {@link ShopperInfoConfig}
     */
    public ShopperInfoConfig getShopperInfoConfig() {
        return shopperInfoConfig;
    }

    /**
     * set Shopper Info Config
     *
     * @param shopperInfoConfig - {@link ShopperInfoConfig}
     */
    public synchronized void setShopperInfoConfig(@NonNull ShopperInfoConfig shopperInfoConfig) {
        if (this.shopperInfoConfig != null) {
            Log.w(TAG, "shopperInfoConfig override");
        }
        this.shopperInfoConfig = shopperInfoConfig;

        try {
            setSdkRequest(new SdkRequest(0.01D, SupportedPaymentMethods.USD, shopperInfoConfig));
        } catch (BSPaymentRequestException e) {
            Log.d(TAG, "failed to create Sdk Request for setShopperInfoConfig: " + e.getMessage());
        }
        sdkRequest.setAllowCurrencyChange(false);

    }

    /**
     * set Shopper Configuration
     * This will reset the previous payment request
     *
     * @param merchantToken A Merchant SDK token, obtained from the merchant.
     * @param tokenProvider A merchant function for requesting a new token if expired
     * @param context       A Merchant Application Context
     * @param callback      A {@link BluesnapServiceCallback}
     */
    public void setShopperConfiguration(String merchantToken, TokenProvider tokenProvider, @NonNull Context context, final BluesnapServiceCallback callback) {
        setup(merchantToken, tokenProvider, context, callback);
    }

    /**
     * After calling initBluesnap() with a token created for an existing shopper, the merchant app can use this method to
     * get the shopper details, including the chosen payment method
     *
     * @return ShopperConfiguration
     */
    public ShopperConfiguration getShopperConfiguration() {

        ShopperConfiguration res = null;
        final Shopper shopper = sDKConfiguration.getShopper();
        if (shopper != null) {
            BillingContactInfo billingContactInfo = new BillingContactInfo(shopper);
            billingContactInfo.setEmail(shopper.getEmail());
            ShippingContactInfo shippingContactInfo = shopper.getShippingContactInfo() == null ? null : new ShippingContactInfo(shopper.getShippingContactInfo());
            ChosenPaymentMethod chosenPaymentMethod = shopper.getChosenPaymentMethod() == null ? null : new ChosenPaymentMethod(shopper.getChosenPaymentMethod());
            res = new ShopperConfiguration(billingContactInfo, shippingContactInfo, chosenPaymentMethod);
        }
        return res;
    }

    @Nullable
    protected String getKountSessionId() {
        return kountService.getKountSessionId();
    }

    private interface AfterNewTokenCreatedAction {
        void complete();
    }

}