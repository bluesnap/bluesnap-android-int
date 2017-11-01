## bluesnap-android

# About
BlueSnap's Android SDK enables you to easily accept credit card and PayPal payments directly from your Android app, and then process payments from your server using the Payment API. When you use this library, BlueSnap handles most of the PCI compliance burden for you, as the shopper's payment data is tokenized and sent directly to BlueSnap's servers.

# Versions
This SDK supports Android SDK 23 and above for development. The minimum Android API version for applications is 15, which covers more than 98% of the [Android devices](https://developer.android.com/about/dashboards/index.html).

# Installation

## Android Studio (Gradle) instructions
To get started, add the following line in your `build.gradle` file in the dependencies section:

    compile 'com.bluesnap:bluesnap-android:1.0.+'

# Usage

## Generate a token for the transaction
For each transaction, you need to generate a Hosted Payment Fields token on your server side and pass it to the SDK.

To do this, initiate a server to server POST request with your credentials and send it to the relevant URL for the Sandbox or Production environment:

* **Sandbox:** `https://sandbox.bluesnap.com/services/2/payment-fields-tokens`
* **Production:** `https://ws.bluesnap.com/services/2/payment-fields-tokens`

The token is returned in the Location header in the response. For more information, see [Create Hosted Payment Fields Token](http://developers.bluesnap.com/v4.0/docs/create-hosted-payment-fields-token)

## Initialize the SDK with the token
Initialize the SDK by passing the token to the setup method of the `BlueSnapService` class:

    BlueSnapService.getInstance().setup("MERCHANT_TOKEN_STRING");

The `setup()` method will initiate the SDK, prepare it for rate conversion, and initialize the required objects for the user interface. No UI interaction will happen at this stage.

**Note:** Since the token is valid for 60 minutes, only initialize the SDK with the token close to the purchase time.

## Launch the checkout page and collect shopper payment info
The SDK includes a pre-built checkout form, enabling you to easily collect the shopper's information. You won't have to deal with validation of the card number or expiration date, or the storage of sensitive information, as this is all handled for you. To launch this form, you need to create a `PaymentRequest` with the purchase amount and currency, and then start the `BluesnapCheckoutActivity` by creating an Android Intent and passing the `PaymentRequest` as an Intent Extra.

### Create a PaymentRequest
A `PaymentRequest` is required to pass information about the purchase to the SDK. The request must include the purchase amount and currency (as an [ISO 4217](https://developers.bluesnap.com/docs/currency-codes) currency code):

    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setAmount("20.5"D);
    paymentRequest.setCurrencyNameCode("USD");

Optionally, you may pass a tax amount and a subtotal price. The tax amount will be added to the subtotal.

    setAmountWithTax(Double subtotalAmount, Double taxAmount);

You can also pass a title or a custom title to be displayed to the shopper:

    paymentRequest.setCustomTitle("custom text");

If you would like to collect shipping information, call the `setShippingRequired` method in the `PaymentRequest`:

    paymentRequest.setShippingRequired(true);

### Launch BluesnapCheckoutActivity
To start the activity, create an Android Intent and pass the `PaymentRequest` as an Intent Extra.

    Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
    intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
    startActivityForResult(intent);

This will launch the activity, display the checkout form with the details you provided in the `PaymentRequest`, and handle the interaction with the shopper.

As part of the checkout process, the shopper's card details will be transmitted securely to BlueSnap's servers. You will not need to store the card number in your application.

When the shopper completes checkout, you'll get an Android Activity Result with a `PaymentResult` object.

## Get the PaymentResult
A `PaymentResult` instance provides the transaction information. For credit card transactions, this includes the last 4 digits of the card, the card type, and the shopper name. For PayPal transactions, it includes the PayPal transaction ID.

The `PaymentResult` is passed back to your activity as an activityResult Extra. To get an activity result from `BluesnapCheckoutActivity`, you need to implement [onActivityResult](https://developer.android.com/training/basics/intents/result.html) ();


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode != RESULT_OK) {
                // User aborted the checkout process
                return;

            PaymentResult paymentResult = (PaymentResult) data.getExtras().get(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
            ShippingInfo shippingInfo = (ShippingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        }

A `PaymentResult` instance holds information about the transaction, such as the purchase amount, currency, and also indicates if this was a returning shopper transaction or a purchase that was completed via PayPal.

    paymentResult.getCurrencyNameCode(); //e.g USD
    paymentResult.getAmount(); //20.5
    paymentResult.getPaypalInvoiceId(); // A string with the invoice Id.

## Complete the transaction
If the shopper purchased via PayPal, then the transaction has successfully been submitted and no further action is required.

If the shopper purchased via credit card, you will need to make a server-to-server call to BlueSnap's Payment API with the Hosted Payment Field token you used with the SDK. You should do this after the shopper has completed checkout and has left the SDK checkout screen. Visit the [API documentation](https://developers.bluesnap.com/v8976-JSON/docs/auth-capture) to see how to send an Auth Capture, Auth Only, Create Subscription, or Create Vaulted Shopper request (to name a few of the options).

## Auth Capture example - Credit card payments
For credit card payments, send an HTTP POST request to `/services/2/transactions` of the BlueSnap sandbox or production environment.

For example:
```cURL
curl -v -X POST https://sandbox.bluesnap.com/services/2/transactions \
-H 'Content-Type: application/json' \
-H 'Accept: application/json' \
-H 'Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=' \
-d '
{
	"cardTransactionType": "AUTH_CAPTURE",
	"recurringTransaction": "ECOMMERCE",
	"softDescriptor": "Mobile SDK test",
	"amount": 25.00,
	"currency": "USD",
	"pfToken": "812f6ee706e463d3276e3abeb21fa94072e40695ed423ddac244409b3b652eff_",
	"transactionFraudInfo": {"fraudSessionId": "B04C4B2B6BED427284ECE2F1F870466C"},
	"cardHolderInfo": {
        "firstName": "Jane",
        "lastName": "Shopper",
        "zip": "02451"
	}
}'
```
If successful, the response HTTP status code is 200 OK. Visit our [API Reference](https://developers.bluesnap.com/v8976-JSON/docs/auth-capture) for more details.

# Additional functionality

## Currency conversion
The SDK provides a currency conversion rates service. To provide the best user experience, the rates are fetched in a single HTTP request and converted locally on the device. You can use the rates conversion without having to initialize any user interface parts of the SDK. However, you still need to initialize the SDK with a token.

The SDK provides the following methods for your convenience:

    updateRates()
    getSupportedRates()
    convertUSD()
    convertPrice()

## PayPal
If a shopper makes a purchase with PayPal, a PayPal transaction ID will be passed as part of the `PaymentResult`.
All the other fields that are relevant to a credit card transaction will be empty.

## Kount
The SDK includes an integrated Kount SDK, a `kountSessionId` will be passed as part of the `PaymentResult`.
This `kountSessionId` is needed for creating a successful transaction (see Auth Capture example - Credit card payments).

If you have the Enterprise fraud prevention service level, pass your Kount MID In the intent you pass to `BluesnapCheckoutActivity`

    intent.putExtra(BluesnapCheckoutActivity.EXTRA_KOUNT_MERCHANT_ID, KountMerchantId);



## Customization and UI Overrides
It is possible to customize the checkout experience, change colors, icons and basic layouts.
One way to achieve that is by overriding the SDK resources files in your application and provide matching resource file names to override the SDK default values.

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