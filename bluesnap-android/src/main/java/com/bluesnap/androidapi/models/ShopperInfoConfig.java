package com.bluesnap.androidapi.models;

public class ShopperInfoConfig {

    private boolean shippingRequired;
    private boolean billingRequired;
    private boolean emailRequired;

    /**
     * Shopper Info Config
     *
     * @param shippingRequired - boolean, setShippingRequired
     * @param billingRequired  - boolean, setBillingRequired
     * @param emailRequired    - boolean, setEmailRequired
     */
    public ShopperInfoConfig(boolean shippingRequired, boolean billingRequired, boolean emailRequired) {
        this.shippingRequired = shippingRequired;
        this.billingRequired = billingRequired;
        this.emailRequired = emailRequired;
    }

    /**
     * Shopper Info Config
     *
     * @param shopperInfoConfig - set Shopper Config Requirement {@link ShopperInfoConfig}
     */
    public ShopperInfoConfig(ShopperInfoConfig shopperInfoConfig) {
        this.shippingRequired = shopperInfoConfig.isShippingRequired();
        this.billingRequired = shopperInfoConfig.isBillingRequired();
        this.emailRequired = shopperInfoConfig.isEmailRequired();
    }

    /**
     * Shopper Info Config - set Shopper Config Requirement to false
     */
    public ShopperInfoConfig() {
        this(false, false, false);
    }

    /**
     * isShippingRequired
     *
     * @return boolean
     */
    public boolean isShippingRequired() {
        return shippingRequired;
    }

    /**
     * setShippingRequired
     *
     * @param shippingRequired - boolean
     */
    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    /**
     * isBillingRequired
     *
     * @return boolean
     */
    public boolean isBillingRequired() {
        return billingRequired;
    }

    /**
     * setBillingRequired
     *
     * @param billingRequired - boolean
     */
    public void setBillingRequired(boolean billingRequired) {
        this.billingRequired = billingRequired;
    }

    /**
     * isEmailRequired
     *
     * @return boolean
     */
    public boolean isEmailRequired() {
        return emailRequired;
    }

    /**
     * setEmailRequired
     *
     * @param emailRequired - boolean
     */
    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

}
