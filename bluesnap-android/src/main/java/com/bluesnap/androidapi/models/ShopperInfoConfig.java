package com.bluesnap.androidapi.models;

public class ShopperInfoConfig {

    private boolean shippingRequired;
    private boolean billingRequired;
    private boolean emailRequired;

    public ShopperInfoConfig(boolean shippingRequired, boolean billingRequired, boolean emailRequired) {

        this.shippingRequired = shippingRequired;
        this.billingRequired = billingRequired;
        this.emailRequired = emailRequired;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public boolean isBillingRequired() {
        return billingRequired;
    }

    public void setBillingRequired(boolean billingRequired) {
        this.billingRequired = billingRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

}
