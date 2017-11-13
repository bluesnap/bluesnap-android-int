package com.bluesnap.android.demoapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DemoMainActivityTest {

    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityTestRule = new ActivityTestRule<>(DemoMainActivity.class);

    @Test
    public void demoMainActivityTest() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.productPriceEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                1),
                        isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.productPriceEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                1),
                        isDisplayed()));
        editText2.perform(replaceText("11"), closeSoftKeyboard());

        pressBack();

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

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.cardHolderNameEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nameLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText3.perform(replaceText("rrr bbb"), closeSoftKeyboard());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.emailEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        editText4.perform(replaceText("rr@bb.com"), closeSoftKeyboard());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.creditCardNumberEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText5.perform(replaceText("4111 1111 1111 1111"), closeSoftKeyboard());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.expDateEditText),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.TableRow")),
                                                0)),
                                1),
                        isDisplayed()));
        editText6.perform(replaceText("11"), closeSoftKeyboard());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.expDateEditText), withText("11/"),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.TableRow")),
                                                0)),
                                1),
                        isDisplayed()));
        editText7.perform(replaceText("11/22"));

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.expDateEditText), withText("11/22"),
                        childAtPosition(
                                allOf(withId(R.id.expLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.TableRow")),
                                                0)),
                                1),
                        isDisplayed()));
        editText8.perform(closeSoftKeyboard());

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.cvvEditText),
                        childAtPosition(
                                allOf(withId(R.id.cvvLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.TableRow")),
                                                1)),
                                1),
                        isDisplayed()));
        editText9.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.billingAddressLineEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText10.perform(replaceText("asdfgh"), closeSoftKeyboard());

        ViewInteraction editText11 = onView(
                allOf(withId(R.id.zipEditText),
                        childAtPosition(
                                allOf(withId(R.id.zipFieldLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        editText11.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.cardFieldsLinearLayout),
                        childAtPosition(
                                allOf(withId(R.id.fraglyout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction editText12 = onView(
                allOf(withId(R.id.billingStateEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText12.perform(replaceText("asdfgh"), closeSoftKeyboard());

        ViewInteraction editText13 = onView(
                allOf(withId(R.id.billingCityEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText13.perform(replaceText("asdfgh"), closeSoftKeyboard());

        ViewInteraction editText14 = onView(
                allOf(withId(R.id.billingCityEditText), withText("asdfgh"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        editText14.perform(pressImeActionButton());

        ViewInteraction button = onView(
                allOf(withId(R.id.buyNowButton), withText("Shipping"), withContentDescription("BuyNowbutton"),
                        childAtPosition(
                                allOf(withId(R.id.fraglyout),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.shippingBuyNowButton), withText("Pay $ 11.00"), withContentDescription("BuyNowbutton"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fraglyout),
                                        3),
                                1),
                        isDisplayed()));
        button2.perform(click());

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
