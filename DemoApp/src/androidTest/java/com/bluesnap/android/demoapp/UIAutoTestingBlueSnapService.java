package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleCallback;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShoppersFactory;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by sivani on 03/09/2018.
 */

public class UIAutoTestingBlueSnapService<StartUpActivity extends Activity> {
    private static final String TAG = UIAutoTestingBlueSnapService.class.getSimpleName();
    private ActivityTestRule<StartUpActivity> mActivityRule;
    private StartUpActivity mActivity;
    public Context applicationContext;
    public BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private SDKConfiguration sDKConfiguration = null;
    private DemoTransactions transactions;
    private static final String SANDBOX_TRANSACTION = "transactions/";

    private boolean isExistingCard = false;
    protected ReturningShoppersFactory.TestingShopper returningShopper;

    private boolean isSdkRequestNull = false;

    private String vaultedShopperId;

    private SdkResult sdkResult;

    private HttpURLConnection myURLConnection;
    private String merchantToken;

    private List<CustomHTTPParams> sahdboxHttpHeaders = getHttpParamsForSandboxTests();
    private static final String SANDBOX_VAULTED_SHOPPER = "vaulted-shoppers";
    private String getShopperResponse;
    private String createVaultedShopperResponse;
    private String emailFromServer;


    private RandomTestValuesGenerator randomTestValuesGenerator = new RandomTestValuesGenerator();

    protected String defaultCountryKey;
    private String defaultCountryValue;
    protected String checkoutCurrency = "USD";
    protected double purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
    private double taxPercent = randomTestValuesGenerator.randomTaxPercentage() / 100;
    protected double taxAmount = purchaseAmount * taxPercent;

    /**
     * constructor without opening URL connection
     */
    public UIAutoTestingBlueSnapService(ActivityTestRule<StartUpActivity> mActivityRule) {
        this.mActivityRule = mActivityRule;
    }

    public void setmActivityRule(ActivityTestRule<StartUpActivity> mActivityRule) {
        this.mActivityRule = mActivityRule;
    }

    public ReturningShoppersFactory.TestingShopper getReturningShopper() {
        return returningShopper;
    }

    public void setExistingCard(boolean existingCard) {
        isExistingCard = existingCard;
    }

    public String getDefaultCountryKey() {
        return defaultCountryKey;
    }

    public String getDefaultCountryValue() {
        return defaultCountryValue;
    }

