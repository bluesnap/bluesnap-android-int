package com.bluesnap.android.demoapp.WebViewUITests;

import android.support.test.espresso.web.webdriver.DriverAtoms;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.bluesnap.android.demoapp.EspressoBasedTest;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.WebViewActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;

/**
 * Created by sivani on 23/08/2018.
 */

@RunWith(AndroidJUnit4.class)
public class PayPalTests extends EspressoBasedTest {

    @Rule
    public ActivityTestRule<WebViewActivity> mActivityRule =
            new ActivityTestRule<WebViewActivity>(WebViewActivity.class,
                    false, false) {
                @Override
                protected void afterActivityLaunched() {
                    onWebView().forceJavascriptEnabled();
                }
            };

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.payPalButton)).perform(click());
    }

    @Test
    public void pay_pal_transaction_test() {
        try {
            onWebView()
                    .withElement(findElement(Locator.ID, "email")) // similar to onView(withId(...))
                    .perform(clearElement())
                    .perform(DriverAtoms.webKeys("apiShopper@bluesnap.com"))

                    .withElement(findElement(Locator.ID, "btnNext"))
                    .perform(webClick());

        } catch (Exception e) {
            Log.d(TAG, "Email is already filled in");
        }

        try {
            onWebView()
                    .withElement(findElement(Locator.ID, "password"))
                    .perform(clearElement())
                    .perform(DriverAtoms.webKeys("Plimus123")) // Similar to perform(click())

                    .withElement(findElement(Locator.ID, "btnLogin"))
                    .perform(webClick());

        } catch (Exception e) {
            Log.d(TAG, "Password is already filled in");
        }

//        //check the id of this element
//        onWebView().withNoTimeout()
//                .withElement(findElement(Locator.ID, "btnPay"))
//                .perform(webClick());

    }

}
