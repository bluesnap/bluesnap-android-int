package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A test-specific activity that immediately starts another activity and listens for that subject's
 * results
 */
public class ResultTestActivity extends Activity {

    private final static String EXTRA_START_ACTIVITY_INTENT = "extraStartActivityIntent";
    private final static int REQUEST_CODE = 9999;
    private int mResultCode;
    private Intent mResultData;

    /**
     * A simple static method to extract intent creation for this test activity
     *
     * @param subjectIntent The intent that will start the activity being tested.
     */
    public static Intent createIntent(Intent subjectIntent) {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), ResultTestActivity.class);
        intent.putExtra(EXTRA_START_ACTIVITY_INTENT, subjectIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Execute the steps necessary for testing whether a subject activity finishes with the
     * activity results expected
     *
     * @param test Define the subject's intent, actions required to finish the subject activity,
     *             and a matcher for ResultTestActivity, likely receivedExpectedResult();
     */
    public static void runActivityResultTest(ActivityResultTest test) {
        // Start an instance of ActivityResultTest, using the subject Activity's intent as an extra
        Intent subjectIntent = test.getSubjectIntent();
//        subjectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        Activity activity = getInstrumentation().startActivitySync(subjectIntent);

        // Perform actions that will cause your subject to finish with a result
        test.triggerActivityResult();

        // If you have the Developer option "Don't keep activities" enabled on your testing device,
        // the instance of ResultTestActivity from startActivitySync() will be different from the
        // ResultTestActivity that will actually receive your subject's results. Since it doesn't
        // matter which instance of ResultTestActivity gets results, we use a testing helper method
        // to get the current activity, which we assume will be a ResultTestActivity
        ResultTestActivity resultTestActivity = (ResultTestActivity) TestUtils.getCurrentActivity();

        // Match the current ResultTestActivity against your expectations for the results as
        // defined in the input ActivityResultTest
        //assertThat(resultTestActivity, test.getActivityResultMatcher());
        Matcher<ResultTestActivity> activityResultMatcher = test.getActivityResultMatcher();
        boolean matches = activityResultMatcher.matches(resultTestActivity);
        assertThat("matching", matches);
        // resultTestActivity has been created in this method and if we don't finish it here, it
        // will continue to exist on your device. There's no need for that, so kill it.
        activity.finish();
    }

    /**
     * A matcher that determines if the ResultTestActivity has received the activity results expected
     * from your subject activity
     *
     * @param resultCodeMatcher Matcher for the result code
     * @param resultDataMatcher Matcher for the data in the resultant Intent
     */
    public static Matcher<ResultTestActivity> receivedExpectedResult(final Matcher<Integer> resultCodeMatcher, final Matcher<Intent> resultDataMatcher) {
        return new TypeSafeMatcher<ResultTestActivity>() {
            @Override
            protected boolean matchesSafely(ResultTestActivity item) {
                return resultCodeMatcher.matches(item.mResultCode) && resultDataMatcher.matches(item.mResultData);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with result code=").appendDescriptionOf(resultCodeMatcher);
                description.appendText(" and with intent=").appendDescriptionOf(resultDataMatcher);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mResultCode = resultCode;
            mResultData = data;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent subjectIntent = getIntent().getParcelableExtra(EXTRA_START_ACTIVITY_INTENT);
            startActivityForResult(subjectIntent, REQUEST_CODE);
        }
    }

    @Override
    public String toString() {
        return "Result Activity with result code=" + mResultCode + " and request data=" + mResultData;
    }

    /**
     * An interface that defines all of the variables involved in testing that an activity's
     * results are as expected
     */
    public interface ActivityResultTest {
        /**
         * @return the intent with the appropriate extras that will start your subject activity
         */
        Intent getSubjectIntent();

        /**
         * Perform the necessary UI actions necessary to trigger the target activity finishing with
         * a result.
         */
        void triggerActivityResult();

        /**
         * @return a matcher for the result test activity to match the target activity's result
         */
        Matcher<ResultTestActivity> getActivityResultMatcher();
    }
}