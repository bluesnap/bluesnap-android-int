package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.services.AndroidUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

public class TestUtils {
    protected static DecimalFormat decimalFormat;

    public static Activity getCurrentActivity() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return getCurrentActivityOnMainThread();
        } else {
            final Activity[] topActivity = new Activity[1];
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    topActivity[0] = getCurrentActivityOnMainThread();
                }
            });
            return topActivity[0];
        }
    }

    private static Activity getCurrentActivityOnMainThread() {
        ActivityLifecycleMonitor registry = ActivityLifecycleMonitorRegistry.getInstance();
        Collection<Activity> activities = registry.getActivitiesInStage(Stage.RESUMED);
        return activities.iterator().hasNext() ? activities.iterator().next() : null;
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value. Note: View's
     * text property is never null. If you setText(null) it will still be "". Do not use null
     * matcher.
     *
     * @param integerMatcher {@link Matcher} of {@link String} with text to match
     */
    public static Matcher<View> withCurrentTextColor(final Matcher<Integer> integerMatcher) {
        checkNotNull(integerMatcher);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                integerMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                return integerMatcher.matches(textView.getCurrentTextColor());
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView} based on it's text property value. Note:
     * View's Sugar for withTextColor(is("string")).
     */
    public static Matcher<View> withCurrentTextColor(int color) {
        return withCurrentTextColor(is(color));
    }


    /**
     * @param resourceId
     * @return
     */
    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    /**
     * @return
     */
    public Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }

    /**
     *
     */
    public static class DrawableMatcher extends TypeSafeMatcher<View> {
        private final int expectedId;

        public DrawableMatcher(int resourceId) {
            super(View.class);
            this.expectedId = resourceId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageButton)) {
                return false;
            }
            ImageButton imageButton = (ImageButton) target;
            if (expectedId < 0) {
                return imageButton.getDrawable() == null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            if (expectedDrawable == null) {
                return false;
            }
            Bitmap bitmap = getBitmap(imageButton.getDrawable());
            Bitmap otherBitmap = getBitmap(expectedDrawable);
            return bitmap.sameAs(otherBitmap);
        }

        private Bitmap getBitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
        }
    }

    /**
     * @param resourceDrawable
     * @return
     */
    public static Matcher<View> withRawDrawable(final Drawable resourceDrawable) {
        return new RawDrawableMatcher(resourceDrawable);
    }

    /**
     *
     */
    public static class RawDrawableMatcher extends TypeSafeMatcher<View> {
        private final Drawable expectedDrawable;

        public RawDrawableMatcher(Drawable resourceDrawable) {
            super(View.class);
            this.expectedDrawable = resourceDrawable;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageButton)) {
                return false;
            }

            if (expectedDrawable == null) {
                return false;
            }

            ImageButton imageButton = (ImageButton) target;

            Resources resources = target.getContext().getResources();

            Bitmap bitmap = getBitmap(imageButton.getDrawable());
            Bitmap otherBitmap = getBitmap(expectedDrawable);
            return bitmap.sameAs(otherBitmap);
        }

        private Bitmap getBitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with raw drawable: ");
            description.appendValue(expectedDrawable);
        }
    }


    public static Matcher<View> isViewFocused() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return (view).isFocused();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is-selected=true");
            }
        };
    }

    public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof Button) {
                    Button bv = (Button) view; //Save, because of check in getConstraints()
                    stringHolder[0] = bv.getText().toString();
                } else {
                    TextView tv = (TextView) view; //Save, because of check in getConstraints()
                    stringHolder[0] = tv.getText().toString();
                }
            }
        });
        return stringHolder[0];
    }

    public static String getStringFormatAmount(String text, String currencyNameCode, Double amount) {
        return String.format("%s %s %s",
                text,
                AndroidUtil.getCurrencySymbol(currencyNameCode),
                getDecimalFormat().format(amount)
        );
    }

    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#############.##");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMinimumIntegerDigits(1);
        }
        return decimalFormat;
    }

    public static void continueToShippingOrPayInNewCard(String country, boolean fullInfo, boolean withEmail, boolean withShipping) {
        continueToShippingOrPayInNewCard(country, fullInfo, withEmail, withShipping, false);
    }

    public static void continueToShippingOrPayInNewCard(String country, boolean fullInfo, boolean withEmail, boolean withShipping, boolean storeCard) {
        continueToShippingOrPayInNewCard(country, fullInfo, withEmail, withShipping, storeCard, false);
    }

    public static void continueToShippingOrPayInNewCard(String country, boolean fullInfo, boolean withEmail, boolean withShipping, boolean storeCard, boolean shopperHasShippingInfo) {
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, country, fullInfo, withEmail);

        if (storeCard)
            onView(withId(R.id.storeCardSwitch)).perform(swipeRight());

        if (withShipping && fullInfo && !shopperHasShippingInfo) //shipping same as billing is on, un-checking it
            TestUtils.setShippingSameAsBillingSwitch(false);

        pressBuyNowButton();
    }

    public static void goBackToBillingInNewCard() {
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();
    }

    public static void goBackToCreditCardInReturningShopper(boolean useDoneButton, int buttonComponentResourceId) {
        if (useDoneButton)
            pressBuyNowButton(buttonComponentResourceId);

        else {
            goBack();
        }
    }

    public static void goBack() {
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();
    }

    public static void pressBuyNowButton() {
        pressBuyNowButton(R.id.billingButtonComponentView);
    }

    public static void pressBuyNowButton(int buttonComponentResourceId) {
        Espresso.closeSoftKeyboard();
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponentResourceId)))).perform(click());
    }

    public static void setShippingSameAsBillingSwitch(boolean setOn) {
        closeSoftKeyboard();
        if (setOn) {
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());
        } else {
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft());
        }
    }

    public static double round_amount(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    public static String getAmountInString(NumberFormat df, double amount) {
        return df.format(amount);
    }

    public static boolean checkCountryHasZip(String countryText) {
        return !Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(countryText.toUpperCase());
    }

    public static void setStoreCardSwitch(boolean storeCard) {
        closeSoftKeyboard();
        if (storeCard) {
            onView(withId(R.id.storeCardSwitch)).perform(swipeRight());
        } else {
            onView(withId(R.id.storeCardSwitch)).perform(swipeLeft());
        }
    }

}