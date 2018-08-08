package com.bluesnap.androidapi;

import com.bluesnap.androidapi.services.BluesnapToken;
import com.bluesnap.androidapi.services.TokenProvider;

/**
 * Created by oz on 29/10/17.
 */

public class SandboxToken extends BluesnapToken {
    public static final String SANDBOX_URL = "https://sandbox.bluesnap.com/services/2/";
    public static final String SANDBOX_TOKEN_CREATION = "payment-fields-tokens";
    public static final String SANDBOX_CREATE_TRANSACTION = "transactions";

    public static final String SANDBOX_USER = "sdkuser";
    public static final String SANDBOX_PASS = "SDKuser123";

    public SandboxToken(String merchantToken, TokenProvider tokenProvider) {
        super(merchantToken, tokenProvider);
    }
}
