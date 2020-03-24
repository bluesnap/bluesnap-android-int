package com.bluesnap.android.demoapp;

import androidx.test.espresso.FailureHandler;
import android.view.View;
import org.hamcrest.Matcher;

import static junit.framework.Assert.fail;

/**
 * Created by sivani on 29/07/2018.
 */

public class CustomFailureHandler implements FailureHandler {

    String errorMessage;

    public CustomFailureHandler(String errorMessage_) {
        errorMessage = errorMessage_;
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        fail(errorMessage);
    }
}
