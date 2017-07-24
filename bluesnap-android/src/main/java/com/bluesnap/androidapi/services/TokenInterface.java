package com.bluesnap.androidapi.services;

/**
 * An interface to run async callbacks from service (when token expired) to merchant.
 */

public interface TokenInterface {

    void getNewToken(TokenServiceCallback tokenServiceCallback);
}
