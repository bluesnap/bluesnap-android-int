package com.bluesnap.androidapi.services;

/**
 * An interface to run after init Cardinal SDK.
 */
public interface InitCardinalServiceCallback {
    void onSuccess();

    void onFailure();
}