    public String getCheckoutCurrency() {
        return checkoutCurrency;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public double getTaxPercent() {
        return taxPercent;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public String getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(String vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    public DemoTransactions getTransactions() {
        return transactions;
    }

    public void setTransactions(DemoTransactions transactions) {
        this.transactions = transactions;
    }

    public void setSdk(SdkRequestBase sdkRequest, TestingShopperCheckoutRequirements shopperCheckoutRequirements) throws JSONException, BSPaymentRequestException, InterruptedException {
        sdkRequest.getShopperCheckoutRequirements().setBillingRequired(shopperCheckoutRequirements.isFullBillingRequired());

        sdkRequest.getShopperCheckoutRequirements().setEmailRequired(shopperCheckoutRequirements.isEmailRequired());

        sdkRequest.getShopperCheckoutRequirements().setShippingRequired(shopperCheckoutRequirements.isShippingRequired());
    }

    /**
     * Sets URL connection for token without returning shopper
     */
    private void setUrlConnection() {
        setUrlConnection("", false);
    }

    /**
     * Sets URL connection for token with returning shopper
     * from factory
     */
    private void setUrlConnection(boolean fromFactory) {
        setUrlConnection("", fromFactory);
    }

    /**
     * Sets URL connection for token with a given
     * returning shopper
     */
    private void setUrlConnection(String returningShopper) {
        setUrlConnection(returningShopper, false);
    }

    /**
     * Sets URL connection for token with or without returning shopper
     */
    private void setUrlConnection(String returningOrNewShopper, boolean fromFactory) {
        if (!returningOrNewShopper.isEmpty())
            returningOrNewShopper = "?shopperId=" + returningOrNewShopper;
        else if (fromFactory) {
            returningShopper = ReturningShoppersFactory.getReturningShopper();
            returningOrNewShopper = "?shopperId=" + returningShopper.getShopperId();
        }

        try {
            URL myURL = new URL(SANDBOX_URL + SANDBOX_TOKEN_CREATION + returningOrNewShopper);
            myURLConnection = (HttpURLConnection) myURL.openConnection();
        } catch (IOException e) {
            fail("Network error open server connection:" + e.getMessage());
            e.printStackTrace();
        }
    }


    public void setSdk(SdkRequestBase sdkRequest, boolean withFullBilling, boolean withEmail, boolean withShipping) {
        sdkRequest.getShopperCheckoutRequirements().setBillingRequired(withFullBilling);
        sdkRequest.getShopperCheckoutRequirements().setEmailRequired(withEmail);
        sdkRequest.getShopperCheckoutRequirements().setShippingRequired(withShipping);
    }

    private void doSetup() {
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }
        //randomTestValuesGenerator = new RandomTestValuesGenerator();
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);

        //Wake up device again in case token fetch took to much time
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest) throws InterruptedException, BSPaymentRequestException {
        setupAndLaunch(sdkRequest, "USD", false, "");
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest, String merchantStoreCurrency) throws InterruptedException, BSPaymentRequestException {
        setupAndLaunch(sdkRequest, merchantStoreCurrency, false, "");
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest, boolean forReturningShopper, String returningShopperId) throws InterruptedException, BSPaymentRequestException {
        setupAndLaunch(sdkRequest, "USD", forReturningShopper, returningShopperId);
    }

    /**
     * Setup app and sdk:
     * set URL connection, setup device, create token, setup sdk and lunch activity.
     */
    public void setupAndLaunch(SdkRequestBase sdkRequest, String merchantStoreCurrency, boolean forReturningShopper, String returningShopperId) throws InterruptedException, BSPaymentRequestException {
        if (forReturningShopper) {
            if (!returningShopperId.isEmpty())
                setUrlConnection(returningShopperId);
            else
                setUrlConnection(true);
        } else
            setUrlConnection();

        doSetup();
        sdkRequest.setTaxCalculator(new TaxCalculator() {
            @Override
            public void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails) {
                if ("us".equalsIgnoreCase(shippingCountry)) {
                    Double taxRate = taxPercent;
                    if ("ma".equalsIgnoreCase(shippingState)) {
                        taxRate = 0.1;
                    }
                    priceDetails.setTaxAmount(priceDetails.getSubtotalAmount() * taxRate);
                } else {
                    priceDetails.setTaxAmount(0D);
                }
            }
        });

        setSDKToken(merchantStoreCurrency);
        Intent intent = new Intent();
        blueSnapService.setSdkRequest(sdkRequest);
        mActivityRule.launchActivity(intent);
        mActivity = mActivityRule.getActivity();
        applicationContext = mActivity.getApplicationContext();
        defaultCountryKey = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        String[] countryKeyArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_key_array);
        String[] countryValueArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_value_array);

        defaultCountryValue = countryValueArray[Arrays.asList(countryKeyArray).indexOf(defaultCountryKey)];
    }

    private void setSDKToken(String merchantStoreCurrency) throws InterruptedException {
        try {
            String userCredentials = SANDBOX_USER + ":" + SANDBOX_PASS;
            String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), 0));
            myURLConnection.setRequestProperty("Authorization", basicAuth);
            myURLConnection.setRequestMethod("POST");
            myURLConnection.connect();
            int responseCode = myURLConnection.getResponseCode();
            String locationHeader = myURLConnection.getHeaderField("Location");
            merchantToken = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
        } catch (IOException e) {
            fail("Network error obtaining token:" + e.getMessage());
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        final TokenProvider tokenProvider = new TokenProvider() {
                            @Override
                            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {
                                new TokenServiceInterface() {
                                    @Override
                                    public void onServiceSuccess() {
                                        //change the expired token
                                        tokenServiceCallback.complete(merchantToken);
                                    }

                                    @Override
                                    public void onServiceFailure() {
                                    }
                                };
                            }
                        };
                        BlueSnapService.getInstance().setup(merchantToken, tokenProvider, merchantStoreCurrency, null, new BluesnapServiceCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Service finish setup");
                                isSdkRequestNull = true;
                            }

                            @Override
                            public void onFailure() {
                                fail("Service could not finish setup");
                                isSdkRequestNull = true;
                            }
                        });

                    }
                });
        while (BlueSnapService.getInstance().getBlueSnapToken() == null) {
            Log.d(TAG, "Waiting for token setup");
            sleep(200);

        }

        while (BlueSnapService.getInstance().getsDKConfiguration() == null || BlueSnapService.getInstance().getsDKConfiguration().equals(sDKConfiguration)) {
            Log.d(TAG, "Waiting for SDK configuration to finish");
            sleep(200);

        }

        sDKConfiguration = BlueSnapService.getInstance().getsDKConfiguration();

        while (!isSdkRequestNull) {
            Log.d(TAG, "Waiting for SDK request to finish");
            sleep(500);
        }

        isSdkRequestNull = false;
    }


    @NonNull
    public List<CustomHTTPParams> getHttpParamsForSandboxTests() {
        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        return headerParams;
    }

    public void checkToken() {
        try {
            onView(withText(containsString("Cannot obtain token"))).check(matches(isDisplayed()));
            fail("No token from server");
        } catch (NoMatchingViewException e) {
            //view not displayed logic
        }
    }

    //@After
    public void detectIfNoToken() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        checkToken();

    }

    public void wakeUpDeviceScreen() throws RemoteException {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.wakeUp();
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(new ActivityLifecycleCallback() {
            @Override
            public void onActivityLifecycleChanged(Activity activity, Stage stage) {
                //if (stage == Stage.PRE_ON_CREATE) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // }
            }
        });
    }

    // Set up and lunch activity for returning shopper checkout flow
    public void returningShopperSetUp(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isExistingCard) throws BSPaymentRequestException, InterruptedException, JSONException {
        setExistingCard(isExistingCard);
        purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();

        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        setSdk(sdkRequest, shopperCheckoutRequirements);
        setupAndLaunch(sdkRequest, true, vaultedShopperId);
    }

    // Create new vaulted shopper
    public void createVaultedShopper(boolean withCreditCard) throws JSONException {
        createVaultedShopperService(withCreditCard, new CreateVaultedShopperInterface() {
            @Override
            public void onServiceSuccess() throws JSONException {
                JSONObject jsonObject = new JSONObject(createVaultedShopperResponse);
                vaultedShopperId = getOptionalString(jsonObject, "vaultedShopperId");
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot create shopper from merchant server");
            }
        });
    }

    // Make a vaulted shopper API call
    private void createVaultedShopperService(boolean withCreditCard, final CreateVaultedShopperInterface createVaultedShopper) throws JSONException {
        JSONObject body = withCreditCard ? createVaultedShopperWithCreditCardDataObject() : createBasicVaultedShopperDataObject();
        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER, body.toString(), "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            createVaultedShopperResponse = response.getResponseString();
            createVaultedShopper.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            createVaultedShopper.onServiceFailure();
        }
    }

    // Create JSONObject for a vaulted shopper with only first and last name and email
    private JSONObject createBasicVaultedShopperDataObject() throws JSONException {
        JSONObject postData = new JSONObject();

        postData.put("firstName", "Fanny");
        postData.put("lastName", "Brice");
        postData.put("email", "some@mail.com");

        return postData;
    }

    // Create JSONObject for a vaulted shopper with credit card info
    private JSONObject createVaultedShopperWithCreditCardDataObject() throws JSONException {
        JSONObject postData = new JSONObject();

        JSONObject jsonObjectCreditCard = new JSONObject();
        jsonObjectCreditCard.put("expirationYear", TestingShopperCreditCard.VISA_CREDIT_CARD.getExpirationYear());
        jsonObjectCreditCard.put("securityCode", Integer.parseInt(TestingShopperCreditCard.VISA_CREDIT_CARD.getCvv()));
        jsonObjectCreditCard.put("expirationMonth", Integer.toString(TestingShopperCreditCard.VISA_CREDIT_CARD.getExpirationMonth()));
        jsonObjectCreditCard.put("cardNumber", Long.parseLong(TestingShopperCreditCard.VISA_CREDIT_CARD.getCardNumber()));

        JSONObject jsonObjectFirstElement = new JSONObject();
        jsonObjectFirstElement.put("creditCard", jsonObjectCreditCard);

        JSONArray jsonArrayCreditCardInfo = new JSONArray();
        jsonArrayCreditCardInfo.put(jsonObjectFirstElement);

        JSONObject jsonObjectPaymentSources = new JSONObject();
        jsonObjectPaymentSources.put("creditCardInfo", jsonArrayCreditCardInfo);

        postData.put("paymentSources", jsonObjectPaymentSources);

        postData.put("firstName", "Fanny");
        postData.put("lastName", "Brice");
        postData.put("email", "some@mail.com");

        return postData;
    }

    // Verify that the checkout activity ends with the correct result code
    // Verify that the amount and currency in sdkResult are right
    public void finishDemoPurchase(TestingShopperCheckoutRequirements shopperCheckoutRequirements) throws InterruptedException {
        sdkResult = blueSnapService.getSdkResult();

        while (!mActivity.isDestroyed()) {
            Log.d(TAG, "Waiting for tokenized credit card service to finish");
            sleep(1000);
        }

        // Verify activity ended with success
        checkResultOk(BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK);

        sDKConfiguration = BlueSnapService.getInstance().getsDKConfiguration();

        // verify that both currency symbol and purchase amount received by sdkResult matches those we actually chose
        assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - purchaseAmount) < 0.0000000001);
        Assert.assertEquals("SDKResult wrong currency", sdkResult.getCurrencyNameCode(), checkoutCurrency);

        makeCheckoutTransaction(sdkResult, shopperCheckoutRequirements);
    }

    // Make a credit card transaction for checkout flow and validate the shopper details in server
    private void makeCheckoutTransaction(SdkResult sdkResult, TestingShopperCheckoutRequirements shopperCheckoutRequirements) {
        transactions = DemoTransactions.getInstance();
        transactions.setContext(applicationContext);
        transactions.createCreditCardTransaction(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                vaultedShopperId = transactions.getShopperId();
                get_shopper_from_server(shopperCheckoutRequirements);
            }

            @Override
            public void onFailure() {
                fail("Failed to make a transaction");
            }
        });
    }

    // Make a credit card transaction for create payment flow and validate the charged card
    public void makeCreatePaymentTransaction() throws InterruptedException {
        while (!mActivity.isDestroyed()) {
            Log.d(TAG, "Waiting for tokenized credit card service to finish");
            sleep(1000);
        }

        //Verify activity ended with success
        checkResultOk(BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK);

        sdkResult = blueSnapService.getSdkResult();

        transactions = DemoTransactions.getInstance();
        transactions.setContext(applicationContext);
        transactions.createCreditCardTransaction(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                Assert.assertEquals("SDKResult wrong credit card was charged", transactions.getCardLastFourDigits(), TestingShopperCreditCard.VISA_CREDIT_CARD.getCardLastFourDigits());
            }

            @Override
            public void onFailure() {
                fail("Failed to make a transaction");
            }
        });
    }

    // Make a retrieve transaction API call
    public void retrieveTransaction(String transactionId) throws JSONException {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_TRANSACTION + transactionId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            transactionDetailsValidation(response.getResponseString());
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            fail("Cannot retrieve transaction from merchant server");
        }
    }

    private void transactionDetailsValidation(String retrieveTransactionResponse) {
        try {
            JSONObject jsonObject = new JSONObject(retrieveTransactionResponse);

            check_if_field_identify("amount", TestUtils.getDecimalFormat().format(purchaseAmount), jsonObject);
            check_if_field_identify("currency", checkoutCurrency, jsonObject);

            JSONObject jsonObjectProcessingInfo = jsonObject.getJSONObject("processingInfo");
            check_if_field_identify("processingStatus", "SUCCESS", jsonObjectProcessingInfo);

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse transaction info");
        }
    }

    // googlePay and payPal validation
    public void chosenPaymentMethodValidationInServer(boolean isGooglePay) throws InterruptedException {
        chosenPaymentMethodValidationInServer(null, null, isGooglePay);
    }

    // credit card, googlePay and payPal validation
    public void chosenPaymentMethodValidationInServer(TestingShopperCheckoutRequirements shopperCheckoutRequirements,
                                                      TestingShopperCreditCard creditCard, boolean isGooglePay) throws InterruptedException {
        if (!isGooglePay) { // Espresso test, wait for activity to finish
            while (!mActivity.isDestroyed()) {
                Log.d(TAG, "Waiting for tokenized credit card service to finish");
                sleep(1000);
            }

            //Verify activity ended with success
            checkResultOk(BluesnapChoosePaymentMethodActivity.BS_CHOOSE_PAYMENT_METHOD_RESULT_OK);
        }

        if (creditCard != null) //chosen cc payment method
            get_shopper_from_server(shopperCheckoutRequirements, true, false, creditCard);
        else //chosen payPal or googlePay payment method
            get_shopper_from_server(null, false, isGooglePay, null);

    }

    // Validation for credit card transactions (in regular checkout)
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements) {
        get_shopper_from_server(shopperCheckoutRequirements, false, true, false, null, null);
    }

    // Validation for googlePay transactions
    public void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperContactInfo contactInfo) {
        get_shopper_from_server(shopperCheckoutRequirements, false, true, true, null, contactInfo);
    }

    // Validation for chosen payment method (cc, payPal, googlePay)
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean forInfoSaved, boolean isGooglePay,
                                         TestingShopperCreditCard creditCard) {
        get_shopper_from_server(shopperCheckoutRequirements, true, forInfoSaved, isGooglePay, creditCard, null);
    }

    // Validate vaulted shopper info in server
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isShopperConfig, boolean isPayment, boolean isGooglePay,
                                         TestingShopperCreditCard creditCard, TestingShopperContactInfo contactInfo) {
        get_shopper_service(new GetShopperServiceInterface() {
            @Override
            public void onServiceSuccess() {
                if (isShopperConfig) { // shopperConfig validation
                    String chosenPaymentMethodType;
                    if (creditCard != null) // Credit card payment method
                        chosenPaymentMethodType = "CC";
                    else // PayPal and GooglePay payment methods
                        chosenPaymentMethodType = isGooglePay ? "GOOGLE_PAY" : "PAYPAL";
                    shopper_chosen_payment_method_validation(creditCard, chosenPaymentMethodType);
                }
                if (isPayment) { // payment validation
//                    String cardLastFourDigits = (creditCard == null) ? "" : creditCard.getCardLastFourDigits();
                    shopper_payment_info_saved_in_server_validation(shopperCheckoutRequirements, contactInfo, isGooglePay);
                }
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot obtain shopper info from merchant server");
            }
        });
    }

    // Make a retrieve vaulted shopper API call
    private void get_shopper_service(final GetShopperServiceInterface getShopperServiceInterface) {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER + "/" + vaultedShopperId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            getShopperResponse = response.getResponseString();
            getShopperServiceInterface.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            getShopperServiceInterface.onServiceFailure();
        }
    }

    // Validate chosen payment method in server
    private void shopper_chosen_payment_method_validation(TestingShopperCreditCard creditCard, String chosenPaymentMethodType) {
        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);

            JSONObject jsonObjectChosenPaymentMethod = jsonObject.getJSONObject("chosenPaymentMethod");
            check_if_field_identify("chosenPaymentMethodType", chosenPaymentMethodType, jsonObjectChosenPaymentMethod);

            if (creditCard != null) {
                JSONObject jsonObjectCreditCard = jsonObjectChosenPaymentMethod.getJSONObject("creditCard");
                check_if_field_identify("cardLastFourDigits", creditCard.getCardLastFourDigits(), jsonObjectCreditCard);
                check_if_field_identify("cardType", creditCard.getCardType(), jsonObjectCreditCard);
                check_if_field_identify("expirationMonth", Integer.toString(creditCard.getExpirationMonth()), jsonObjectCreditCard);
                check_if_field_identify("expirationYear", Integer.toString(creditCard.getExpirationYear()), jsonObjectCreditCard);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    //TODO: add validation that the new credit card info has been saved correctly
    // Validate payment info, Billing and shipping, in server
    private void shopper_payment_info_saved_in_server_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperContactInfo contactInfo, boolean googlePay) {
//        int cardIndex = 0;
        JSONObject jsonObjectBillingContactInfo;

        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);
            if (shopperCheckoutRequirements.isEmailRequired())
                emailFromServer = getOptionalString(jsonObject, "email");

            if (!googlePay) { // cc payment- parse billing info
                JSONObject jsonObjectPaymentSources = jsonObject.getJSONObject("paymentSources");
                JSONArray creditCardInfoJsonArray = jsonObjectPaymentSources.getJSONArray("creditCardInfo");

//                if (!cardLastFourDigits.isEmpty()) {
//                    String firstCard = getOptionalString(creditCardInfoJsonArray.getJSONObject(0).getJSONObject("creditCard"), "cardLastFourDigits");
//                    cardIndex = firstCard.equals(cardLastFourDigits) ? 0 : 1;
//                }

                jsonObjectBillingContactInfo = creditCardInfoJsonArray.getJSONObject(0).getJSONObject("billingContactInfo");

                // billing info validation
                shopper_component_info_saved_validation(shopperCheckoutRequirements, true, jsonObjectBillingContactInfo, contactInfo);

            }

            // shipping info validation
            if (shopperCheckoutRequirements.isShippingRequired())
                shopper_component_info_saved_validation(shopperCheckoutRequirements, false, jsonObject.getJSONObject("shippingContactInfo"), contactInfo);

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    // Validate shopper info in server. if isBillingInfo is true then validate billing info,
    // o.w. validate shipping info.
    private void shopper_component_info_saved_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isBillingInfo, JSONObject jsonObject,
                                                         TestingShopperContactInfo _contactInfo) {
        String countryKey;
        String address = "address1";
        TestingShopperContactInfo contactInfo;

        if (_contactInfo != null) { // GooglePay contact info
            contactInfo = _contactInfo;
        } else { // Credit card contact info
            if (!isExistingCard) { // New shopper
                contactInfo = (!isBillingInfo && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? ContactInfoTesterCommon.shippingContactInfo : ContactInfoTesterCommon.billingContactInfo;
            } else { // Returning shopper
                contactInfo = (isBillingInfo) ? ContactInfoTesterCommon.editBillingContactInfo : ContactInfoTesterCommon.editShippingContactInfo;
            }
        }

        countryKey = contactInfo.getCountryKey();

        check_if_field_identify("country", countryKey.toLowerCase(), jsonObject);

        check_if_field_identify("firstName", contactInfo.getFirstName(), jsonObject);
        check_if_field_identify("lastName", contactInfo.getLastName(), jsonObject);

        if (isBillingInfo && shopperCheckoutRequirements.isEmailRequired())
            check_if_field_identify("email", contactInfo.getEmail(), jsonObject);

        if (TestUtils.checkCountryHasZip(countryKey))
            check_if_field_identify("zip", contactInfo.getZip(), jsonObject);

        if (!isBillingInfo || shopperCheckoutRequirements.isFullBillingRequired()) { //full info or shipping
            if (_contactInfo != null)
                check_if_field_identify("state", "MA", jsonObject);

            else if (countryKey.equals("US") || countryKey.equals("CA") || countryKey.equals("BR")) {
                check_if_field_identify("state", contactInfo.getState(), jsonObject);
            }
            check_if_field_identify("city", contactInfo.getCity(), jsonObject);
            check_if_field_identify(address, contactInfo.getAddress(), jsonObject);
        }
    }

    // Parse json fields and compare to the parameter "expectedResult"
    private void check_if_field_identify(String fieldName, String expectedResult, JSONObject shopperInfoJsonObject) {
        String fieldContent = null;
        try {
            if (fieldName.equals("email"))
                fieldContent = emailFromServer.substring(0, emailFromServer.indexOf("&")) + "@" + emailFromServer.substring(emailFromServer.indexOf(";") + 1);
            else
                fieldContent = getOptionalString(shopperInfoJsonObject, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
        }

        if (fieldName.equals("amount")) // ignoring format differences (such as number of zeros after decimal point), comparing numeric values only
            Assert.assertTrue(fieldName + " was not saved correctly in DataBase for shopper: " + vaultedShopperId, Double.parseDouble(expectedResult) - Double.parseDouble(fieldContent) == 0);

        else
            Assert.assertEquals(fieldName + " was not saved correctly in DataBase for shopper: " + vaultedShopperId, expectedResult, fieldContent);
    }

    // Verify that the activity returned with the correct result code
    private void checkResultOk(int expectedResultCode) {
        try {
            Field f = Activity.class.getDeclaredField("mResultCode"); //NoSuchFieldException
            f.setAccessible(true);
            int mResultCode = f.getInt(mActivityRule.getActivity());
            assertEquals("The result code from activity: " + Activity.class.getName() + " is not correct. ", mResultCode, expectedResultCode);
        } catch (Exception e) {
            fail();
        }
    }
}
