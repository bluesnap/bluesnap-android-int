package com.bluesnap.androidapi.services;

import androidx.annotation.MainThread;
import com.bluesnap.androidapi.models.SdkResult;

/**
 * An interface to run async callbacks from services on the main thread.
 */
public interface BluesnapServiceResultCallback {
    @MainThread
    void onSuccess(SdkResult sdkResult);

    @MainThread
    void onFailure(String errorMsg);
}
