package com.bluesnap.android.demoapp;

import android.os.Handler;
import android.os.Looper;
import android.support.test.espresso.Espresso;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;

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
    public static void currency_view_validation(int buttonComponent, String currencyCode) throws InterruptedException {
        checkCurrencyInHamburgerButton(currencyCode);
        checkCurrencyInBuyButton(buttonComponent, currencyCode);
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

    private static void checkCurrencyInHamburgerButton(String currencyCode) {
        //verify hamburger button displays the correct currency when clicking on it
        onView(withId(R.id.hamburger_button)).perform(click());
        //String buyNowButtonText = TestUtils.getText(withText(containsString("Currency")));

        onView(withText(containsString("Currency"))).check(matches(withText(containsString(currencyCode))));
        Espresso.pressBack();
    }

    private static void checkCurrencyInBuyButton(int buttonComponent, String currencyCode) {
        //verify "Pay" button displays the correct currency when clicking on it
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol(currencyCode)))));
    }

    //TODO:
    public static void rates_validation(int buttonComponent, String currencyCode) throws InterruptedException {
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
    }


}

