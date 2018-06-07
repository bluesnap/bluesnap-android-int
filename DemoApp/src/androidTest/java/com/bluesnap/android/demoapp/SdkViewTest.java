package com.bluesnap.android.demoapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.NoMatchingViewException;
//import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageButton;

import com.bluesnap.androidapi.models.SdkRequest;
//import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static com.bluesnap.android.demoapp.CardFormTesterCommon.cardNumberGeneratorTest;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 04/06/2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SdkViewTest extends EspressoBasedTest {

    @After
    public void keepRunning() throws InterruptedException {
        //        while (true) { Thread.sleep(2000); } //Remove this
        Thread.sleep(1000);
    }


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

    }

    @Test
    public void state_view_validation() throws InterruptedException {
        //------------------------------------------
        // Country Image
        //------------------------------------------

        //String billingCountry = "USA";

        Matcher<View> countryImageButtonImageBottunVM = withId(R.id.countryImageButton);
        Matcher<View> stateLayoutVM = withId(R.id.input_layout_state);

        //Test validation of state appearance
        //Verify USA has been chosen
        try {
            //onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.us)));
            onView(anyOf(withDrawable(R.drawable.us), withDrawable(R.drawable.ca), withDrawable(R.drawable.br))).check(matches(isDisplayed()));
            onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

        } catch (NoMatchingViewException nsve) {
            //billingCountry = "notUSA";
            onView(stateLayoutVM).check(matches(not(ViewMatchers.isDisplayed())));
            return;
        }

        //Test validation of state appearance
    }

    @Test
    public void new_card_state_view_validation_after_change() throws InterruptedException {
        //------------------------------------------
        // Country Image
        //------------------------------------------

        String billingCountry = "USA";

        Matcher<View> countryImageButtonImageBottunVM = withId(R.id.countryImageButton);
        Matcher<View> stateLayoutVM = withId(R.id.input_layout_state);

        //Test validation of state appearance. changing to USA
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.us)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.it)));
        onView(stateLayoutVM).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.ca)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.es)));
        onView(stateLayoutVM).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(withDrawable(R.drawable.br)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

    }


    public Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }


    public class DrawableMatcher extends TypeSafeMatcher<View> {
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
}
