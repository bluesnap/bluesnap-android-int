package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.KountService;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalObject;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * A representation of server exchange rate.
 */
public class Shopper extends ContactInfo {
    private static final String TAG = Shopper.class.getSimpleName();

    public static final String LAST_PAYMENT_INFO = "lastPaymentInfo";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String SHOPPER_CURRENCY = "shopperCurrency";
    public static final String VAULTED_SHOPPER_ID = "vaultedShopperId";
    public static final String PAYMENT_SOURCES = "paymentSources";
    public static final String SHIPPING_CONTACT_INFO = "shippingContactInfo";
    public static final String CHOSEN_PAYMENT_METHOD = "chosenPaymentMethod";
    public static final String TRANSACTION_FRAUD_INFO = "transactionFraudInfo";
    public static final String FRAUD_SESSION_ID = "fraudSessionId";

    private int vaultedShopperId;
    @Nullable
    private String email;
    @Nullable
    private String phone;
    private String shopperCurrency;
    @Nullable
    private PaymentSources previousPaymentSources;
    @Nullable
    private PaymentSources newPaymentSources;
    @Nullable
    private ShippingContactInfo shippingContactInfo;
    @Nullable
    private LastPaymentInfo lastPaymentInfo;
    @NonNull
    private ChosenPaymentMethod chosenPaymentMethod = new ChosenPaymentMethod();
    @Nullable
    private CreditCardInfo newCreditCardInfo;
    private boolean storeCard = false;

    public Shopper(ContactInfo contactInfo) {
        setFirstName(contactInfo.getFirstName());
        setLastName(contactInfo.getLastName());
        setAddress(contactInfo.getAddress());
        setAddress2(contactInfo.getAddress2());
        setZip(contactInfo.getZip());
        setCity(contactInfo.getCity());
        setState(contactInfo.getState());
        setCountry(contactInfo.getCountry());
        newCreditCardInfo = new CreditCardInfo();
    }

    public Shopper() {
        shippingContactInfo = new ShippingContactInfo();
        newCreditCardInfo = new CreditCardInfo();
    }

    @Nullable
    public CreditCardInfo getNewCreditCardInfo() {
        return newCreditCardInfo;
    }

    public void setNewCreditCardInfo(CreditCardInfo newCreditCardInfo) {
        this.newCreditCardInfo = newCreditCardInfo;
    }

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    @Nullable
    public PaymentSources getPreviousPaymentSources() {
        return previousPaymentSources;
    }

    public void setPreviousPaymentSources(@Nullable PaymentSources previousPaymentSources) {
        this.previousPaymentSources = previousPaymentSources;
    }

    @NonNull
    public ShippingContactInfo getShippingContactInfo() {
        if (null == shippingContactInfo)
            shippingContactInfo = new ShippingContactInfo();
        return shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable ShippingContactInfo shippingContactInfo) {
        this.shippingContactInfo = shippingContactInfo;
    }

    public void setShippingContactInfo(@Nullable BillingContactInfo billingContactInfo) {
        if (shippingContactInfo == null || billingContactInfo == null) {
            Log.w(TAG, "Cannot setShippingContactInfo, either shipping or billing is null");
        } else {
            this.shippingContactInfo.setFullName(billingContactInfo.getFullName());
            this.shippingContactInfo.setAddress(billingContactInfo.getAddress());
            this.shippingContactInfo.setAddress2(billingContactInfo.getAddress2());
            this.shippingContactInfo.setZip(billingContactInfo.getZip());
            this.shippingContactInfo.setCity(billingContactInfo.getCity());
            this.shippingContactInfo.setState(billingContactInfo.getState());
            this.shippingContactInfo.setCountry(billingContactInfo.getCountry());
        }
    }

    @Nullable
    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(@Nullable LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }

    @Nullable
    public PaymentSources getNewPaymentSources() {
        return newPaymentSources;
    }

    public void setNewPaymentSources(@Nullable PaymentSources newPaymentSources) {
        this.newPaymentSources = newPaymentSources;
    }

    public boolean isStoreCard() {
        return storeCard;
    }

    public void setStoreCard(boolean storeCard) {
        this.storeCard = storeCard;
    }

    @Nullable
    public static Shopper fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        ContactInfo contactInfo = ContactInfo.fromJson(jsonObject);
        Shopper shopper;

        if (contactInfo != null)
            shopper = new Shopper(contactInfo);
        else
            shopper = new Shopper();

        shopper.setEmail(getOptionalString(jsonObject, EMAIL));
        shopper.setPhone(getOptionalString(jsonObject, PHONE));
        shopper.setShopperCurrency(getOptionalString(jsonObject, SHOPPER_CURRENCY));
        shopper.setVaultedShopperId(Integer.parseInt(getOptionalString(jsonObject, VAULTED_SHOPPER_ID)));
        shopper.setLastPaymentInfo(LastPaymentInfo.fromJson(getOptionalObject(jsonObject, LAST_PAYMENT_INFO)));
        shopper.setPreviousPaymentSources(PaymentSources.fromJson(getOptionalObject(jsonObject, PAYMENT_SOURCES)));
        shopper.setShippingContactInfo(ShippingContactInfo.fromJson(getOptionalObject(jsonObject, SHIPPING_CONTACT_INFO)));
        //shopper.setShippingContactInfo(BillingContactInfo.fromJson(getOptionalObject(jsonObject, SHIPPING_CONTACT_INFO)));
        if (shopper.previousPaymentSources != null && shopper.previousPaymentSources.getCreditCardInfos() != null && shopper.previousPaymentSources.getCreditCardInfos().size() > 0)
            shopper.setNewCreditCardInfo(shopper.previousPaymentSources.getCreditCardInfos().get(0));
        shopper.setChosenPaymentMethod(ChosenPaymentMethod.fromJson(getOptionalObject(jsonObject, CHOSEN_PAYMENT_METHOD)));

        return shopper;
    }

    /**
     * create JSON object from Shopper
     *
     * @return JSONObject
     */
    @NonNull
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        putJSONifNotNull(jsonObject, EMAIL, getEmail());
        putJSONifNotNull(jsonObject, PHONE, getPhone());
        putJSONifNotNull(jsonObject, SHOPPER_CURRENCY, getShopperCurrency());
        putJSONifNotNull(jsonObject, VAULTED_SHOPPER_ID, getVaultedShopperId());
        putJSONifNotNull(jsonObject, PAYMENT_SOURCES, getNewPaymentSources());
        putJSONifNotNull(jsonObject, SHIPPING_CONTACT_INFO, getShippingContactInfo());
        putJSONifNotNull(jsonObject, CHOSEN_PAYMENT_METHOD, getChosenPaymentMethod());

        String fraudSessionId = KountService.getInstance().getKountSessionId();
        if (null != fraudSessionId && !"".equals(fraudSessionId)) {
            JSONObject transactionFraudInfo = new JSONObject();
            putJSONifNotNull(transactionFraudInfo, FRAUD_SESSION_ID, fraudSessionId);
            putJSONifNotNull(jsonObject, TRANSACTION_FRAUD_INFO, transactionFraudInfo);
        }

        return jsonObject;
    }

    @NonNull
    public ChosenPaymentMethod getChosenPaymentMethod() {
        return chosenPaymentMethod;
    }

    public void setChosenPaymentMethod(@NonNull ChosenPaymentMethod chosenPaymentMethod) {
        this.chosenPaymentMethod = chosenPaymentMethod;
    }
}
