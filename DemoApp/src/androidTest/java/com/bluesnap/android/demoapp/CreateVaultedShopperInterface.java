package com.bluesnap.android.demoapp;

import org.json.JSONException;

/**
 * Created by sivani on 01/09/2018.
 */

public interface CreateVaultedShopperInterface {
    void onServiceSuccess() throws JSONException;

    void onServiceFailure();
}
