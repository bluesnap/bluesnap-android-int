package com.bluesnap.android.demoapp;

/**
 * Created by sivani on 09/08/2018.
 */

public class ReturningShoppersFactory {
    public static class Shopper {
        private String shopperDescription; //which option is this shopper (from the 8 possible)
        private String shopperId;
        private boolean fullBilling;
        private boolean withEmail;
        private boolean withShipping;
        private ShopperContactInfo billingContactInfo;
        private ShopperContactInfo shippingContactInfo;

        public Shopper(String shopperDescription_, String shopperId_, String billingCountry_, String shippingCountry_) {
            shopperDescription = shopperDescription_;
            shopperId = shopperId_;
            billingContactInfo = new ShopperContactInfo(ContactInfoTesterCommon.billingContactInfo);
            billingContactInfo.setCountry(billingCountry_);
            shippingContactInfo = new ShopperContactInfo(ContactInfoTesterCommon.shippingContactInfo);
            shippingContactInfo.setCountry(shippingCountry_);

        }

        public String getShopperDescription() {
            return shopperDescription;
        }

        public String getShopperId() {
            return shopperId;
        }

        public ShopperContactInfo getBillingContactInfo() {
            return billingContactInfo;
        }

        public ShopperContactInfo getShippingContactInfo() {
            return shippingContactInfo;
        }

        public boolean isFullBilling() {
            return fullBilling;
        }

        public boolean isWithEmail() {
            return withEmail;
        }

        public boolean isWithShipping() {
            return withShipping;
        }

        public void setFullBilling(boolean fullBilling) {
            this.fullBilling = fullBilling;
        }

        public void setWithEmail(boolean withEmail) {
            this.withEmail = withEmail;
        }

        public void setWithShipping(boolean withShipping) {
            this.withShipping = withShipping;
        }
    }

    private static final String[] returningShopperOptions = {"RETURNING_SHOPPER_MIN_BILLING", "RETURNING_SHOPPER_MIN_BILLING_WITH_EMAIL", "RETURNING_SHOPPER_MIN_BILLING_WITH_SHIPPING",
            "RETURNING_SHOPPER_MIN_BILLING_WITH_SHIPPING_WITH_EMAIL", "RETURNING_SHOPPER_FULL_BILLING", "RETURNING_SHOPPER_FULL_BILLING_WITH_EMAIL",
            "RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING", "RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING_WITH_EMAIL"};

    private static final String[] returningShopperIDs = {"22876609", "22852991", "22862697", "22862837", "", "", "", ""};

    private static final String[] returningShopperBillingCountries = {"IL", "SI", "MD", "NP", "", "", "", ""};
    private static final String[] returningShopperShippingCountries = {"", "", "CH", "TH", "", "", "", ""};


    private static Shopper[] shoppers = new Shopper[8];

    protected static int COUNTER = 0;
    //private static Map<String,String> shoppersIds = new HashMap();

    static {
        String bitCode;

        for (int i = 0; i < 8; i++) {
            bitCode = Integer.toBinaryString(i);
            for (int j = bitCode.length(); j < 3; j++)
                bitCode = "0" + bitCode;
            shoppers[i] = new Shopper(returningShopperOptions[i], returningShopperIDs[i], returningShopperBillingCountries[i], returningShopperShippingCountries[i]);
            setFlags(shoppers[i], bitCode);
        }
    }

    public static void setFlags(Shopper shopper, String bitCode) {
        boolean fullBilling = getBooleanFromChar(bitCode.charAt(0));
        boolean withShipping = getBooleanFromChar(bitCode.charAt(1));
        boolean withEmail = getBooleanFromChar(bitCode.charAt(2));

        shopper.setFullBilling(fullBilling);
        shopper.setWithEmail(withEmail);
        shopper.setWithShipping(withShipping);
    }

    public static boolean getBooleanFromChar(char x) {
        return x == '0' ? false : true;
    }


    public static Shopper getReturningShopper() {
        Shopper returningShopper = shoppers[COUNTER];
        COUNTER++;
        if (COUNTER == 4)
            COUNTER = 0;
        return returningShopper;
    }

}
