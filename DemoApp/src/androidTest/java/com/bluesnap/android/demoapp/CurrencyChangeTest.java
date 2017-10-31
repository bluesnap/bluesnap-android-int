package com.bluesnap.android.demoapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;


/**
 * Created by oz on 5/26/16.
 */
//@RunWith(AndroidJUnit4.class)
@LargeTest
public class CurrencyChangeTest extends EspressoBasedTest {
    public static final Double AMOUNT = 23.4;
    private static final String TAG = CurrencyChangeTest.class.getSimpleName();
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, false);
    private BluesnapCheckoutActivity mActivity;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Before
    public void setup() throws InterruptedException {
        super.setup();
        super.setSDKToken();
        super.setRates();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(AMOUNT);
        Intent intent = new Intent();
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        paymentRequest.setCurrencyNameCode("USD");
        paymentRequest.setShippingRequired(false);
        mActivityRule.launchActivity(intent);
        clearPrefs(mActivityRule.getActivity().getBaseContext());
    }

    /**
     * @throws InterruptedException
     */
    @Test
    public void changeCurrencyOnceCheck() throws InterruptedException {
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {

                        BlueSnapService.getInstance().updateRates(new BluesnapServiceCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Service go rates");
                            }

                            @Override
                            public void onFailure() {
                                fail("Service could not update rates");
                            }
                        });
                    }
                });

        onView(withId(R.id.buyNowButton)).check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("CAD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("CAD")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("ILS"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("ILS")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("USD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AMOUNT.toString()))));


        onView(withId(R.id.buyNowButton)).perform(click());

    }

}

