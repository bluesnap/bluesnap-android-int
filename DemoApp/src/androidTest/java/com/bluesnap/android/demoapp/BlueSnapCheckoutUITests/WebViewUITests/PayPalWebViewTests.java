package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests;

import androidx.test.espresso.web.webdriver.DriverAtoms;
import androidx.test.espresso.web.webdriver.Locator;
import android.util.Log;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.BuildConfig;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static java.lang.Thread.sleep;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by sivani on 23/08/2018.
 */
public class PayPalWebViewTests extends CheckoutEspressoBasedTester {
    private static final String TAG = PayPalWebViewTests.class.getSimpleName();
    private BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private List<CustomHTTPParams> sahdboxHttpHeaders = uIAutoTestingBlueSnapService.getHttpParamsForSandboxTests();

    private final String SANDBOX_RETRIEVE_PAYPAL_TRANSACTION = "alt-transactions/";
    private final String SANDBOX_PAYPAL_EMAIL = BuildConfig.BS_PAYPAL_EMAIL;
    private final String SANDBOX_PAYPAL_PASSWORD = BuildConfig.BS_PAYPAL_PASSWORD;
    private String payPalInvoiceId;
    private String retrieveTransactionResponse;

//    @Rule
//    public ActivityTestRule<WebViewActivity> mActivityRule =
//            new ActivityTestRule<WebViewActivity>(WebViewActivity.class,
//                    false, false) {
//                @Override
//                protected void afterActivityLaunched() {
//                    onWebView().forceJavascriptEnabled();
//                }
//            };

    protected void payPalCheckoutSetup() throws BSPaymentRequestException, InterruptedException, JSONException {
        payPalCheckoutSetup("USD", checkoutCurrency);
    }

    protected void payPalCheckoutSetup(String merchantStoreCurrency, String checkoutCurrency) throws BSPaymentRequestException, InterruptedException, JSONException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);
        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, merchantStoreCurrency);
    }

    public PayPalWebViewTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, false);
    }

    void payPalBasicTransaction() throws InterruptedException {
        payPalBasicTransaction(true, checkoutCurrency, purchaseAmount);
    }

    public void payPalBasicTransaction(boolean pressPayPalButton, String checkoutCurrency, double purchaseAmount) throws InterruptedException {
        loadPayPalWebView(pressPayPalButton);
        loginToPayPal();
        submitPayPalPayment();

        SdkResult sdkResult = blueSnapService.getSdkResult();

        //wait for transaction to finish
        while ((payPalInvoiceId = sdkResult.getPaypalInvoiceId()) == null)
            sleep(5000);

        //verify transaction status
        retrievePayPalTransaction(checkoutCurrency, purchaseAmount);
    }

    void loadPayPalWebView() throws InterruptedException {
        loadPayPalWebView(true);
    }

    void loadPayPalWebView(boolean pressPayPalButton) throws InterruptedException {
        if (pressPayPalButton)
            onView(withId(R.id.payPalButton)).perform(click());

        //wait for web to load
        sleep(20000);

        //verify that paypal url opened up
        onWebView().check(webMatches(getCurrentUrl(), containsString(Constants.getPaypalSandUrl())));
        onWebView().forceJavascriptEnabled();

    }

    public void loginToPayPal() throws InterruptedException {
        try {
//            onWebView()
//                    .check(webContent(hasElementWithId("email")));
            onWebView()
                    .withElement(findElement(Locator.ID, "email")) // similar to onView(withId(...))
                    .perform(clearElement())
                    .perform(DriverAtoms.webKeys(SANDBOX_PAYPAL_EMAIL))

                    .withElement(findElement(Locator.ID, "btnNext"))
                    .perform(webClick());
            sleep(6000);

        } catch (Exception e) {
            Log.d(TAG, "Email is already filled in");
        }

        try {
            onWebView()
                    .withElement(findElement(Locator.ID, "password"))
                    .perform(clearElement())
                    .perform(DriverAtoms.webKeys(SANDBOX_PAYPAL_PASSWORD)) // Similar to perform(click())

                    .withElement(findElement(Locator.ID, "btnLogin"))
                    .perform(webClick());

        } catch (Exception e) {
            Log.d(TAG, "Password is already filled in");
        }

        //wait for login
        sleep(30000);
    }

    public void submitPayPalPayment() throws InterruptedException {
//        onWebView()
//                .withElement(findElement(Locator.ID, "confirmButtonTop"))
//                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.CLASS_NAME, "continueButton"))
                .perform(webClick());

        try {
            onWebView()
                    .withElement(findElement(Locator.CLASS_NAME, "continueButton"))
                    .perform(webClick());

        } catch (Exception e) {
            Log.d(TAG, "There is no second continueButton");
        }
    }

    public void updateCurrencyAndAmountAfterConversion(String oldCurrencyCode, String newCurrencyCode) {
        checkoutCurrency = newCurrencyCode;
        if (!oldCurrencyCode.equals("USD")) {
            double conversionRateToUSD = blueSnapService.getsDKConfiguration().getRates().getCurrencyByCode(oldCurrencyCode).getConversionRate();
            purchaseAmount = purchaseAmount / conversionRateToUSD;
        }

        double conversionRateFromUSD = blueSnapService.getsDKConfiguration().getRates().getCurrencyByCode(newCurrencyCode).getConversionRate();
        uIAutoTestingBlueSnapService.setPurchaseAmount(uIAutoTestingBlueSnapService.getPurchaseAmount() / conversionRateFromUSD);
        purchaseAmount = purchaseAmount * conversionRateFromUSD;
    }

    public void retrievePayPalTransaction(String checkoutCurrency, double purchaseAmount) {
        retrievePayPalTransactionService(new RetrievePayPalTransactionInterface() {
            @Override
            public void onServiceSuccess() {
                getTransactionStatus(checkoutCurrency, purchaseAmount);
            }

            @Override
            public void onServiceFailure() {
                fail("Cannot obtain transaction status from merchant server");
            }
        });
    }

    private void retrievePayPalTransactionService(final RetrievePayPalTransactionInterface retrievePayPalTransaction) {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_RETRIEVE_PAYPAL_TRANSACTION + payPalInvoiceId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            retrieveTransactionResponse = response.getResponseString();
            retrievePayPalTransaction.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            retrievePayPalTransaction.onServiceFailure();
        }
    }

    private void getTransactionStatus(String checkoutCurrency, double purchaseAmount) {
        try {
            JSONObject jsonObject = new JSONObject(retrieveTransactionResponse);
            JSONObject jsonObjectProcessingInfo = jsonObject.getJSONObject("processingInfo");

            String transactionStatus = getOptionalString(jsonObjectProcessingInfo, "processingStatus");
            Assert.assertEquals("PayPal transaction failed!", "SUCCESS", transactionStatus);

            String transactionCurrency = getOptionalString(jsonObject, "currency");
            Assert.assertEquals("Wrong transaction amount", checkoutCurrency, transactionCurrency);

            String transactionAmount = getOptionalString(jsonObject, "amount");
            Assert.assertEquals("Wrong transaction amount", TestUtils.round_amount(purchaseAmount), TestUtils.round_amount(Double.parseDouble(transactionAmount)));


        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse transaction status");
        }
    }

}
