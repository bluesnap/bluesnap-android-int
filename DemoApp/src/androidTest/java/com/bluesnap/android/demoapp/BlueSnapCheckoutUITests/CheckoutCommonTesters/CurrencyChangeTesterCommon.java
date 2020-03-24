package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters;

import android.os.Handler;
import android.os.Looper;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import android.util.Log;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.hamcrest.Matchers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;


/**
 * Created by oz on 5/26/16.
 *
 * Runs the app and uses the menu to change currency several times, checking that the new
 * currency is reflected in the Buy button.
 */
public class CurrencyChangeTesterCommon {
    private static final String TAG = CurrencyChangeTesterCommon.class.getSimpleName();

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
        changeCurrency("CAD");
        changeCurrency("ILS");
        changeCurrency(initialCurrency);

        String buttonContent = TestUtils.getText(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))));

        onView(Matchers.allOf(ViewMatchers.withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Amount changed" +
                        " expected amount: " + initialAmount + ", actual button content: " + buttonContent))
                .check(matches(withText(containsString(initialAmount))));
    }

    private static void checkCurrencyInHamburgerButton(String testName, String currencyCode) {
        //verify hamburger button displays the correct currency when clicking on it
        onView(withId(R.id.hamburger_button)).perform(click());
        //String buyNowButtonText = TestUtils.getText(withText(containsString("Currency")));

        onView(withText(containsString("Currency")))
                .withFailureHandler(new CustomFailureHandler(testName + ": Hamburger button doesn't display the correct currency"))
                .check(matches(withText(containsString(currencyCode))));
        Espresso.pressBack();
    }

    private static void checkCurrencyInBuyButton(String testName, int buttonComponent, String currencyCode) {
        //verify "Pay" button displays the correct currency when clicking on it
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button doesn't display the correct currency"))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol(currencyCode)))));
    }

    public static void changeCurrency(String currencyCode) {
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString(currencyCode))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
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

