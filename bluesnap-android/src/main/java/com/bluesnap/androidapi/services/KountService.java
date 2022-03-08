package com.bluesnap.androidapi.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.kount.api.DataCollector;

import java.util.UUID;

/**
 * Created by roy.biber on 14/11/2017.
 */

public class KountService {
    private static final String TAG = KountService.class.getSimpleName();
    private static final KountService INSTANCE = new KountService();
    public static final int KOUNT_MERCHANT_ID = 700000;
    public static final String EXTRA_KOUNT_MERCHANT_ID = "com.bluesnap.intent.KOUNT_MERCHANT_ID";
    private static final int KOUNT_REQUST_ID = 3;
    private DataCollector kount;

    private static String kountSessionId;

    public String getKountSessionId() {
        return kountSessionId;
    }

    public static KountService getInstance() {
        return INSTANCE;
    }

    void setupKount(Integer kountMerchantID, final Context context, boolean isProduction) {
        kount = DataCollector.getInstance();
        kount.setMerchantID(kountMerchantID);
        kount.setContext(context);
        kount.setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT);

        if (isProduction) {
            kount.setEnvironment(DataCollector.ENVIRONMENT_PRODUCTION);
            kount.setDebug(false);

        } else {
            kount.setEnvironment(DataCollector.ENVIRONMENT_TEST);
            kount.setDebug(true);
        }

        //Run this inside it's on thread.
        (new Handler(Looper.getMainLooper()))
                .post(new Runnable() {
                    public void run() {

                        kountSessionId = UUID.randomUUID().toString();
                        kountSessionId = kountSessionId.replace("-", "");

                        kount.collectForSession(kountSessionId, new DataCollector.CompletionHandler() {
                            /* Add handler code here if desired. The handler is optional. */
                            @Override
                            public void completed(String sessionID) {
                                Log.d(TAG, "Kount DataCollector completed");
                            }

                            @Override
                            public void failed(String sessionID, final DataCollector.Error error) {
                                Log.e(TAG, "Kount DataCollector failed: " + error);
                            }
                        });
                    }
                });
    }

}
