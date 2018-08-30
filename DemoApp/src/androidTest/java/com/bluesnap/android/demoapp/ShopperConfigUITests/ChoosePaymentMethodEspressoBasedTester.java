package com.bluesnap.android.demoapp.ShopperConfigUITests;

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

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShoppersFactory;
import com.bluesnap.android.demoapp.GetShopperServiceInterface;
import com.bluesnap.android.demoapp.RandomTestValuesGenerator;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.android.demoapp.TokenServiceInterface;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;

import java.io.IOException;
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
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by sivani on 30/08/2018.
 */

public class ChoosePaymentMethodEspressoBasedTester {
    protected static final String TAG = CheckoutEspressoBasedTester.class.getSimpleName();
    protected BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private SDKConfiguration sDKConfiguration = null;
    //    protected NumberFormat df;
    protected RandomTestValuesGenerator randomTestValuesGenerator = new RandomTestValuesGenerator();

    protected String defaultCountryKey;
    protected String defaultCountryValue;
    protected String checkoutCurrency = "USD";
    protected double purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
    //    protected double roundedPurchaseAmount = TestUtils.round_amount(purchaseAmount);
    private double taxPercent = randomTestValuesGenerator.randomTaxPercentage() / 100;
    protected double taxAmount = purchaseAmount * taxPercent;

    protected ReturningShoppersFactory.TestingShopper returningShopper;

    private boolean isSdkRequestNull = false;

    private static final String SANDBOX_GET_SHOPPER = "vaulted-shoppers/";
    protected String shopperId;
    private String getShopperResponse;

    private HttpURLConnection myURLConnection;
    private String merchantToken;

    public Context applicationContext;

    private List<CustomHTTPParams> sahdboxHttpHeaders = getHttpParamsForSandboxTests();

    ChoosePaymentMethodEspressoBasedTester(String returningShopperId) {
        setUrlConnection("?shopperId=" + returningShopperId);
    }

    private void setUrlConnection(String returningOrNewShopper) {
        try {
            URL myURL = new URL(SANDBOX_URL + SANDBOX_TOKEN_CREATION + returningOrNewShopper);
            myURLConnection = (HttpURLConnection) myURL.openConnection();
        } catch (IOException e) {
            fail("Network error open server connection:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Rule
    public ActivityTestRule<BluesnapChoosePaymentMethodActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapChoosePaymentMethodActivity.class, false, false);

    private BluesnapCheckoutActivity mActivity;

    //    @Before
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
        setupAndLaunch(sdkRequest, "USD");
    }

    public void setupAndLaunch(SdkRequestBase sdkRequest, String merchantStoreCurrency) throws InterruptedException, BSPaymentRequestException {
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
    List<CustomHTTPParams> getHttpParamsForSandboxTests() {
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

    public void chosenPaymentMethodValidationInServer(TestingShopperCreditCard creditCard) throws InterruptedException {
        while (!mActivity.isDestroyed()) {
            Log.d(TAG, "Waiting for tokenized credit card service to finish");
            sleep(1000);
        }

        get_shopper_from_server(creditCard);
    }

    private void get_shopper_from_server(TestingShopperCreditCard creditCard) {
        get_shopper_service(new GetShopperServiceInterface() {
            @Override
            public void onServiceSuccess() {
                shopper_chosen_payment_method_validation(creditCard);
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot obtain shopper info from merchant server");
            }
        });
    }

    private void get_shopper_service(final GetShopperServiceInterface getShopperServiceInterface) {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_GET_SHOPPER + shopperId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            getShopperResponse = response.getResponseString();
            getShopperServiceInterface.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            getShopperServiceInterface.onServiceFailure();
        }
    }

    private void shopper_chosen_payment_method_validation(TestingShopperCreditCard creditCard) {
        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);

            JSONObject jsonObjectChosenPaymentMethod = jsonObject.getJSONObject("chosenPaymentMethod");
            check_if_field_identify("chosenPaymentMethodType", "CC", jsonObjectChosenPaymentMethod);

            JSONObject jsonObjectCreditCard = jsonObjectChosenPaymentMethod.getJSONObject("creditCard");
            check_if_field_identify("cardLastFourDigits", creditCard.getCardLastFourDigits(), jsonObjectCreditCard);
            check_if_field_identify("cardType", creditCard.getCardType(), jsonObjectCreditCard);
//            check_if_field_identify("cardSubType", creditCard.getCardSubType(), jsonObjectCreditCard);
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

    private void check_if_field_identify(String fieldName, String expectedResult, JSONObject shopperInfoJsonObject) {
        String fieldContent = null;
        try {
            fieldContent = getOptionalString(shopperInfoJsonObject, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
        }

        Assert.assertEquals(fieldName + " was not saved correctly in DataBase", expectedResult, fieldContent);
    }

}
