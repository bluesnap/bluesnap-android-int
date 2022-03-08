package com.bluesnap.androidapi.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.http.AppExecutors;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BSTokenizeDetailsJsonFactory;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.Rates;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
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
    private SdkRequestBase sdkRequestBase;
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

    }

    public void setNewToken(String newToken) {
        changeExpiredToken(newToken);
    }

    /**
     * Update shopper details on the BlueSnap Server
     *
     * @param shopper  - {@link Shopper}
     * @param callback - {@link BluesnapServiceCallback}
     */
    public void submitUpdatedShopperDetails(final Shopper shopper, final BluesnapServiceCallback callback) {
        Log.d(TAG, "update Shopper on token " + bluesnapToken.toString());
        getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BlueSnapHTTPResponse response = blueSnapAPI.updateShopper(shopper.toJson().toString());
                if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess();
                } else if (HttpURLConnection.HTTP_UNAUTHORIZED == response.getResponseCode()) {
                    Log.e(TAG, "create PayPal Token service error");
                    tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                        @Override
                        public void complete() {
                            submitUpdatedShopperDetails(shopper, callback);
                        }
                    });
                } else {
                    String errorMsg = String.format("submit Updated Shopper Details error [%s], [%s]", response.getResponseCode(), response.getResponseString());
                    Log.e(TAG, errorMsg);
                    callback.onFailure();
                }
            }
        });
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
     * Submit GPay result token to server
     *
     * @param paymentToken - payment token (for GPay, this is a base64-encoded payload data)
     * @param paymentMethod - payment method (for example: SupportedPaymentMethods.GOOGLE_PAY)
     * @throws JSONException                in case of invalid JSON object (should not happen)
     * @throws UnsupportedEncodingException should not happen
     */
    public BlueSnapHTTPResponse submitTokenenizedPayment(final String paymentToken, final String paymentMethod) throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Tokenizing GPay on token " + bluesnapToken.toString());
        JSONObject postData = new JSONObject();
        postData.put(BSTokenizeDetailsJsonFactory.PAYMENT_TOKEN, paymentToken);
        postData.put(BSTokenizeDetailsJsonFactory.PAYMENT_METHOD, paymentMethod);
        putJSONifNotNull(postData, FRAUDSESSIONID, kountService.getKountSessionId());
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
        return blueSnapAPI.tokenizeDetails(createDataObject(purchaseDetails).toString());
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
        putJSONifNotNull(jsonObject, FRAUDSESSIONID, kountService.getKountSessionId());
        return blueSnapAPI.tokenizeDetails(jsonObject.toString());
    }

    /**
     * Check if Token is Expired on the BlueSnap Server
     * need to be empty JSON otherwise will receive general server error
     *
     * @throws JSONException                in case of invalid JSON object (should not happen)
     * @throws UnsupportedEncodingException should not happen
     */
    private BlueSnapHTTPResponse checkTokenIsExpired() throws UnsupportedEncodingException {
        Log.d(TAG, "Check if Token is Expired: " + bluesnapToken.toString());
        return blueSnapAPI.tokenizeDetails((new JSONObject()).toString());
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
                    } else if (response.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST && null != getTokenProvider() && !"".equals(response.getErrorResponseString())) {
                        try {
                            JSONObject errorResponse = new JSONObject(response.getErrorResponseString());
                            JSONArray rs2 = (JSONArray) errorResponse.get("message");
                            JSONObject rs3 = (JSONObject) rs2.get(0);
                            if ("EXPIRED_TOKEN".equals(rs3.get("errorName"))) {
                                getTokenProvider().getNewToken(
                                        new TokenServiceCallback() {
                                            @Override
                                            public void complete(String newToken) {
                                                setNewToken(newToken);
                                                afterNewTokenCreatedAction.complete();
                                            }
                                        }
                                );
                            } else {
                                Log.e(TAG, "Token not found error");
                                callback.onFailure();
                            }
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

                        CardinalManager cardinalManager = CardinalManager.getInstance();

                        //set JWT in Cardinal manager
                        cardinalManager.setCardinalJWT(sDKConfiguration.getCardinalToken());

                        //cardinal configure & init
                        cardinalManager.configureCardinal(context, getBlueSnapToken().isProduction());
                        cardinalManager.initCardinal(new InitCardinalServiceCallback() {
                            @Override
                            public void onComplete() {
                                callback.onSuccess();
                            }
                        });


                    } catch (Exception e) {
                        Log.e(TAG, "exception: ", e);
                        callback.onFailure();
                    }
                } else if (HttpURLConnection.HTTP_UNAUTHORIZED == response.getResponseCode()) {
                    Log.e(TAG, "SDK Init service error");
                    tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                        @Override
                        public void complete() {
                            sdkInit(merchantStoreCurrency, context, bluesnapServiceCallback);
                        }
                    });
                } else {
                    String errorMsg = String.format("SDK Init service error [%s], [%s]", response.getResponseCode(), response.getErrorResponseString());
                    Log.e(TAG, errorMsg);
                    callback.onFailure();
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
                BlueSnapHTTPResponse response = blueSnapAPI.createPayPalToken(amount, currency, sdkRequestBase.getShopperCheckoutRequirements().isShippingRequired());
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
                                Log.e(TAG, "paypal call return bad response:" + response.getResponseCode());
                                callback.onFailure();
                            }
                        });
                    }
                } else if (HttpURLConnection.HTTP_UNAUTHORIZED == response.getResponseCode()) {
                    Log.e(TAG, "create PayPal Token service error");
                    tokenExpiredAction(callback, new AfterNewTokenCreatedAction() {
                        @Override
                        public void complete() {
                            createPayPalToken(amount, currency, callback);
                        }
                    });
                } else if ((HttpURLConnection.HTTP_BAD_REQUEST == response.getResponseCode()
                        || HttpURLConnection.HTTP_FORBIDDEN == response.getResponseCode())
                        && response.getErrorResponseString() != null) {
                    errorDescription = new JSONObject();
                    try {
                        JSONArray errorResponseJSONArray = new JSONObject(response.getErrorResponseString()).getJSONArray("message");
                        JSONObject errorJson = errorResponseJSONArray.getJSONObject(0);
                        errorDescription = errorJson;
                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing exception", e);
                    }
                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "paypal call return bad response:" + response.getResponseCode());
                            callback.onFailure();
                        }
                    });

                } else {
                    String errorMsg = String.format("create PayPal Token service error [%s], [%s]", response.getResponseCode(), response.getResponseString());
                    Log.e(TAG, errorMsg);
                    callback.onFailure();
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
                    // if token is expired than transaction will fail
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
            sdkRequestBase.setSdkResult(sdkResult);
        } catch (Exception e) {
            Log.e(TAG, "sdkResult set Token, Amount, Currency or ShopperId resulted in an error");
        }
        return sdkResult;
    }

    @NonNull
    public SdkRequestBase getSdkRequest() {
        return sdkRequestBase;
    }

    /**
     * Set a sdkRequest and call on  it.
     *
     * @param newSdkRequestBase SdkRequestBase an Sdk request to uses
     * @throws BSPaymentRequestException in case of invalid SdkRequest
     */
    public synchronized void setSdkRequest(@NonNull SdkRequestBase newSdkRequestBase) throws BSPaymentRequestException {

        if (sdkRequestBase != null) {
            Log.w(TAG, "sdkRequest override");
        }
        sdkRequestBase = newSdkRequestBase;
        sdkResult = new SdkResult();
        // Copy values from request
        sdkRequestBase.setSdkResult(sdkResult);
    }

    /**
     * @param newCurrencyNameCode - String, new Currency Name Code
     * @param context             - Context
     */
    public void onCurrencyChange(String newCurrencyNameCode, Context context) {
        if (sdkRequestBase instanceof SdkRequest) {
            Log.d(TAG, "onCurrencyChange= " + newCurrencyNameCode);
            final PriceDetails priceDetails = sdkRequestBase.getPriceDetails();
            convertPrice(priceDetails, newCurrencyNameCode);
            sdkResult.setAmount(priceDetails.getAmount());
            sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
            // any changes like currency and/or amount while not creating a new token should clear previous used PayPal token
            clearPayPalToken();
            BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, TAG);
        }
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
        return (BlueSnapValidator.checkCountryHasZip(getUserCountry(context)));
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
        sdkRequestBase.updateTax(shippingCountry, shippingState);
        // send event to update amount in UI
        if (sdkRequestBase instanceof SdkRequest)
            BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, TAG);
    }


    public synchronized AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
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


    private interface AfterNewTokenCreatedAction {
        void complete();
    }

}