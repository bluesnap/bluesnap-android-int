package com.bluesnap.android.demoapp;

import android.os.Handler;
import android.os.Looper;
import android.support.test.espresso.Espresso;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;


/**
 * Created by oz on 5/26/16.
 *
 * Runs the app and uses the menu to change currency several times, checking that the new
 * currency is reflected in the Buy button.
 */
public class CurrencyChangeTest {
    private static final String TAG = CurrencyChangeTest.class.getSimpleName();

    /**
     * This test verifies that changing the currency changes
     * the hamburger button and buy button as it should.
     */
    public static void change_currency_validation(int buttonComponent, String currencyCode) throws InterruptedException {
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
        check_currency_in_hamburger_button(currencyCode);
        check_currency_in_buy_button(buttonComponent, currencyCode);
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one, the amount remains the same
     */
    public static void change_currency_amount_validation(int buttonComponent, String initialCurrency, String initialAmount) throws InterruptedException {
        CreditCardLineTesterCommon.changeCurrency("CAD");
        CreditCardLineTesterCommon.changeCurrency("ILS");
        CreditCardLineTesterCommon.changeCurrency(initialCurrency);

        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(withText(containsString(initialAmount))));

    }

    public static void check_currency_in_hamburger_button(String currencyCode) {
        //verify hamburger button displays the correct currency when clicking on it
        onView(withId(R.id.hamburger_button)).perform(click());
        //String buyNowButtonText = TestUtils.getText(withText(containsString("Currency")));

        onView(withText(containsString("Currency"))).check(matches(withText(containsString(currencyCode))));
        Espresso.pressBack();
    }

    public static void check_currency_in_buy_button(int buttonComponent, String currencyCode) {
        //verify "Pay" button displays the correct currency when clicking on it
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol(currencyCode)))));
    }

}

