package com.bluesnap.androidapi.services;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.ShopperCheckoutRequirements;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.utils.JsonParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.CardInfo;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

public class GooglePayService {

    private static GooglePayService instance = new GooglePayService();
    private final String TAG = GooglePayService.class.getSimpleName();

    // Changing this to ENVIRONMENT_PRODUCTION will make the API return real card information.
    // Please refer to the documentation to read about the required steps needed to enable
    // ENVIRONMENT_PRODUCTION.
    //private final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_PRODUCTION; //.ENVIRONMENT_TEST; //

    // The name of our payment processor / gateway.
    public final String GATEWAY_TOKENIZATION_NAME = "bluesnap";

    // Currently we support CARD and TOKENIZED CARD (only) for any merchant who supports GOOGLE_PAY
    public final List<Integer> SUPPORTED_METHODS = Arrays.asList(
            // PAYMENT_METHOD_CARD returns to any card the user has stored in their Google Account.
            WalletConstants.PAYMENT_METHOD_CARD,
            // PAYMENT_METHOD_TOKENIZED_CARD refers to EMV tokenized credentials stored in the
            // Google Pay app, assuming it's installed.
            // Please keep in mind tokenized cards may exist in the Google Pay app without being
            // added to the user's Google Account.
            WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD
    );


    /**
     * In case of success from the Google-Pay button, we create the encoded GPay token we
     * and post it on the HPF
     * Returns the SDK result with the shopper details and the encoded token if all OK, null if not.
     *
     * @param paymentData
     */
    public SdkResult createSDKResult(PaymentData paymentData) {

        // PaymentMethodToken contains the payment information, as well as any additional
        // requested information, such as billing and shipping address.
        // Refer to your processor's documentation on how to proceed from here.
        PaymentMethodToken token = paymentData.getPaymentMethodToken();

        // getPaymentMethodToken will only return null if PaymentMethodTokenizationParameters was
        // not set in the PaymentRequest. which is never in our case

        SdkResult sdkResult = null;
        try {
            String encodedToken = GooglePayService.getInstance().createBlsTokenFromGooglePayPaymentData(paymentData);
            Log.d(TAG, "paymentData encoded as Token for BlueSnap");

            sdkResult = BlueSnapService.getInstance().getSdkResult();
            sdkResult.setChosenPaymentMethodType(SupportedPaymentMethods.GOOGLE_PAY);
            sdkResult.setGooglePayToken(encodedToken);

            final UserAddress billingAddress = paymentData.getCardInfo().getBillingAddress();
            if (billingAddress != null) {
                BillingContactInfo billingContactInfo = new BillingContactInfo();
                billingContactInfo.setEmail(billingAddress.getEmailAddress());
                billingContactInfo.setAddress(billingAddress.getAddress1());
                billingContactInfo.setAddress2(billingAddress.getAddress2());
                billingContactInfo.setCity(billingAddress.getLocality());
                billingContactInfo.setCountry(billingAddress.getCountryCode());
                billingContactInfo.setFullName(billingAddress.getName());
                billingContactInfo.setState(billingAddress.getAdministrativeArea());
                billingContactInfo.setZip(billingAddress.getPostalCode());
                sdkResult.setBillingContactInfo(billingContactInfo);
            }

            final UserAddress shippingAddress = paymentData.getShippingAddress();
            if (shippingAddress != null) {
                ShippingContactInfo shippingContactInfo = new ShippingContactInfo();
                shippingContactInfo.setPhone(shippingAddress.getPhoneNumber());
                shippingContactInfo.setAddress(shippingAddress.getAddress1());
                shippingContactInfo.setAddress2(shippingAddress.getAddress2());
                shippingContactInfo.setCity(shippingAddress.getLocality());
                shippingContactInfo.setCountry(shippingAddress.getCountryCode());
                shippingContactInfo.setFullName(shippingAddress.getName());
                shippingContactInfo.setState(shippingAddress.getAdministrativeArea());
                shippingContactInfo.setZip(shippingAddress.getPostalCode());
                sdkResult.setShippingContactInfo(shippingContactInfo);
            }

        } catch (Exception e) {
            Log.e(TAG,"Error encoding payment data into BlueSnap token", e);
        }
        return sdkResult;
    }

