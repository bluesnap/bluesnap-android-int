package com.bluesnap.android.demoapp;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
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
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GBPtoAUDTaxAndSubtotalTest extends EspressoBasedTest {

    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityTestRule = new ActivityTestRule<>(DemoMainActivity.class);
    private Double bgpAmonut = 300D;


    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }


    @Override
    public void setup() throws InterruptedException {
        super.setup();
        clearPrefs(mActivityTestRule.getActivity().getApplicationContext());

    }

    @Test
    public void gBPtoAUDTaxAndSubtotalTest() {

        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);
        checkToken();

        ViewInteraction spinner = onView(
                allOf(withId(R.id.merchantStoreCurrencySpinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        1),
                                1),
                        isDisplayed()));
        spinner.perform(click());

        DataInteraction checkedTextView = onData(anything())
                .inAdapterView(withClassName(is("android.widget.ListPopupWindow$DropDownListView")))
                .atPosition(2);
        checkedTextView.perform(click());

        ViewInteraction spinner2 = onView(
                allOf(withId(R.id.rateSpinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        3),
                                3),
                        isDisplayed()));
        spinner2.perform(click());

        DataInteraction checkedTextView2 = onData(anything())
                .inAdapterView(withClassName(is("android.widget.ListPopupWindow$DropDownListView")))
                .atPosition(2);
        checkedTextView2.perform(click());

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
        editText2.perform(replaceText("300"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.demoTaxEditText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        5),
                                1),
                        isDisplayed()));
        editText3.perform(replaceText("2"), closeSoftKeyboard());

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

        ViewInteraction button = onView(
                allOf(withId(R.id.buyNowButton), withContentDescription("BuyNowbutton"),

                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.subtotalValueTextview),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.subtotal_tax_table),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("£300.00")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.taxValueTextview),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.subtotal_tax_table),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("£6.00")));

//        ViewInteraction textView3 = onView(
//                allOf(withId(R.id.taxValueTextview), withText("?6.00"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.subtotal_tax_table),
//                                        1),
//                                1),
//                        isDisplayed()));
//        textView3.check(matches(withText("?6.00")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.hamburger_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        0),
                                1),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(android.R.id.title), withText("Currency - GBP"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        textView4.perform(click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.currency_list_view),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(6);
        linearLayout.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.subtotalValueTextview),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.subtotal_tax_table),
                                        0),
                                1),
                        isDisplayed()));
        //Verify that subtotal contains AU currency
        textView5.check(matches(withText(containsString("AU$"))));


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
