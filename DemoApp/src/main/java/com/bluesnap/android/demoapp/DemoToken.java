package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.services.BluesnapToken;
import com.bluesnap.androidapi.services.TokenInterface;

/**
 * Created by roy.biber on 04/08/2016.
 */
public class DemoToken extends BluesnapToken{
    public static final String SANDBOX_URL = "https://sandbox.bluesnap.com/services/2/";
    public static final String SANDBOX_TOKEN_CREATION = "payment-fields-tokens";
    public static final String SANDBOX_CREATE_TRANSACTION = "transactions";

    public static final String SANDBOX_USER = "sdkuser";
    public static final String SANDBOX_PASS = "SDKuser123";

    public DemoToken(String merchantToken, TokenInterface tokenInterface) {
        super(merchantToken, tokenInterface);
    }
}
