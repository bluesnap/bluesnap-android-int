package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.models.ShopperCheckoutRequirements;

/**
 * Created by sivani on 03/09/2018.
 */

public class TestingShopperCheckoutRequirements {
    private boolean shippingRequired;
    private boolean fullBillingRequired;
    private boolean emailRequired;
    private boolean shippingSameAsBilling;


    /**
     * Shopper Info Config
     *
     * @param shippingRequired - boolean, setShippingRequired
     * @param billingRequired  - boolean, setFullBillingRequired
     * @param emailRequired    - boolean, setEmailRequired
     */
    public TestingShopperCheckoutRequirements(boolean shippingRequired, boolean billingRequired, boolean emailRequired) {
        this(shippingRequired, billingRequired, emailRequired, false);
    }

    /**
     * Shopper Info Config
     *
     * @param shippingRequired      - boolean, setShippingRequired
     * @param billingRequired       - boolean, setFullBillingRequired
     * @param emailRequired         - boolean, setEmailRequired
     * @param shippingSameAsBilling - boolean, setEmailRequired
     */
    public TestingShopperCheckoutRequirements(boolean shippingRequired, boolean billingRequired, boolean emailRequired, boolean shippingSameAsBilling) {
        this.shippingRequired = shippingRequired;
        this.fullBillingRequired = billingRequired;
        this.emailRequired = emailRequired;
        this.shippingSameAsBilling = shippingSameAsBilling;
    }

    /**
     * Shopper Info Config
     *
     * @param testingShopperCheckoutRequirements - set Shopper Config Requirement {@link ShopperCheckoutRequirements}
     */
    public TestingShopperCheckoutRequirements(TestingShopperCheckoutRequirements testingShopperCheckoutRequirements) {
        this.shippingRequired = testingShopperCheckoutRequirements.isShippingRequired();
        this.fullBillingRequired = testingShopperCheckoutRequirements.isFullBillingRequired();
        this.emailRequired = testingShopperCheckoutRequirements.isEmailRequired();
        this.shippingSameAsBilling = testingShopperCheckoutRequirements.isShippingSameAsBilling();
        ;

    }

    /**
     * Shopper Info Config - set Shopper Config Requirement to false
     */
    public TestingShopperCheckoutRequirements() {
        this(false, false, false, false);
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public boolean isFullBillingRequired() {
        return fullBillingRequired;
    }

    public void setFullBillingRequired(boolean fullBillingRequired) {
        this.fullBillingRequired = fullBillingRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public boolean isShippingSameAsBilling() {
        return shippingSameAsBilling;
    }

    public void setShippingSameAsBilling(boolean shippingSameAsBilling) {
        this.shippingSameAsBilling = shippingSameAsBilling;
    }
}