    /**
     * Creates a base64 encoded token with the PaymentData
     */
    public String createBlsTokenFromGooglePayPaymentData(PaymentData paymentData) throws Exception {

        final CardInfo cardInfo = paymentData.getCardInfo();

        JSONObject result = new JSONObject();

        // paymentMethodData
        JSONObject paymentMethodData = new JSONObject();

        // paymentMethodData -> description: A payment method and method identifier suitable for communication to a shopper in a confirmation screen or purchase receipt.
        final String description = cardInfo.getCardDescription();
        if (description != null) {
            paymentMethodData.put("description", description);
        }

        // paymentMethodData -> tokenizationData
        final PaymentMethodToken paymentMethodToken = paymentData.getPaymentMethodToken();
        JSONObject tokenizationData = new JSONObject();
        tokenizationData.put("type", paymentMethodToken.getPaymentMethodTokenizationType());
        tokenizationData.put("token", paymentMethodToken.getToken());
        paymentMethodData.put("tokenizationData", tokenizationData);

        // paymentMethodData -> info
        JSONObject info = new JSONObject();
        paymentMethodData.put("info", info);

        // paymentMethodData -> info -> cardNetwork
        final String cardNetwork = cardInfo.getCardNetwork();
        if (cardNetwork != null) {
            info.put("cardNetwork", cardNetwork);
        }

        // paymentMethodData -> info -> cardDetails
        final String cardDetails = cardInfo.getCardDetails();
        if (cardDetails != null) {
            info.put("cardDetails", cardDetails);
        }

        // paymentMethodData -> info -> cardClass (1-3 or 0, should somehow translate to DEBIT/CREDIT)
        final int cardClassCode = cardInfo.getCardClass();
        String cardClass = null;
        if (cardClassCode == WalletConstants.CARD_CLASS_CREDIT) {
            cardClass = "CREDIT";
        } else if (cardClassCode == WalletConstants.CARD_CLASS_DEBIT) {
            cardClass = "DEBIT";
        } else if (cardClassCode == WalletConstants.CARD_CLASS_PREPAID) {
            cardClass = "PREPAID";
        }
        if (cardClass != null) {
            info.put("cardClass", cardClass);
        }
        // paymentMethodData -> info -> billingAddress
        final JSONObject billingAddressJson = getUserAddressAsJson(cardInfo.getBillingAddress());
        if (billingAddressJson != null) {
            info.put("billingAddress", billingAddressJson);
        }

        result.put("paymentMethodData", paymentMethodData);

        // email
        final String email = paymentData.getEmail();
        if (email != null) {
            result.put("email", email);
        }

        // googleTransactionId - not sure this is the right place in the json for it
        final String googleTransactionId = paymentData.getGoogleTransactionId();
        if (googleTransactionId != null) {
            result.put("googleTransactionId", googleTransactionId);
        }
        // shippingAddress
        final JSONObject shippingAddressJson = getUserAddressAsJson(paymentData.getShippingAddress());
        if (shippingAddressJson != null) {
            result.put("shippingAddress", shippingAddressJson);
        }

        String tokenForBls = result.toString();
        String encodedToken = Base64.encodeToString(tokenForBls.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);

        return encodedToken;
    }

