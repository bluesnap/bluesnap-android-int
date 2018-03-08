package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class BillingViewSummarizedComponent extends ContactInfoViewSummarizedComponent {
    public static final String TAG = BillingViewSummarizedComponent.class.getSimpleName();
    private Button editButton;
    private LinearLayout forFullBillingLinearLayout;

    public BillingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BillingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BillingViewSummarizedComponent(Context context) {
        super(context);
    }

    public void updateResource(BillingInfo billingInfo) {
        super.updateResource(billingInfo);
        setEmailText(billingInfo.getEmail());
    }

    @Override
    void initControl(final Context context) {
        super.initControl(context);

        final BlueSnapService blueSnapService = BlueSnapService.getInstance();
        final SdkRequest sdkRequest = blueSnapService.getSdkRequest();

        assert sdkRequest != null;
        if (!blueSnapService.getSdkRequest().isEmailRequired()) {
            setEmailVisibility(GONE);
        }

        forFullBillingLinearLayout = (LinearLayout) findViewById(R.id.forFullBillingLinearLayout);
        if (!blueSnapService.getSdkRequest().isBillingRequired())
            forFullBillingLinearLayout.setVisibility(GONE);

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE_REQUEST, TAG);
            }
        });
    }
}
