package com.bluesnap.android.demoapp;

import android.os.RemoteException;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static java.lang.Thread.sleep;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by sivani on 23/07/2018.
 */

public class BasicTransactionsFlows extends EspressoBasedTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);

    DemoMainActivity demoMainActivity;
    private static final String TAG = "NewShopperBasicFlow";
    boolean fullInfo = false;
    boolean withShipping = false;
    boolean withEmail = false;
    boolean shippingSameAsBilling = false;
    boolean isReturningShoppper = false;
    String emailFromServer;

    private static final String SANDBOX_GET_SHOPPER = "vaulted-shoppers/";
    private String shopperId;
    private String getShopperResponse;
    String billingCountryKey;
    String billingCountryValue;
    String shippingCountryKey;
    String shippingCountryValue;

    private static String[] returningShoppersIDs = new String[8]; //For returning shoppers transaction flow tests

    @After
    public void keepRunning() {
        mActivityRule.getActivity().finish();
        //Thread.sleep(1000);
    }

    @Before
    public void setup() {
        demoMainActivity = mActivityRule.getActivity();
        applicationContext = demoMainActivity.getApplicationContext();
        //defaultCountryKey = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }

        //TODO: restore this after the counties mapping fix
//        String[] billingCountry = randomTestValuesGenerator.randomReturningShopperCountry(applicationContext);
//        billingCountryKey = billingCountry[0];
//        billingCountryValue = billingCountry[1];
//
//        String[] shippingCountry = randomTestValuesGenerator.randomReturningShopperCountry(applicationContext);
//        shippingCountryKey = shippingCountry[0];
//        shippingCountryValue = shippingCountry[1];

        defaultCountryKey = BlueSnapService.getInstance().getUserCountry(applicationContext);
        String[] countryKeyArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_key_array);
        String[] countryValueArray = applicationContext.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_value_array);

        defaultCountryValue = countryValueArray[Arrays.asList(countryKeyArray).indexOf(defaultCountryKey)];
        billingCountryKey = shippingCountryKey = defaultCountryKey;
        billingCountryValue = shippingCountryValue = defaultCountryValue;
    }

    public static Matcher<Object> itemListMatcher(final Matcher<String> itemListText) {
        Checks.checkNotNull(itemListText);
        return new BoundedMatcher<Object, String>(String.class) {
            @Override
            public boolean matchesSafely(String item) {
                return itemListText.matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text: " + itemListText.toString());
                itemListText.describeTo(description);
            }
        };
    }

    public Double start_demo_purchase(int returningShopperIndex) {
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);

        checkToken();

        if (returningShopperIndex >= 0) {
            onView(withId(R.id.returningShopperEditText)).check(matches(isDisplayed())).perform(clearText(), typeText(returningShoppersIDs[returningShopperIndex]));
            try { //TODO: use the IdlingResources to get rid of this
                sleep(5000);
            } catch (Exception e) {
                fail();
            }
        }

        onView(withId(R.id.rateSpinner)).check(matches(isDisplayed())).perform(closeSoftKeyboard(), click());
        onData(allOf(is(instanceOf(String.class)), itemListMatcher(containsString(checkoutCurrency))))
                .perform(click());

        // onView(withId(R.id.rateSpinner)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(Double.toString(purchaseAmount)), ViewActions.closeSoftKeyboard());

        if (fullInfo)
            onView(withId(R.id.billingSwitch)).perform(swipeRight());

        if (withShipping)
            onView(withId(R.id.shippingSwitch)).perform(swipeRight());

        if (withEmail)
            onView(withId(R.id.emailSwitch)).perform(swipeRight());

        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        return purchaseAmount;
    }


    public void finish_demo_purchase(SdkResult sdkResult, int returningShopperIndex) {
        //wait for transaction to finish
        Espresso.registerIdlingResources(transactionMessageIR);
        IdlingPolicies.setIdlingResourceTimeout(120, TimeUnit.SECONDS);
        onView(withId(R.id.transactionResult)) //verify transaction success
                .check(matches(withText(containsString("Transaction Success"))));

        //TODO: change this stupid thing. in demoApp as well
        shopperId = TestUtils.getText(withId(R.id.shopperId)).substring(13);
        if (returningShopperIndex >= 0)
            returningShoppersIDs[returningShopperIndex] = shopperId;
        Espresso.unregisterIdlingResources(transactionMessageIR);
//        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

        //verify that both currency symbol and purchase amount received by sdkResult matches those we actually chose
        Assert.assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - purchaseAmount) < 0.00000000001);
        Assert.assertEquals("SDKResult wrong currency", sdkResult.getCurrencyNameCode(), checkoutCurrency);
    }

    void get_shopper_after_transaction() {
        get_shopper_service(new GetShopperServiceInterface() {
            @Override
            public void onServiceSuccess() {
                new_shopper_info_saved_validation();
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

    //TODO: add validation that the new credit card info has been saved correctly
    private void new_shopper_info_saved_validation() {
        try {
            JSONObject jsonObject = new JSONObject(getShopperResponse);
            if (withEmail)
                emailFromServer = getOptionalString(jsonObject, "email");

            JSONObject jsonObjectPaymentSources = jsonObject.getJSONObject("paymentSources");
            JSONArray creditCardInfoJsonArray = jsonObjectPaymentSources.getJSONArray("creditCardInfo");
            JSONObject jsonObjectBillingContactInfo = creditCardInfoJsonArray.getJSONObject(0).getJSONObject("billingContactInfo");

            new_shopper_component_info_saved_validation(true, jsonObjectBillingContactInfo);
            if (withShipping)
                new_shopper_component_info_saved_validation(false, jsonObject.getJSONObject("shippingContactInfo"));

        } catch (JSONException e) {
            e.printStackTrace();
            fail("Error on parse shopper info");
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: email" + "\n" + getShopperResponse);
        }
    }

    private void new_shopper_component_info_saved_validation(boolean isBillingInfo, JSONObject jsonObject) {
        String countryKey;
        TestingShopperContactInfo contactInfo;

        if (!isReturningShoppper) { //New shopper
            countryKey = (!isBillingInfo && !shippingSameAsBilling) ? shippingCountryKey : billingCountryKey;
            contactInfo = (!isBillingInfo && !shippingSameAsBilling) ? ContactInfoTesterCommon.shippingContactInfo : ContactInfoTesterCommon.billingContactInfo;
        } else { //Returning shopper
            countryKey = (isBillingInfo) ? "ca" : "us";
            contactInfo = (isBillingInfo) ? ContactInfoTesterCommon.editBillingContactInfo : ContactInfoTesterCommon.editShippingContactInfo;
        }

        check_if_field_identify("country", countryKey.toLowerCase(), jsonObject);

        check_if_field_identify("firstName", contactInfo.getFirstName(), jsonObject);
        check_if_field_identify("lastName", contactInfo.getLastName(), jsonObject);

        if (isBillingInfo && withEmail)
            check_if_field_identify("email", contactInfo.getEmail(), jsonObject);

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(countryKey))
            check_if_field_identify("zip", contactInfo.getZip(), jsonObject);

        if (fullInfo || !isBillingInfo) { //full info or shipping
            if (countryKey.equals("US") || countryKey.equals("CA") || countryKey.equals("BR")) {
                if (countryKey.equals("US"))
                    check_if_field_identify("state", "NY", jsonObject);
                else if (countryKey.equals("CA"))
                    check_if_field_identify("state", "QC", jsonObject);
                else
                    check_if_field_identify("state", "RJ", jsonObject);
            }
            check_if_field_identify("city", contactInfo.getCity(), jsonObject);
            check_if_field_identify("address1", contactInfo.getAddress(), jsonObject);
        }
    }

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

        Assert.assertEquals(fieldName + " was not saved correctly in DataBase", expectedResult, fieldContent);
    }
}
