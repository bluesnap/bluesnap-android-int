package com.bluesnap.androidapi.services;

/**
 * An interface to run from merchant side (our implementation) after merchant created a new token (when the before was expired).
 */

public interface TokenServiceCallback {
    void complete(String newToken);
}
