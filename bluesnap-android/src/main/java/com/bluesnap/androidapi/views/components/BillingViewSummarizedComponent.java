package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

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
    public Button editButton;

    public BillingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BillingViewSummarizedComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BillingViewSummarizedComponent(Context context) {
        super(context);
    }

    /**
     * updates BillingViewSummarizedComponent with details
     *
     * @param billingInfo - {@link BillingInfo}
     */
    public void updateViewResourceWithDetails(@NonNull BillingInfo billingInfo) {
        super.updateViewResourceWithDetails(billingInfo);

        final SdkRequest sdkRequest = BlueSnapService.getInstance().getSdkRequest();

        if (!sdkRequest.isEmailRequired() || stringify(billingInfo.getEmail()).isEmpty())
            setEmailVisibility(GONE);
        else
            setEmailText(billingInfo.getEmail());

        if (!sdkRequest.isBillingRequired())
            forFullBillingLinearLayout.setVisibility(GONE);
    }

    @Override
    void initControl(final Context context) {
        super.initControl(context);

        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT, TAG);
            }
        });
    }
}
