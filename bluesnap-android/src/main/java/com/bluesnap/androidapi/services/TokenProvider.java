package com.bluesnap.androidapi.services;

/**
 * An interface to run async callbacks from service (when token expired) to merchant.
 */

public interface TokenProvider {

    void getNewToken(TokenServiceCallback tokenServiceCallback);
}
