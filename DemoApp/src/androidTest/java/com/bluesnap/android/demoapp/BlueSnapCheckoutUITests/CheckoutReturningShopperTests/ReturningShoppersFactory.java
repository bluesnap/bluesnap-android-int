package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.TestingShopperContactInfo;

/**
 * Created by sivani on 09/08/2018.
 */

public class ReturningShoppersFactory {
    public static class TestingShopper {
        private String shopperDescription; //which option is this shopper (from the 8 possible)
        private String shopperId;
        private boolean fullBilling;
        private boolean withEmail;
        private boolean withShipping;
        private TestingShopperContactInfo billingContactInfo;
        private TestingShopperContactInfo shippingContactInfo;

        TestingShopper(String shopperDescription, String shopperId, String billingCountry, String shippingCountry) {
            this.shopperDescription = shopperDescription;
            this.shopperId = shopperId;
            this.billingContactInfo = new TestingShopperContactInfo(ContactInfoTesterCommon.billingContactInfo);
            this.billingContactInfo.setCountryKey(billingCountry);
            this.shippingContactInfo = new TestingShopperContactInfo(ContactInfoTesterCommon.shippingContactInfo);
            this.shippingContactInfo.setCountryKey(shippingCountry);
        }

        String getShopperDescription() {
            return shopperDescription;
        }

        public String getShopperId() {
            return shopperId;
        }

        TestingShopperContactInfo getBillingContactInfo() {
            return billingContactInfo;
        }

        TestingShopperContactInfo getShippingContactInfo() {
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

        void setFullBilling(boolean fullBilling) {
            this.fullBilling = fullBilling;
        }

        void setWithEmail(boolean withEmail) {
            this.withEmail = withEmail;
        }

        void setWithShipping(boolean withShipping) {
            this.withShipping = withShipping;
        }
    }

    private static final String[] returningShopperOptions = {"RETURNING_SHOPPER_MIN_BILLING", "RETURNING_SHOPPER_MIN_BILLING_WITH_EMAIL", "RETURNING_SHOPPER_MIN_BILLING_WITH_SHIPPING",
            "RETURNING_SHOPPER_MIN_BILLING_WITH_SHIPPING_WITH_EMAIL", "RETURNING_SHOPPER_FULL_BILLING", "RETURNING_SHOPPER_FULL_BILLING_WITH_EMAIL",
            "RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING", "RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING_WITH_EMAIL"};

    private static final String[] returningShopperIDs = {"22876609", "22852991", "22862697", "22942031", "29632258", "29632260", "29632268", "29632264"};

    private static final String[] returningShopperBillingCountries = {"IL", "SI", "MD", "US", "FJ", "MT", "CL", "LT"};
    private static final String[] returningShopperShippingCountries = {"", "", "CH", "BR", "", "", "GA", "KH"};


    private static TestingShopper[] shoppers = new TestingShopper[8];

    public static int COUNTER = 0;
    //private static Map<String,String> shoppersIds = new HashMap();

    static {
        String bitCode;

        for (int i = 0; i < 8; i++) {
            bitCode = Integer.toBinaryString(i);
            for (int j = bitCode.length(); j < 3; j++)
                bitCode = "0" + bitCode;
            shoppers[i] = new TestingShopper(returningShopperOptions[i], returningShopperIDs[i], returningShopperBillingCountries[i], returningShopperShippingCountries[i]);
            setFlags(shoppers[i], bitCode);
        }
    }

    private static void setFlags(TestingShopper shopper, String bitCode) {
        boolean fullBilling = getBooleanFromChar(bitCode.charAt(0));
        boolean withShipping = getBooleanFromChar(bitCode.charAt(1));
        boolean withEmail = getBooleanFromChar(bitCode.charAt(2));

        shopper.setFullBilling(fullBilling);
        shopper.setWithEmail(withEmail);
        shopper.setWithShipping(withShipping);
    }

    private static boolean getBooleanFromChar(char x) {
        return x != '0';
    }


    public static TestingShopper getReturningShopper() {
        TestingShopper returningShopper = shoppers[COUNTER];
        COUNTER++;
        if (COUNTER == 8)
            COUNTER = 0;
        return returningShopper;
    }

}
