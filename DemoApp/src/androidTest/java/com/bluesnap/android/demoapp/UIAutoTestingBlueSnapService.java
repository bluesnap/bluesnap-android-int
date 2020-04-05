package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleCallback;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.UiDevice;
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
import com.bluesnap.androidapi.services.CardinalManager;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PLAN;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_SUBSCRIPTION;
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
    protected double purchaseAmount = Double.parseDouble(TestUtils.getDecimalFormat().format(randomTestValuesGenerator.randomDemoAppPrice()));
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
                        BlueSnapService.getInstance().setup(merchantToken, tokenProvider, merchantStoreCurrency, InstrumentationRegistry.getInstrumentation().getContext(), new BluesnapServiceCallback() {
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
//        purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();

        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        setSdk(sdkRequest, shopperCheckoutRequirements);
        setupAndLaunch(sdkRequest, true, vaultedShopperId);
    }

    public void createVaultedShopper(boolean withDefaultCreditCard) throws JSONException {
        createVaultedShopper(withDefaultCreditCard ? TestingShopperCreditCard.VISA_CREDIT_CARD : null, false);
    }

    public void createVaultedShopper(boolean withDefaultCreditCard, boolean withShipping) throws JSONException {
        createVaultedShopper(withDefaultCreditCard ? TestingShopperCreditCard.VISA_CREDIT_CARD : null, withShipping);
    }

    public void createVaultedShopper(TestingShopperCreditCard creditCard) throws JSONException {
        createVaultedShopper(creditCard, false);
    }

    // Create new vaulted shopper
    public void createVaultedShopper(TestingShopperCreditCard creditCard, boolean withShipping) throws JSONException {
        JSONObject body = createVaultedShopperDataObject(creditCard, withShipping);
        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER, body.toString(), "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            createVaultedShopperResponse = response.getResponseString();
            JSONObject jsonObject = new JSONObject(createVaultedShopperResponse);
            vaultedShopperId = getOptionalString(jsonObject, "vaultedShopperId");
        } else {
            Log.e(TAG, "createVaultedShopperService API error: " + response);
            fail("Cannot create shopper from merchant server");
        }
    }

    // Create JSONObject for a vaulted shopper with optionals credit card and shipping info
    private JSONObject createVaultedShopperDataObject(TestingShopperCreditCard creditCard, boolean withShipping) throws JSONException {
        JSONObject postData = new JSONObject();

        postData.put("firstName", "Fanny");
        postData.put("lastName", "Brice");
        postData.put("email", "some@mail.com");

        if (creditCard != null) {
            JSONObject jsonObjectCreditCard = new JSONObject();
            jsonObjectCreditCard.put("expirationYear", creditCard.getExpirationYear());
            jsonObjectCreditCard.put("securityCode", Integer.parseInt(creditCard.getCvv()));
            jsonObjectCreditCard.put("expirationMonth", Integer.toString(creditCard.getExpirationMonth()));
            jsonObjectCreditCard.put("cardNumber", Long.parseLong(creditCard.getCardNumber()));

            JSONObject jsonObjectFirstElement = new JSONObject();
            jsonObjectFirstElement.put("creditCard", jsonObjectCreditCard);

            JSONArray jsonArrayCreditCardInfo = new JSONArray();
            jsonArrayCreditCardInfo.put(jsonObjectFirstElement);

            JSONObject jsonObjectPaymentSources = new JSONObject();
            jsonObjectPaymentSources.put("creditCardInfo", jsonArrayCreditCardInfo);

            postData.put("paymentSources", jsonObjectPaymentSources);
        }

        if (withShipping){
            JSONObject jsonObjectShippingContactInfo = new JSONObject();
            jsonObjectShippingContactInfo.put("firstName", ContactInfoTesterCommon.shippingContactInfo.getFirstName());
            jsonObjectShippingContactInfo.put("lastName", ContactInfoTesterCommon.shippingContactInfo.getLastName());
            jsonObjectShippingContactInfo.put("address1", ContactInfoTesterCommon.shippingContactInfo.getAddress());
            jsonObjectShippingContactInfo.put("city", ContactInfoTesterCommon.shippingContactInfo.getCity());
            jsonObjectShippingContactInfo.put("state", ContactInfoTesterCommon.shippingContactInfo.getState());
            jsonObjectShippingContactInfo.put("zip", ContactInfoTesterCommon.shippingContactInfo.getZip());
            jsonObjectShippingContactInfo.put("country", ContactInfoTesterCommon.shippingContactInfo.getCountryKey().toLowerCase());


            postData.put("shippingContactInfo", jsonObjectShippingContactInfo);

        }

        return postData;
    }

    public void finishDemoPurchase(TestingShopperCheckoutRequirements shopperCheckoutRequirements) throws InterruptedException {
        finishDemoPurchase(shopperCheckoutRequirements, false, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_UNAVAILABLE.name(), true);
    }

    public void finishDemoPurchase(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean cardStored) throws InterruptedException {
        finishDemoPurchase(shopperCheckoutRequirements, cardStored, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_UNAVAILABLE.name(), true);
    }

    // for 3DS flows
    public void finishDemoPurchase(TestingShopperCheckoutRequirements shopperCheckoutRequirements, String expected3DSResult, boolean isResultOK) throws InterruptedException {
        finishDemoPurchase(shopperCheckoutRequirements, false, expected3DSResult, isResultOK);
    }

    // Verify that the checkout activity ends with the correct result code
    // Verify that the amount and currency in sdkResult are right
    public void finishDemoPurchase(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean cardStored, String expected3DSResult, boolean isResultOK) throws InterruptedException {

        while (!mActivity.isDestroyed()) {
            Log.d(TAG, "Waiting for tokenized credit card service to finish");
            sleep(1000);
        }

        // Verify activity ended with success
        checkResultOk(BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK, isResultOK);

        sDKConfiguration = BlueSnapService.getInstance().getsDKConfiguration();

        checkSDKResult(expected3DSResult, isResultOK);

        if (isResultOK) {
            makeCheckoutTransaction(shopperCheckoutRequirements, cardStored);
        }
    }

    private void checkSDKResult(String expected3DSResult, boolean isResultOK) {
        if (isResultOK) {
            sdkResult = blueSnapService.getSdkResult();
            // verify that both currency symbol and purchase amount received by sdkResult matches those we actually chose
            assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - purchaseAmount) < 0.0000000001);
            assertEquals("SDKResult wrong currency", checkoutCurrency, sdkResult.getCurrencyNameCode());

            assertEquals("SDKResult wrong 3DSResult", expected3DSResult, sdkResult.getThreeDSAuthenticationResult());
        }
    }

    // Make a credit card transaction for checkout flow and validate the shopper details in server
    private void makeCheckoutTransaction(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean cardStored) {
        transactions = DemoTransactions.getInstance();
        transactions.setContext(applicationContext);
        transactions.createCreditCardTransaction(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                vaultedShopperId = transactions.getShopperId();
                get_shopper_from_server(shopperCheckoutRequirements, cardStored);
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
                assertEquals("SDKResult wrong credit card was charged", transactions.getCardLastFourDigits(), TestingShopperCreditCard.VISA_CREDIT_CARD.getCardLastFourDigits());
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

    // Make a Create Subscription Plan API call
    public String createSubscriptionPlan() throws JSONException {
        String planId = "";
        JSONObject body = createBasicSubscriptionPlanDataObject();
        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_PLAN, body.toString(), "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            JSONObject jsonObject = new JSONObject(response.getResponseString());
            planId = getOptionalString(jsonObject, "planId");
        } else {
            Log.e(TAG, "createVaultedShopperService API error: " + response);
            fail("Cannot create subscription plan from merchant server");
        }

        return planId;

    }

    // Create JSONObject for a Subscription Plan
    private JSONObject createBasicSubscriptionPlanDataObject() throws JSONException {
        JSONObject postData = new JSONObject();

        postData.put("chargeFrequency", "MONTHLY");
        postData.put("name", "Gold Plan");
        postData.put("currency", checkoutCurrency);
        postData.put("recurringChargeAmount", purchaseAmount);

        return postData;
    }

    // Make a Create Subscription Charge API call
    public void createSubscriptionCharge(String planId, TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperCreditCard creditCard) throws JSONException {
        JSONObject body = createBasicSubscriptionChargeDataObject(planId);
        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_SUBSCRIPTION, body.toString(), "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            JSONObject jsonObject = new JSONObject(response.getResponseString());
            subscription_shopper_payment_info_saved_in_server_validation(shopperCheckoutRequirements, creditCard, response.getResponseString());
        } else {
            Log.e(TAG, "createVaultedShopperService API error: " + response);
            fail("Cannot create subscription charge from merchant server");
        }
    }

    // Create JSONObject for a Subscription Charge
    private JSONObject createBasicSubscriptionChargeDataObject(String planId) throws JSONException {
        JSONObject postData = new JSONObject();

        postData.put("planId", planId);

        JSONObject jsonObjectPaymentSources = new JSONObject();
        jsonObjectPaymentSources.put("pfToken", merchantToken);
        postData.put("paymentSource", jsonObjectPaymentSources);


        return postData;
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
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean cardStored) {
        get_shopper_from_server(shopperCheckoutRequirements, false, true, false, null, null, cardStored);
    }

    // Validation for googlePay transactions
    public void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperContactInfo contactInfo) {
        get_shopper_from_server(shopperCheckoutRequirements, false, true, true, null, contactInfo, false);
    }

    // Validation for chosen payment method (cc, payPal, googlePay)
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isPayment, boolean isGooglePay,
                                         TestingShopperCreditCard creditCard) {
        get_shopper_from_server(shopperCheckoutRequirements, true, isPayment, isGooglePay, creditCard, null, true);
    }

    // Validate vaulted shopper info in server
    private void get_shopper_from_server(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isShopperConfig, boolean isPayment, boolean isGooglePay,
                                         TestingShopperCreditCard creditCard, TestingShopperContactInfo contactInfo, boolean cardStored) {
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
                    shopper_payment_info_saved_in_server_validation(shopperCheckoutRequirements, contactInfo, isGooglePay, cardStored);
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
                credit_card_validation(jsonObjectChosenPaymentMethod, creditCard);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    // Validate credit card in server
    private void credit_card_validation(JSONObject jsonObject, TestingShopperCreditCard creditCard) {
        try {
            JSONObject jsonObjectCreditCard = jsonObject.getJSONObject("creditCard");
            check_if_field_identify("cardLastFourDigits", creditCard.getCardLastFourDigits(), jsonObjectCreditCard);
            check_if_field_identify("cardType", creditCard.getCardType(), jsonObjectCreditCard);
            check_if_field_identify("expirationMonth", Integer.toString(creditCard.getExpirationMonth()), jsonObjectCreditCard);
            check_if_field_identify("expirationYear", Integer.toString(creditCard.getExpirationYear()), jsonObjectCreditCard);

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    private void subscription_shopper_payment_info_saved_in_server_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperCreditCard creditCard, String serverResponse) {

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);

            // validate subscription is active
            check_if_field_identify("status", "ACTIVE", jsonObject);

            JSONObject jsonObjectPaymentSource = jsonObject.getJSONObject("paymentSource");
            JSONObject jsonObjectCreditCardInfo = jsonObjectPaymentSource.getJSONObject("creditCardInfo");

            JSONObject jsonObjectBillingContactInfo = jsonObjectCreditCardInfo.getJSONObject("billingContactInfo");

            // billing info validation
            shopper_component_info_saved_validation(shopperCheckoutRequirements, true, jsonObjectBillingContactInfo, null, true);

            // credit card validation
            credit_card_validation(jsonObjectCreditCardInfo, creditCard);

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info from server response:\n" + serverResponse);
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing fields in server response:\n" + serverResponse);
        }

    }

    //TODO: add validation that the new credit card info has been saved correctly
    // Validate payment info, Billing and shipping, in server
    private void shopper_payment_info_saved_in_server_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, TestingShopperContactInfo contactInfo, boolean googlePay, boolean cardStored) {
//        int cardIndex = 0;
        JSONObject jsonObjectBillingContactInfo;

        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);
            if (shopperCheckoutRequirements.isEmailRequired())
                emailFromServer = getOptionalString(jsonObject, "email");

            if (!googlePay) { // cc payment- parse creditCardInfo

                JSONObject jsonObjectPaymentSources = jsonObject.getJSONObject("paymentSources");

                try {
                    JSONArray creditCardInfoJsonArray = jsonObjectPaymentSources.getJSONArray("creditCardInfo");

                    if (!cardStored) {
                        fail("Error on Retrieve vaulted shopper- 'creditCardInfo' exists when shopper selected DO NOT store");
                    }

//                    if (!cardLastFourDigits.isEmpty()) {
//                        String firstCard = getOptionalString(creditCardInfoJsonArray.getJSONObject(0).getJSONObject("creditCard"), "cardLastFourDigits");
//                        cardIndex = firstCard.equals(cardLastFourDigits) ? 0 : 1;
//                    }

                    jsonObjectBillingContactInfo = creditCardInfoJsonArray.getJSONObject(0).getJSONObject("billingContactInfo");

                    // billing info validation
                    shopper_component_info_saved_validation(shopperCheckoutRequirements, true, jsonObjectBillingContactInfo, contactInfo);

                } catch (JSONException e) {
                    if (cardStored) {
                        e.printStackTrace();
                        fail("Error parsing BS result on Retrieve vaulted shopper- Missing 'creditCardInfo'");
                    }
                }

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

    private void shopper_component_info_saved_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isBillingInfo, JSONObject jsonObject,
                                                         TestingShopperContactInfo _contactInfo) {
        shopper_component_info_saved_validation(shopperCheckoutRequirements, isBillingInfo, jsonObject, _contactInfo, false);

    }

    // Validate shopper info in server. if isBillingInfo is true then validate billing info,
    // o.w. validate shipping info.
    private void shopper_component_info_saved_validation(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean isBillingInfo, JSONObject jsonObject,
                                                         TestingShopperContactInfo _contactInfo, Boolean isSubscription) {
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

        if (isBillingInfo && shopperCheckoutRequirements.isEmailRequired() && !isSubscription)
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
                fieldContent = emailFromServer;

            else
                fieldContent = getOptionalString(shopperInfoJsonObject, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
        }

        if (fieldName.equals("amount")) // ignoring format differences (such as number of zeros after decimal point), comparing numeric values only
            assertTrue(fieldName + " was not saved correctly in DataBase for shopper: " + vaultedShopperId, Double.parseDouble(expectedResult) - Double.parseDouble(fieldContent) == 0);

        else
            assertEquals(fieldName + " was not saved correctly in DataBase for shopper: " + vaultedShopperId, expectedResult, fieldContent);
    }

    // Verify that the activity returned with the correct result code
    private void checkResultOk(int expectedResultCode) {
        checkResultOk(expectedResultCode, true);
    }

    private void checkResultOk(int expectedResultCode, boolean isResultOK) {
        try {
            Field f = Activity.class.getDeclaredField("mResultCode"); //NoSuchFieldException
            f.setAccessible(true);
            int mResultCode = f.getInt(mActivityRule.getActivity());
            int expectedActivityResultCode = isResultOK ? Activity.RESULT_OK : BluesnapCheckoutActivity.RESULT_SDK_FAILED;
            assertEquals("The result code from activity: " + Activity.class.getName() + " is not correct. ", expectedActivityResultCode, mResultCode);
        } catch (Exception e) {
            fail();
        }

        assertEquals("The result code from SDK is not correct. ", expectedResultCode, blueSnapService.getSdkResult().getResult());

    }
}
