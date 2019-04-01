package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.services.BluesnapToken;
import com.bluesnap.androidapi.services.TokenProvider;

import static com.bluesnap.android.demoapp.BuildConfig.BS_API_PASSWORD;
import static com.bluesnap.android.demoapp.BuildConfig.BS_API_USER;

/**
 * Created by roy.biber on 04/08/2016.
 * This file should follow SandboxToken from the sdk. but cannot replace it in order to keep the demo app build independent.
 */
public class DemoToken extends BluesnapToken{
    public static final String SANDBOX_URL = "https://sandbox.bluesnap.com/services/2/";
    public static final String SANDBOX_TOKEN_CREATION = "payment-fields-tokens";
    public static final String SANDBOX_CREATE_TRANSACTION = "transactions";
    public static final String SANDBOX_PLAN = "recurring/plans";
    public static final String SANDBOX_SUBSCRIPTION = "recurring/subscriptions";

    public static final String SANDBOX_USER = BS_API_USER;
    public static final String SANDBOX_PASS = BS_API_PASSWORD;

    public DemoToken(String merchantToken, TokenProvider tokenProvider) {
        super(merchantToken, tokenProvider);
    }
}
