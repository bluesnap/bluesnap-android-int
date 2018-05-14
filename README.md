## bluesnap-android

[![Build Status-Master](https://travis-ci.org/bluesnap/bluesnap-android-int.svg?branch=master)](https://travis-ci.org/bluesnap/bluesnap-android-int)
[![Build Status-Develop](https://travis-ci.org/bluesnap/bluesnap-android-int.svg?branch=develop)](https://travis-ci.org/bluesnap/bluesnap-android-int)

# About
BlueSnap's Android SDK enables you to easily accept credit card and PayPal payments directly from your Android app, and then process payments from your server using the Payment API. When you use this library, BlueSnap handles most of the PCI compliance burden for you, as the shopper's payment data is tokenized and sent directly to BlueSnap's servers.

# Versions
This SDK supports Android SDK 25 and above for development. The minimum Android API version for applications is 18, which covers more than 98% of the [Android devices](https://developer.android.com/about/dashboards/index.html).

# Installation

## Android Studio (Gradle) instructions
To get started, add the following line in your `build.gradle` file in the dependencies section:

    compile 'com.bluesnap:bluesnap-android:[version]'


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
BlueSnapService.getInstance().setup("MERCHANT_TOKEN_STRING", tokenProvider(), "merchantStoreCurrency", getApplicationContext(), new BluesnapServiceCallback() {
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

**Note:** Since the token is valid for 60 minutes, only initialize the SDK with the token close to the purchase time.

## Launch the checkout page and collect shopper payment info
The SDK includes a pre-built checkout form, enabling you to easily collect the shopper's information. You won't have to handle card number or expiration date validations, or the storage of sensitive information. 

To launch the checkout flow, you'll create an `SdkRequest` instance with the purchase amount and currency, and then start the `BluesnapCheckoutActivity` by creating an Android Intent and passing the `SdkRequest` as an Intent Extra.

### Create an SdkRequest instance 
An `SdkRequest` instance is required to pass information about the purchase to the SDK.
The instance must include:
 - the current purchase amount 
 - purchase currency (as an [ISO 4217](https://developers.bluesnap.com/docs/currency-codes) currency code)
 - an optional static tax amount (if you want the tax amount to be calculated dynamically, see 'Handling tax updates' below)
 - billingRequired: if false, the SDK will collect name and country; if true, it will collect also the billing address.
 - emailRequired: if true, the SDK will collect email as part of the billing information.
 - shippingRequired: if true, the SDK will collect shipping details.

```
SdkRequest sdkRequest = new SdkRequest(Double amount, String currencyNameCode, Double taxAmount, boolean billingRequired, boolean emailRequired, boolean shippingRequired)
```

#### Handling tax updates (optional)
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
Even if after constructor was created You can still collect additional information:

For shipping information, call the `setShippingRequired` method of the `SdkRequest` class:

```
sdkRequest.setShippingRequired(true);
```

For billing information, call the `setBillingRequired` method of the `SdkRequest` class:

```
sdkRequest.setBillingRequired(true);
```
For email, call the `setEmailRequired` method of the `SdkRequest` class:

```
sdkRequest.setEmailRequired(true);
```

### Set Sdk Request
before launching the activity set the `SdkRequest` in `BlueSnapService`.
```
BlueSnapService.getInstance().setSdkRequest(sdkRequest);
```

### Launch BluesnapCheckoutActivity
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
    if (resultCode != RESULT_OK) {
        // User aborted the checkout process
        return;

    SdkResult sdkResult = (SdkResult) data.getExtras().get(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
    ShippingInfo shippingInfo = (ShippingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
}
```

An `SdkResult` instance holds the purchase details, such as the purchase amount and currency, whether the transaction involved a returning shopper, and whether the shopper paid via card or PayPal. 

For credit card transactions, the card last four digits, card type, and shopper name will be included. For PayPal transactions, the PayPal transaction ID will be included. 

The following code shows some methods of the `SdkResult` class that you can use to obtain the purchase details: 

```
sdkResult.getCurrencyNameCode(); //e.g USD
sdkResult.getAmount(); //20.5
sdkResult.getPaypalInvoiceId(); // A string with the invoice Id.
```

## Complete the transaction
If the shopper purchased via PayPal, then the transaction has successfully been submitted and no further action is required.

If the shopper purchased via credit card, you will need to make a server-to-server call to BlueSnap's Payment API with the Hosted Payment Field token you initialized in the SDK. You should do this after the shopper has completed checkout and has left the SDK checkout screen. Visit the [API documentation](https://developers.bluesnap.com/v8976-JSON/docs/auth-capture) to see how to send an Auth Capture, Auth Only, Create Subscription, or Create Vaulted Shopper request (to name a few of the options).

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

## Kount
The SDK includes an integrated Kount SDK. A `kountSessionId` will be passed as part of the `SdkResult`.

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