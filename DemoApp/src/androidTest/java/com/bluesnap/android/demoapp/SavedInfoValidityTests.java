package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.CardFormTesterCommon;
import com.bluesnap.android.demoapp.EspressoBasedTest;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 16/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SavedInfoValidityTests extends EspressoBasedTest {

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setEmailRequired(true);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

    }

    /**
     * This test verifies that the billing contact info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button
     */
    @Test
    public void contact_info_saved_validation_billing() throws InterruptedException {
        //Changing country to USA
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("US", true, true);

        //Continue to Shipping and back to billing
        onView(withId(R.id.buyNowButton)).perform(click());
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        //Verify country has been saved in billing
        onView(withId(R.id.countryImageButton)).check(matches(TestUtils.withDrawable(R.drawable.us)));

        //Verify state has been saved in billing
        onView(withId(R.id.input_state)).check(matches(withText("NY")));

        //Verify full name has been saved in billing
        onView(withId(R.id.input_name)).check(matches(withText("La Fleur")));

        //Verify email has been saved in billing
        onView(withId(R.id.input_email)).check(matches(withText("test@sdk.com")));

        //Verify zip has been saved in billing
        onView(withId(R.id.input_zip)).check(matches(withText("3abc 324a")));

        //Verify city has been saved in billing
        onView(withId(R.id.input_city)).check(matches(withText("Tel Aviv")));

        //Verify address has been saved in billing
        onView(withId(R.id.input_address)).check(matches(withText("Rotchild street")));

    }

    /**
     * This test verifies that the shipping contact info is saved when
     * going back to billing and entering the shipping once again.
     */
    @Test
    public void contact_info_saved_validation_shipping() throws InterruptedException {
        String defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, true, true);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Changing country to USA
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        CardFormTesterCommon.fillInContactInfoShipping("US");

        //Verify country has been saved in billing
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(TestUtils.withDrawable(R.drawable.us)));

        //Verify state has been saved in billing
        onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(withText("NY")));

        //Verify full name has been saved in billing
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(withText("La Fleur")));

        //Verify zip has been saved in billing
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(withText("3abc 324a")));

        //Verify city has been saved in billing
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(withText("Tel Aviv")));

        //Verify address has been saved in billing
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(withText("Rotchild street")));

    }

    /**
     * This test verifies that the credit card line info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button.
     */
    @Test
    public void cc_card_info_saved_validation() throws InterruptedException {
        String defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, true, true);
        //String creditCardNumber = TestUtils.getText(withId(R.id.creditCardNumberEditText));

        //Continue to Shipping and back to billing
        onView(withId(R.id.buyNowButton)).perform(click());
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        //Verify cc number has been saved in billing
        onView(withId(R.id.creditCardNumberEditText)).check(matches(withText("5288")));

        //Verify cc number has been saved in billing
        onView(withId(R.id.expEditText)).check(matches(withText("12/26")));

        //Verify cvv number has been saved in billing
        onView(withId(R.id.cvvEditText)).check(matches(withText("123")));
    }
}