    private JSONObject getUserAddressAsJson(UserAddress userAddress) throws Exception {
        JSONObject res = null;
        if (userAddress != null) {
            res = new JSONObject();
            JsonParser.putJSONifNotNull(res, "name", userAddress.getName());
            JsonParser.putJSONifNotNull(res,"postalCode", userAddress.getPostalCode());
            JsonParser.putJSONifNotNull(res,"countryCode", userAddress.getCountryCode());
            JsonParser.putJSONifNotNull(res,"phoneNumber", userAddress.getPhoneNumber());
            JsonParser.putJSONifNotNull(res,"companyName", userAddress.getCompanyName());
            JsonParser.putJSONifNotNull(res,"emailAddress", userAddress.getEmailAddress());
            JsonParser.putJSONifNotNull(res,"address1", userAddress.getAddress1());
            JsonParser.putJSONifNotNull(res,"address2", userAddress.getAddress2());
            JsonParser.putJSONifNotNull(res,"address3", userAddress.getAddress3());
            JsonParser.putJSONifNotNull(res,"address4", userAddress.getAddress4());
            JsonParser.putJSONifNotNull(res,"address5", userAddress.getAddress5());
            // A country subdivision (e.g. state or province)
            JsonParser.putJSONifNotNull(res,"administrativeArea", userAddress.getAdministrativeArea());
            // City, town, neighborhood, or suburb.
            JsonParser.putJSONifNotNull(res,"locality", userAddress.getLocality());
            JsonParser.putJSONifNotNull(res,"sortingCode", userAddress.getSortingCode());
        }
        return res;
    }


    public static GooglePayService getInstance() { return instance; }

    /**
     * Creates an instance of {@link PaymentsClient} for use in an {@link Activity} using the
     * environment and theme
     *
     * @param activity is the caller's activity.
     */
    public PaymentsClient createPaymentsClient(Activity activity) {

        // check that Google Play is available
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity.getBaseContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            boolean isUserResolvableError = googleApiAvailability.isUserResolvableError(resultCode);
            Log.i(TAG,"Google Play not available; resultCode=" + resultCode + ", isUserResolvableError=" + isUserResolvableError);
            return null;
        }

        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        SdkRequestBase sdkRequest = blueSnapService.getSdkRequest();
        int googlePayMode = WalletConstants.ENVIRONMENT_PRODUCTION;
        if (sdkRequest.isGooglePayTestMode()) {
            googlePayMode = WalletConstants.ENVIRONMENT_TEST;
        }
        // Create the client
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(googlePayMode)
                .build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    /**
     * Builds {@link PaymentDataRequest} to be consumed by {@link PaymentsClient#loadPaymentData}.
     */
    public Task<PaymentData> createPaymentDataRequest(PaymentsClient googlePayClient) {

        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        SdkRequestBase sdkRequest = blueSnapService.getSdkRequest();
        Long merchantId = blueSnapService.getsDKConfiguration().getMerchantId();
        if (merchantId == null) {
            Log.e(TAG, "Missing merchantId from SDK init data");
            return null;
        }

        List<Pair<String, String>> GATEWAY_TOKENIZATION_PARAMETERS = Arrays.asList(
                Pair.create("gatewayMerchantId", merchantId.toString())
        );

        PaymentMethodTokenizationParameters.Builder paramsBuilder =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                        .addParameter("gateway", GATEWAY_TOKENIZATION_NAME);
        for (Pair<String, String> param : GATEWAY_TOKENIZATION_PARAMETERS) {
            paramsBuilder.addParameter(param.first, param.second);
        }

        PaymentDataRequest request = createPaymentDataRequest(paramsBuilder.build(), sdkRequest);
        Task<PaymentData> futurePaymentData = googlePayClient.loadPaymentData(request);
        return futurePaymentData;
    }


