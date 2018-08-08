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
import static android.support.test.espresso.matcher.ViewMatchers.*;
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
    public static void currency_view_validation(String testName, int buttonComponent, String currencyCode) {
        checkCurrencyInHamburgerButton(testName, currencyCode);
        checkCurrencyInBuyButton(testName, buttonComponent, currencyCode);
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one, the amount remains the same
     */
    public static void change_currency_amount_validation(String testName, int buttonComponent, String initialCurrency, String initialAmount) {
        CreditCardLineTesterCommon.changeCurrency("CAD");
        CreditCardLineTesterCommon.changeCurrency("ILS");
        CreditCardLineTesterCommon.changeCurrency(initialCurrency);

        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Amount changed"))
                .check(matches(withText(containsString(initialAmount))));
    }

    private static void checkCurrencyInHamburgerButton(String testName, String currencyCode) {
        //verify hamburger button displays the correct currency when clicking on it
        onView(withId(R.id.hamburger_button)).perform(click());
        //String buyNowButtonText = TestUtils.getText(withText(containsString("Currency")));

        onView(withText(containsString("Currency")))
                .withFailureHandler(new CustomFailureHandler(testName + ": Hamburger button doesn't present the correct currency"))
                .check(matches(withText(containsString(currencyCode))));
        Espresso.pressBack();
    }

    private static void checkCurrencyInBuyButton(String testName, int buttonComponent, String currencyCode) {
        //verify "Pay" button displays the correct currency when clicking on it
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button doesn't present the correct currency"))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol(currencyCode)))));
    }

    //TODO:
    public static void rates_validation(int buttonComponent, String currencyCode) {
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

