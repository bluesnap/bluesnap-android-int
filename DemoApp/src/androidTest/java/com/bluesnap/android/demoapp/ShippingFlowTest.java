package com.bluesnap.android.demoapp;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by oz on 5/26/16.
 */
//@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShippingFlowTest {
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, false);
    private BluesnapCheckoutActivity mActivity;

    //TODO: move this to separate class that generate random card numbers
    private static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    @After
    public void keepRunning() throws InterruptedException {
        //  while (true) {Thread.sleep(2000);}//TODO: remove this
    }

    @Before
    public void setup() throws BSPaymentRequestException {
        Intent intent = new Intent();
        SdkRequest sdkRequest = new SdkRequest(23.4, "USD");
        BlueSnapService.getInstance().setSdkRequest(sdkRequest);
        mActivityRule.launchActivity(intent);
    }

    //@Test
    ///.PerformException: Error performing 'Send down motion event' on view 'unknown'.
    public void enterCardDetails() throws InterruptedException {
        // Check that the sipping button appears
        onView(withId(R.id.buyNowButton))
                .check(matches(withText("Shipping")));

        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());

        CardFormTesterCommon.fillInAllFieldsWithValidCard();

        onView(withId(R.id.buyNowButton)).perform(click());
        Thread.sleep(2000);
        //TODO: check that the shipping activity is launched.
        CardFormTesterCommon.fillInShippingDetails();
        Thread.sleep(2000);
        onView(withId(R.id.shippingBuyNowButton)).perform(ViewActions.closeSoftKeyboard()).perform(click());
        //TOOD: Verify that flow returns to Payment


    }


}

