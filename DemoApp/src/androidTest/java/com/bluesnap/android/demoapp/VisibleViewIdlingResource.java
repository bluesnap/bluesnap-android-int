package com.bluesnap.android.demoapp;

import androidx.test.espresso.IdlingResource;
import android.view.View;

import javax.annotation.Nullable;

import static com.bluesnap.android.demoapp.TestUtils.getCurrentActivity;

public class VisibleViewIdlingResource implements IdlingResource {

    private final int targetViewId;
    private final int targetVisibility;
    private String resourceName;
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    public VisibleViewIdlingResource(int targetView, int visible, @Nullable String resourceName) {
        this.targetViewId = targetView;
        this.targetVisibility = visible;
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        return VisibleViewIdlingResource.class.getName() + "_" + String.valueOf(resourceName);
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;

        if (getCurrentActivity() == null)
            return false;

        View targetView = getCurrentActivity().findViewById(targetViewId);
        if (targetView == null)
            return false;

        isIdle = targetVisibility == targetView.getVisibility();
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }

        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}