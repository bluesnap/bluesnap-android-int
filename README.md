## bluesnap-android

[![Build Status-Master](https://travis-ci.org/bluesnap/bluesnap-android-int.svg?branch=master)](https://travis-ci.org/bluesnap/bluesnap-android-int)
[![Build Status-Develop](https://travis-ci.org/bluesnap/bluesnap-android-int.svg?branch=develop)](https://travis-ci.org/bluesnap/bluesnap-android-int)

# About
BlueSnap's Android SDK enables you to easily accept credit card, Google Pay and PayPal payments directly from your Android app, and then process payments from your server using the Payment API. When you use this library, BlueSnap handles most of the PCI compliance burden for you, as the shopper's payment data is tokenized and sent directly to BlueSnap's servers.

# Versions
This SDK supports Android SDK 25 and above for development. The minimum Android API version for applications is 18, which covers more than 98% of the [Android devices](https://developer.android.com/about/dashboards/index.html).

# Installation

## Android Studio (Gradle) instructions
To get started, add the following line in your `build.gradle` file in the dependencies section:

    compile 'com.bluesnap:bluesnap-android:[version]'

# Available flows for collecting payment details

There are 3 supported flow types:
* Checkout: Collect shopper and payment details for a transaction
* Choose Payment method: Collect the shopper's Chosen Payment Method
* Create Payment: Collect the shopper's payment details for a transaction, based on the Chosen Payment Method
"Choose Payment method" and "Create Payment" flows work together.

## Checkout flow: Collect shopper and payment details for a transaction
This flow is the most commonly used: the shopper chooses to buy something from you app, and you call bluesnap SDK BluesnapCheckoutActivity to handle the UI and data-transmission to BlueSnap; the shopper is asked to choose the payment method (CC/Google Pay/PayPal) and then fills the relevant payment details. The shopper's payment data is tokenized and sent directly to BlueSnap's servers. The activity result returns some of the (non secure) information, but mainly all you need to do now is to complete the transaction: let your app server do an API call to BlueSnap, sending just the token, and the transaction will be completed. In case of PayPal, you don't even have to do that, since PayPal flow already completes the transaction.
Steps required for this flow (more information on each below):
* Generate a token for the transaction
* Initialize the SDK with the token
* Launch BluesnapCheckoutActivity and collect shopper payment info
* Get the SdkResult
* Complete the transaction

## Choose Payment method flow: Collect the shopper's Chosen Payment Method
This flow will be used in apps where you wish to save the shopper's payment details upon registration, and use it later in a quick and easy fashion. This flow can be run only for an existing shopper (you can easily create the shopper using BlueSnap API).
If the shopper chooses a Credit card, we collect the billing details and store them on BlueSnap servers, so that the (later) charge will not require the shopper to type any information. If the shopper chooses Google Pay or PayPal, we simply keep this preference, so that in the next step (Create Payment flow), the shopper will automatically get the GooglePay pop-up or the PayPal page.
Steps required for this flow (more information on each below):
* Generate a token for the transaction (for the existing shopper)
* Initialize the SDK with the token
* Launch the BluesnapChoosePaymentMethodActivity to collect shopper chosen payment info
* Once you get the activity result, there is nothing else for you to do beside check that it was successful; if successful, then the shopper details are already updated on BlueSnap servers.

## Create Payment flow: Collect the shopper's payment details for a transaction, based on the Chosen Payment Method
This is where you quickly collect the shopper's chosen payment details; The shopper's payment data is tokenized and sent directly to BlueSnap's servers. This flow can be run only for an existing shopper that HAS valid chosen payment details.
If (in the previous step: Choose Payment method flow) the shopper chose a credit card, you will get the response immediately; if the shopper chose Google Pay or PayPal, they will now do the payment flow). In both cases, the result is the same as in the Checkout flow: The SDK result returns some (non secure) information, but mainly all you need to do now is let your app server do an API call to BlueSnap, sending just the token, and the transaction will be completed. In case of PayPal, you don't even have to do that, since PayPal flow already completes the transaction.
Steps required for this flow (more information on each below):
* Generate a token for the transaction (for the existing shopper)
* Initialize the SDK with the token
* Launch the BluesnapCreatePaymentActivity to collect shopper payment info based on previously chosen payment method 
* Get the SdkResult
* Complete the transaction

# Usage

## Generate a token for the transaction
For each transaction, you need to generate a Hosted Payment Fields token on your server side and pass it to the SDK.

To do this, initiate a server to server POST request with your credentials and send it to the relevant URL for the Sandbox or Production environment:

* **Sandbox:** `https://sandbox.bluesnap.com/services/2/payment-fields-tokens`
* **Production:** `https://ws.bluesnap.com/services/2/payment-fields-tokens`

> **Specifying a returning user** <br>
>To specify a returning user and have BlueSnap pre-populate the checkout page with their information, include the parameter `shopperId` in the URL query string. For example: `https://sandbox.bluesnap.com/services/2/payment-fields-tokens?shopperId=20848977`

The token is returned in the Location header in the response. For more information, see [Create Hosted Payment Fields Token](http://developers.bluesnap.com/v4.0/docs/create-hosted-payment-fields-token). 

## Initialize the SDK with the token
To initiate the SDK, prepare it for rate conversion, and intialize the required objects for the user interface, pass your token to the `setup` method of the `BlueSnapService` class, along with the following: 

* `tokenProvider()` - Callback function that handles token expiration by creating a new token. 
* `merchantStoreCurrency` - [ISO 4217](https://developers.bluesnap.com/docs/currency-codes) currency code of your base currency. Default value is USD. 
* `getApplicationContext()` - Context of your application, which is used for fraud prevention purposes. 
* `new BluesnapServiceCallback() {...}` - Callback function that is invoked after the setup process finishes, either resulting in a success or failure (`setup` is an async function). 

```
BlueSnapService.getInstance().setup("MERCHANT_TOKEN_STRING", tokenProvider(), "<merchantStoreCurrency>", getApplicationContext(), new BluesnapServiceCallback() {
    @Override
    public void onSuccess() {
        // Do onSuccess
    }        
    @Override
    public void onFailure() {
        // Do onFailure
    }
});
```

**Note:**  For each purchase, you'll need to call setup() 

## Launch the BluesnapCheckoutActivity and collect shopper payment info (Checkout flow)
The SDK includes a pre-built checkout form, enabling you to easily collect the shopper's information. You won't have to handle card number or expiration date validations, or the storage of sensitive information. 

To launch the checkout flow, you'll create an `SdkRequest` instance with the purchase amount and currency, and then start the `BluesnapCheckoutActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method

## Launch the BluesnapChoosePaymentMethodActivity to collect shopper chosen payment info (Choose Payment method flow)
The SDK includes a pre-built form, enabling you to easily collect the shopper's chosen payment information. You won't have to handle card number or expiration date validations, or the storage of sensitive information. 

To launch this flow, you'll create an `SdkRequest` instance (without the purchase amount and currency), and then start the `BluesnapChoosePaymentMethodActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method

## Launch the BluesnapCreatePaymentActivity to collect shopper payment info based on previously chosen payment method (Create Payment flow)
The SDK enables you to easily collect the shopper's information that was saved before (in case the chosen payment method is a credit card; if the chosen payment type was PayPal or Google Pay, the flow will collect the required details similar to the checkout flow). You won't have to handle validations or the storage of sensitive information. 

To launch this flow, you'll create an `SdkRequest` instance (with the purchase amount and currency), and then start the `BluesnapCreatePaymentActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method

### Create an SdkRequest instance 
An `SdkRequest` instance is required to pass information about the purchase to the SDK.
The instance must include:
 - Price Details (for 1st and 3rd flow):
   -- the current purchase amount 
   -- purchase currency (as an [ISO 4217](https://developers.bluesnap.com/docs/currency-codes) currency code)
 - Shopper Checkout Requirements
   -- billingRequired: if false, the SDK will collect name and country; if true, it will collect also the billing address.
   -- emailRequired: if true, the SDK will collect email as part of the billing information.
   -- shippingRequired: if true, the SDK will collect shipping details.
 
```
SdkRequest sdkRequest = new SdkRequest(Double amount, String currencyNameCode,  boolean billingRequired, boolean emailRequired, boolean shippingRequired)
```
An `SdkRequest` instance contain also an `allowCurrencyChange` property: if true, the SDK will allow the shopper to change the purchase currency. By defult it is true; if you wish to prevent your shoppers from changing the currency, you can specifically change this value like this:
```
sdkRequest.setAllowCurrencyChange(false);
```
An `SdkRequest` instance contain also a `googlePayTestMode` property: if true (default), Google Pay flow will work in TEST mode, which means any card you enter will result in dummy card details.
if you set it to false, the SDK will instatiate Google Pay in PRODUCTION mode, which requires Google's approval of the app. If your app is not approved and you set to PRODUCTION mode, when clicking on the Google Pay button, you will get a pop-up saying "This merchant is not enabled for Google Pay"). 
Google Pay TEST mode is only supported in BlueSnap's Sandbox environment; if you try it in production, the app flow will work, but your transaction will fail.
You can specifically change this value like this:
```
sdkRequest.setGooglePayTestMode(false);
```

#### Handling tax updates in checkout flow (optional)
If you choose to collect shipping details (i.e. withShipping is set to true), 
then you may want to update tax rates whenever the user changes their shipping location. 
Supply a callback function to handle tax updates to the updateTaxFunc property of sdkRequest. 
Your function will be called whenever the user changes their shipping country or state. 
To see an example, check out updateTax in the demo app.

```
// Set special tax policy: non-US pay no tax; MA pays 10%, other US states pay 5%
        sdkRequest.setTaxCalculator(new TaxCalculator() {
            @Override
            public void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails) {
                if ("us".equalsIgnoreCase(shippingCountry)) {
                    Double taxRate = 0.05;
                    if ("ma".equalsIgnoreCase(shippingState)) {
                        taxRate = 0.1;
                    }
                    priceDetails.setTaxAmount(priceDetails.getSubtotalAmount() * taxRate);
                } else {
                    priceDetails.setTaxAmount(0D);
                }
            }
        });
```

#### Specify required information 
Even after sdkRequest was created, you can still change it to collect additional information:

For shipping information, call the `setShippingRequired` method of the `SdkRequest` class:
```
sdkRequest.getShopperCheckoutRequirements().setShippingRequired(true);
```
For billing information, call the `setBillingRequired` method of the `SdkRequest` class:
```
sdkRequest.getShopperCheckoutRequirements().setBillingRequired(true);
```
For email, call the `setEmailRequired` method of the `SdkRequest` class:
```
sdkRequest.getShopperCheckoutRequirements().setEmailRequired(true);
```

### Set Sdk Request
before launching the activity set the `SdkRequest` in `BlueSnapService`.
```
BlueSnapService.getInstance().setSdkRequest(sdkRequest);
```

### Launch BluesnapCheckoutActivity / BluesnapChoosePaymentMethodActivity / BluesnapCreatePaymentActivity
Launch the checkout activity.
```
Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
startActivityForResult(intent);
```
This function will launch the checkout activity, display the checkout form with the details you provided in `sdkRequest`, and handle the interaction with the shopper.

> **Reminder:** As part of the checkout process, the shopper's card details will be sent directly and securely to BlueSnap's servers, so you won't have to touch the sensitive data yourself.

When the shopper completes checkout, you'll get an Android Activity Result with an `SdkResult` instance.

**Note:** For every time you launch the activity you need to activate the `BlueSnapService.getInstance().setup(...)` before and set a new SdkRequest `BlueSnapService.getInstance().setSdkRequest(sdkRequest);`.

## Get the SdkResult
The `SdkResult` instance will provide information about the transaction, and is passed back to your activity as an activityResult Extra. To get an activity result from `BluesnapCheckoutActivity`, you need to implement `onActivityResult()` (see [Android's documentation](https://developer.android.com/training/basics/intents/result.html) for more details on `onActivityResult`). 

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK && 
		resultCode != BluesnapChoosePaymentMethodActivity.BS_CHOOSE_PAYMENT_METHOD_RESULT_OK) {
	    if (data != null) {
                String sdkErrorMsg = "SDK Failed to process the request:";
                sdkErrorMsg += data.getStringExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG);
                // handle the error
            } else {
	        // User aborted the checkout process
            }
        return;

        // Here we can access the payment result
        Bundle extras = data.getExtras();
        SdkResult sdkResult = data.getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
	
	// this result will be returned from both BluesnapCheckoutActivity and BluesnapCreatePaymentActivity,
	// since handling is the same. BluesnapChoosePaymentMethodActivity has a different OK result code.
	if (BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK == sdkResult.getResult()) {
		// Call app server to process the payment
	}
}
```
The result code can be:
* BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK - for successful completion of Checkout/Create Payment flows
* BluesnapChoosePaymentMethodActivity.BS_CHOOSE_PAYMENT_METHOD_RESULT_OK - for successful completion of the "Choose Payment Type" flow
* Otherwise - not successful

An `SdkResult` instance holds the purchase details (for BluesnapCheckoutActivity and BluesnapCreatePaymentActivity), such as the purchase amount and currency, whether the transaction involved a returning shopper, and whether the shopper paid via card or PayPal. 
In case of BluesnapChosenPaymentMethodActivity, you get the chosen PM details as well as teh chose PM itself.

For credit card transactions, the card last four digits, card type, and shopper name will be included. For PayPal transactions, the PayPal transaction ID will be included. 

The following code shows some methods of the `SdkResult` class that you can use to obtain the purchase details: 

```
sdkResult.getCurrencyNameCode(); //e.g USD
sdkResult.getAmount(); //20.5
sdkResult.getPaypalInvoiceId(); // A string with the invoice Id
sdkResult.getChosenPaymentMethodType(); // for 2nd flow
```

## Complete the transaction (for Checkout and Create Payment flows)
If the shopper purchased via PayPal, then the transaction has successfully been submitted and no further action is required.

If the shopper purchased via credit card, you will need to make a server-to-server call to BlueSnap's Payment API with the Hosted Payment Field token you initialized in the SDK. You should do this after the shopper has completed checkout and has left the SDK checkout screen. Visit the [API documentation](https://developers.bluesnap.com/v8976-JSON/docs/auth-capture) to see how to send an Auth Capture, Auth Only, or Create Vaulted Shopper request (to name a few of the options).

### Auth Capture example - Credit card payments
For credit card payments, send an HTTP POST request to `/services/2/transactions` of the BlueSnap sandbox or production environment.

For example:
```cURL
curl -v -X POST https://sandbox.bluesnap.com/services/2/transactions \
-H 'Content-Type: application/json' \
-H 'Accept: application/json' \
-H 'Authorization: Basic Auth' \
-d '
{
	"cardTransactionType": "AUTH_CAPTURE",
	"recurringTransaction": "ECOMMERCE",
	"softDescriptor": "Mobile SDK test",
	"amount": 25.00,
	"currency": "USD",
	"pfToken": "TOKEN_STRING"
}'
```
If successful, the response HTTP status code is 200 OK. Visit our [API Reference](https://developers.bluesnap.com/v8976-JSON/docs/auth-capture) for more details.

# Additional functionality

## Currency conversion
The SDK provides a currency conversion rates service. To provide the best user experience, the rates are fetched in a single HTTP request and converted locally on the device. You can use the rates conversion without having to initialize any user interface parts of the SDK. However, you still need to initialize the SDK with a token.

The SDK provides the following methods for your convenience:
* `getSupportedRates()`
* `convertUSD()` 
* `convertPrice()` 

More information on any of these functions can be found in `BlueSnapService.java` of the SDK. 

## PayPal
If a shopper makes a purchase with PayPal, a PayPal transaction ID will be passed as part of the `SdkResult`.
All the other fields that are relevant to a credit card transaction will be empty.

## Google Pay
We've added Google Pay support to our SDK, which involves some dependencies and settings in the SDK's build.gradle.
Enable the Android Pay API by adding the following to the <application> tag of your AndroidManifest.xml:

    <application
      ...
      <meta-data
        android:name="com.google.android.gms.wallet.api.enabled"
        android:value="true" />
    </application>
The Google Pay button will become available in the checkout flow if you have allowed this payment method in the BlueSnap console, and if it is supported by the mobile. The demo app will run Google Pay in test mode, meaning any card you choose will result in a dummy card - this way you can test it without having to be approved by Google. 
To set Google Pay mode to PRODUCTION, change the googlePayTestMode value in your SDKRequest to false.
To get approved by Google, see [Google Pay Developer Documentation](https://developers.google.com/pay/api/processors/guides/test-and-validation/developer-documentation-checklist).

## Kount
The SDK includes an integrated Kount SDK for anti fraud functionality. A `kountSessionId` will be sent to BlueSnap servers and also with the server to server call. For more information see [https://developers.bluesnap.com/docs/fraud-prevention] 

## Customization and UI Overrides
The SDK allows you to customize the checkout experience, change colors, icons and basic layouts. One way to achieve that is by overriding the SDK resources files in your application and provide matching resource file names to override the SDK default values.

## Translation
The SDK includes translated resources for many languages. The Android framework will automatically pick up the translation according to the Android framework locale.

# Demo application
To get started with the demo application, do the following:
1. Clone the git repository.
2. Import the project by choosing "Import Project" and selecting the build.gradle file in the checkout directory.
3. Build and run the DemoApp on your device.

### Demo app token
The Demo app will obtain a merchant token from BlueSnap sandbox servers using HTTP calls and demo credentials. This procedure should be replaced by your server-side calls.

### ProGuard exclude
If you're running ProGuard as part of your build process make sure to exclude the Gson. to do this please add [this](https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg) to your proguard.cfg file.


## License
The MIT License (MIT)
Copyright (c) 2016 BlueSnap Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
