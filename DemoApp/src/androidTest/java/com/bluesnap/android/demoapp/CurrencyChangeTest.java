package com.bluesnap.android.demoapp;

import android.os.Handler;
import android.os.Looper;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;


/**
 * Created by oz on 5/26/16.
 *
 * Runs the app and uses the menu to change currency several times, checking that the new
 * currency is reflected in the Buy button.
 */
@LargeTest
public class CurrencyChangeTest extends EspressoBasedTest {
    private static final Double AMOUNT = 23.4;
    private static final String TAG = CurrencyChangeTest.class.getSimpleName();

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {

        SdkRequest sdkRequest = new SdkRequest(AMOUNT, "USD", 0D, false, false, false);
        setupAndLaunch(sdkRequest);
    }

    @Test
    public void changeCurrencyOnceCheck() throws InterruptedException {
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BlueSnapService.getInstance().getRatesArray();
                            Log.d(TAG, "Service go rates");
                        } catch (Exception e) {
                            fail("Service could not update rates");
                        }
                    }
                });

        onView(withId(R.id.newCardButton)).perform(click());

        onView(withId(R.id.buyNowButton)).check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfo(this.mActivity.getApplicationContext(), false, false);

        CardFormTesterCommon.changeCurrency("CAD");
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("CAD")))));

        CardFormTesterCommon.changeCurrency("ILS");
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("ILS")))));

        CardFormTesterCommon.changeCurrency("USD");
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AMOUNT.toString()))));
    }

}