    private PaymentDataRequest createPaymentDataRequest(PaymentMethodTokenizationParameters params, SdkRequestBase sdkRequest) {

        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        // AS-149: Google Pay price does not allow more than 2 digits after the decimal point
        String price = String.format("%.2f", (priceDetails != null)? priceDetails.getAmount() : 0.0);
        TransactionInfo transactionInfo = createTransaction(price, (priceDetails != null)? priceDetails.getCurrencyCode() : "USD");

        final List merchantPaymentMethods = getMerchantPaymentMethods();

        ShopperCheckoutRequirements shopperCheckoutRequirements = sdkRequest.getShopperCheckoutRequirements();
        PaymentDataRequest request =
                PaymentDataRequest.newBuilder()
                        .setPhoneNumberRequired(shopperCheckoutRequirements.isShippingRequired())
                        .setEmailRequired(shopperCheckoutRequirements.isEmailRequired())
                        .setShippingAddressRequired(shopperCheckoutRequirements.isShippingRequired())

                        // Omitting ShippingAddressRequirements all together means all countries are
                        // supported.
                        //.setShippingAddressRequirements(
                        //        ShippingAddressRequirements.newBuilder()
                        //                .addAllowedCountryCodes(Constants.SHIPPING_SUPPORTED_COUNTRIES)
                        //                .build())

                        .setTransactionInfo(transactionInfo)
                        .addAllowedPaymentMethods(SUPPORTED_METHODS)
                        .setCardRequirements(
                                CardRequirements.newBuilder()
                                        .addAllowedCardNetworks(merchantPaymentMethods)
                                        .setAllowPrepaidCards(true) // todo: need to find out wehat this means
                                        .setBillingAddressRequired(true)

                                        // Omitting this parameter will result in the API returning
                                        // only a "minimal" billing address (post code only).
                                        .setBillingAddressFormat(shopperCheckoutRequirements.isBillingRequired() ? WalletConstants.BILLING_ADDRESS_FORMAT_FULL : WalletConstants.BILLING_ADDRESS_FORMAT_MIN)
                                        .build())
                        .setPaymentMethodTokenizationParameters(params)

                        // If the UI is not required, a returning user will not be asked to select
                        // a card. Instead, the card they previously used will be returned
                        // automatically (if still available).
                        // Prior whitelisting is required to use this feature.
                        .setUiRequired(true)
                        .build();

        return request;
    }

    /**
     * Returns the allowed networks, based on the merchant info.
     * The allowed networks to be requested from Google-Pay API. If the user has cards from networks not
     * specified here in their account, these will not be offered for them to choose in the popup.
     *
     * @return
     */
    private List<Integer> getMerchantPaymentMethods() {

        ArrayList<String> creditCardBrands = BlueSnapService.getInstance().getsDKConfiguration().getSupportedPaymentMethods().getCreditCardBrands();

        List<Integer> supportedNetworks = new java.util.ArrayList<>();

        for (String ccBrand : creditCardBrands) {
            if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.VISA)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_VISA);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.AMEX)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_AMEX);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.DISCOVER)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_DISCOVER);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.JCB)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_JCB);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.MASTERCARD)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_MASTERCARD);
            } else {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_OTHER);
            }
        }

        return supportedNetworks;
    }

    /**
     * Determines if the user is eligible to Pay with Google by calling
     * {@link PaymentsClient#isReadyToPay}. The nature of this check depends on the methods set in
     * {@link #SUPPORTED_METHODS}.
     * <p>
     * If {@link WalletConstants#PAYMENT_METHOD_CARD} is specified among supported methods, this
     * function will return true even if the user has no cards stored. Please refer to the
     * documentation for more information on how the check is performed.
     *
     * @param client used to send the request.
     */
    public Task<Boolean> isReadyToPay(PaymentsClient client) {

        IsReadyToPayRequest.Builder request = IsReadyToPayRequest.newBuilder();
        for (Integer allowedMethod : SUPPORTED_METHODS) {
            request.addAllowedPaymentMethod(allowedMethod);
        }
        Task<Boolean> readyToPay = client.isReadyToPay(request.build());
        return readyToPay;
    }

    /**
     * Builds {@link TransactionInfo} for use with {@link #createPaymentDataRequest}.
     * <p>
     * The price is not displayed to the user and must be in the following format: "12.34".
     *
     * @param price total of the transaction.
     */
    public TransactionInfo createTransaction(String price, String currency) {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_ESTIMATED)
                .setTotalPrice(price)
                .setCurrencyCode(currency)
                .build();
    }

}
