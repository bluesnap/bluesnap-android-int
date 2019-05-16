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
     * @param fullBillingRequired  - boolean, setFullBillingRequired
     * @param emailRequired    - boolean, setEmailRequired
     */
    public TestingShopperCheckoutRequirements(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired) {
        this(fullBillingRequired, emailRequired, shippingRequired, false);
    }

    /**
     * Shopper Info Config
     *
     * @param shippingRequired      - boolean, setShippingRequired
     * @param fullBillingRequired       - boolean, setFullBillingRequired
     * @param emailRequired         - boolean, setEmailRequired
     * @param shippingSameAsBilling - boolean, setEmailRequired
     */
    public TestingShopperCheckoutRequirements(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired, boolean shippingSameAsBilling) {
        this.shippingRequired = shippingRequired;
        this.fullBillingRequired = fullBillingRequired;
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

    public void setTestingShopperCheckoutRequirements(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired) {
        setTestingShopperCheckoutRequirements(fullBillingRequired, emailRequired, shippingRequired, false);
    }

    public void setTestingShopperCheckoutRequirements(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired, boolean shippingSameAsBilling) {
        this.shippingRequired = shippingRequired;
        this.fullBillingRequired = fullBillingRequired;
        this.emailRequired = emailRequired;
        this.shippingSameAsBilling = shippingSameAsBilling;
    }

    @Override
    public String toString() {
        return "with full Billing: " + fullBillingRequired +
                ", with shipping: " + shippingRequired +
                ", with email: " + emailRequired;
    }
}
