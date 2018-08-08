package com.bluesnap.androidapi.services;

/**
 * An interface to run async callbacks from services on the main thread.
 */
public interface BluesnapServiceCallback {
    void onSuccess();

    void onFailure();
}
