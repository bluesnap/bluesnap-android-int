package com.bluesnap.android.demoapp;


import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.PrefsStorage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PressingHomeButtonTest extends EspressoBasedTest {
    private static final String TAG = PressingHomeButtonTest.class.getSimpleName();
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class, true, false);
    private DemoMainActivity mActivity;

    @After
    public void keepRunning() throws InterruptedException {
        //        while (true) { Thread.sleep(2000); } //Remove this
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        /*super.setup();
        super.setSDKToken();
        SdkRequest sdkRequest = new SdkRequest(23.4, "USD");
        //Thread.sleep(500);
        BlueSnapService.getInstance().setSdkRequest(sdkRequest);*/
        Intent intent = new Intent();
        /*sdkRequest.setShippingRequired(false);*/

        mActivityRule.launchActivity(intent);
        mActivity = mActivityRule.getActivity();
        clearPrefs(mActivity.getApplicationContext());


        while (BlueSnapService.getInstance().getBlueSnapToken() == null) {
            Log.d(TAG, "Waiting for token setup");
            Thread.sleep(2000);

        }

        while (BlueSnapService.getInstance().getsDKConfiguration() == null) {
            Log.d(TAG, "Waiting for SDK configuration to finish");
            Thread.sleep(2000);

        }

    }

    @Test
    public void pressingHomeButtonTest() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.productPriceEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        3),
                                1),
                        isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.productPriceEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        3),
                                1),
                        isDisplayed()));
        editText2.perform(replaceText("10"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.demoTaxEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        5),
                                1),
                        isDisplayed()));
        editText3.perform(replaceText("10"), closeSoftKeyboard());

        goHome(mActivity);

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.shippingSwitch), withText("Shipping"),
                        childAtPosition(
                                allOf(withId(R.id.mainLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        switch_.perform(click());

        ViewInteraction switch_2 = onView(
                allOf(withId(R.id.billingSwitch), withText("Billing"),
                        childAtPosition(
                                allOf(withId(R.id.mainLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                3),
                        isDisplayed()));
        switch_2.perform(click());

        ViewInteraction switch_3 = onView(
                allOf(withId(R.id.emailSwitch), withText("Email"),
                        childAtPosition(
                                allOf(withId(R.id.mainLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        switch_3.perform(click());

        ViewInteraction zoomButton = onView(
                allOf(withId(R.id.merchantAppSubmitButton),
                        childAtPosition(
                                allOf(withId(R.id.mainLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        zoomButton.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.cardHolderNameEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nameLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText4.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.cardHolderNameEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nameLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText5.perform(replaceText("rrr ttt"), closeSoftKeyboard());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.cardHolderNameEditText), withText("rrr ttt"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nameLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText6.perform(pressImeActionButton());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.emailEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText7.perform(replaceText("rrr@ttt."), closeSoftKeyboard());

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.creditCardNumberEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText8.perform(replaceText("5555 5555 5555 5557"), closeSoftKeyboard());

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.creditCardNumberEditText), withText("5555 5555 5555 5557"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText9.perform(pressImeActionButton());

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.expDateEditText),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                0)),
                                1),
                        isDisplayed()));
        editText10.perform(replaceText("11"), closeSoftKeyboard());

        ViewInteraction editText11 = onView(
                allOf(withId(R.id.expDateEditText), withText("11/"),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                0)),
                                1),
                        isDisplayed()));
        editText11.perform(replaceText("11/22"));

        ViewInteraction editText12 = onView(
                allOf(withId(R.id.expDateEditText), withText("11/22"),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                0)),
                                1),
                        isDisplayed()));
        editText12.perform(closeSoftKeyboard());

        ViewInteraction editText13 = onView(
                allOf(withId(R.id.expDateEditText), withText("11/22"),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                0)),
                                1),
                        isDisplayed()));
        editText13.perform(pressImeActionButton());

        ViewInteraction editText14 = onView(
                allOf(withId(R.id.cvvEditText),
                        childAtPosition(
                                allOf(withId(R.id.cvvLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                1)),
                                1),
                        isDisplayed()));
        editText14.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction editText15 = onView(
                allOf(withId(R.id.cvvEditText), withText("123"),
                        childAtPosition(
                                allOf(withId(R.id.cvvLinearLayout),
                                        childAtPosition(
                                                withId(R.id.expCvvTableRow),
                                                1)),
                                1),
                        isDisplayed()));
        editText15.perform(pressImeActionButton());

        ViewInteraction editText16 = onView(
                allOf(withId(R.id.billingAddressLineEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText16.perform(replaceText("zxc cxz"), closeSoftKeyboard());

        ViewInteraction editText17 = onView(
                allOf(withId(R.id.billingAddressLineEditText), withText("zxc cxz"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText17.perform(pressImeActionButton());

        ViewInteraction editText18 = onView(
                allOf(withId(R.id.zipEditText),
                        childAtPosition(
                                allOf(withId(R.id.zipFieldLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        editText18.perform(replaceText("123456"), closeSoftKeyboard());

        goHome(mActivity, BluesnapCheckoutActivity.class);

        ViewInteraction editText19 = onView(
                allOf(withId(R.id.billingStateEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText19.perform(replaceText("NJ"), closeSoftKeyboard());

        ViewInteraction editText20 = onView(
                allOf(withId(R.id.billingCityEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText20.perform(replaceText("rrr"), closeSoftKeyboard());

        ViewInteraction editText21 = onView(
                allOf(withId(R.id.emailEditText), withText("rrr@ttt."),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText21.perform(replaceText("rrr@ttt.com"));

        ViewInteraction editText22 = onView(
                allOf(withId(R.id.emailEditText), withText("rrr@ttt.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText22.perform(closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(R.id.buyNowButton), withText("Shipping"), withContentDescription("BuyNowbutton"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fraglyout),
                                        2),
                                0),
                        isDisplayed()));
        button.perform(click());

        goHome(mActivity, BluesnapCheckoutActivity.class);

        ViewInteraction button2 = onView(
                allOf(withId(R.id.shippingBuyNowButton), withText("Pay $ 11.00"), withContentDescription("BuyNowbutton"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fraglyout),
                                        3),
                                1),
                        isDisplayed()));
        button2.perform(click());

        goHome(mActivity);

        PrefsStorage prefsStorage = new PrefsStorage(getContext());

        while (prefsStorage.getString("SHOPPER_ID", "") == null || prefsStorage.getString("SHOPPER_ID", "").equals("")) {
            Log.d(TAG, "Waiting for SDK configuration to finish");
            threadSleep(2000);
        }

        onView(withId(R.id.transactionResult))
                .check(matches(withText(containsString("Transaction Success"))));

    }

    private void goHome(Activity activity) {
        goHome(activity, null);
    }
    private void goHome(Activity activity, Class aClass) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
        threadSleep(6000);
        bringToForeground(mActivity, aClass);
    }

    private void bringToForeground(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, (aClass == null ? activity.getClass() : BluesnapCheckoutActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    private void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread.sleep failed " + e.getMessage());
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
