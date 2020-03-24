package com.bluesnap.androidapi.views.components;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.SdkRequestBase;
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
     * @param billingContactInfo - {@link BillingContactInfo}
     */
    public void updateViewResourceWithDetails(@NonNull BillingContactInfo billingContactInfo) {
        super.updateViewResourceWithDetails(billingContactInfo);

        final SdkRequestBase sdkRequest = BlueSnapService.getInstance().getSdkRequest();

        if (!sdkRequest.getShopperCheckoutRequirements().isEmailRequired() || stringify(billingContactInfo.getEmail()).isEmpty())
            setEmailVisibility(GONE);
        else
            setEmailText(billingContactInfo.getEmail());

        if (!sdkRequest.getShopperCheckoutRequirements().isBillingRequired())
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
