package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests;

import android.support.test.espresso.web.webdriver.DriverAtoms;
import android.support.test.espresso.web.webdriver.Locator;
import android.util.Log;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.model.Atoms.getCurrentUrl;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
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
    private final String SANDBOX_PAYPAL_EMAIL = "apiShopper@bluesnap.com";
    private final String SANDBOX_PAYPAL_PASSWORD = "Plimus123";
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
        loadPayPalWebView();
        loginToPayPal();
        submitPayPalPayment();

        SdkResult sdkResult = blueSnapService.getSdkResult();

        //wait for transaction to finish
        while ((payPalInvoiceId = sdkResult.getPaypalInvoiceId()) == null)
            sleep(5000);

        //verify transaction status
        retrievePayPalTransaction();
    }

    void loadPayPalWebView() throws InterruptedException {
        onView(withId(R.id.payPalButton)).perform(click());

        //wait for web to load
        sleep(20000);

        //verify that paypal url opened up
        onWebView().check(webMatches(getCurrentUrl(), containsString(Constants.getPaypalSandUrl())));
    }

    void loginToPayPal() throws InterruptedException {
        try {
//            onWebView()
//                    .check(webContent(hasElementWithId("email")));
            onWebView()
                    .withElement(findElement(Locator.ID, "email")) // similar to onView(withId(...))
                    .perform(clearElement())
                    .perform(DriverAtoms.webKeys(SANDBOX_PAYPAL_EMAIL))

                    .withElement(findElement(Locator.ID, "btnNext"))
                    .perform(webClick());

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

    void submitPayPalPayment() throws InterruptedException {
        onWebView()
                .withElement(findElement(Locator.ID, "confirmButtonTop"))
                .perform(webClick());
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

    void retrievePayPalTransaction() {
        retrievePayPalTransactionService(new RetrievePayPalTransactionInterface() {
            @Override
            public void onServiceSuccess() {
                getTransactionStatus();
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

    private void getTransactionStatus() {
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
