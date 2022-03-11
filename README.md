## bluesnap-android

[![](https://jitpack.io/v/bluesnap/bluesnap-android-int.svg)](https://jitpack.io/#bluesnap/bluesnap-android-int)


# About
BlueSnap's Android SDK enables you to easily accept credit card, Google Pay and PayPal payments directly from your Android app, and then process payments from your server using the Payment API. When you use this library, BlueSnap handles most of the PCI compliance burden for you, as the shopper's payment data is tokenized and sent directly to BlueSnap's servers.

# Versions
This SDK supports Android SDK 27 and above for development. The minimum Android API version for applications is 19,See  [Android devices](https://developer.android.com/about/dashboards/index.html) for device coverage.

# Installation

## Android Studio (Gradle) instructions
Follow the [instructions on jitpack](https://jitpack.io/#bluesnap/bluesnap-android-int/) 

Step 1:
```
    allprojects {
	 repositories {
	      maven { url 'https://jitpack.io' }
		}
	}
```

Step 2:
```
   dependencies {
	 implementation 'com.github.bluesnap:bluesnap-android-int:Tag'
	}
```

Step 3: Replace the "Tag" with the desired version.


# Available flows for collecting payment details

There are 3 supported flow types:
* Checkout: Create a transaction for a new or existing shopper
* Configure shopper: Collect a shopper's information and selected payment method
* Pay with selected payment method: Create a transaction using an existing shopper's selected payment method

Any of those flows can be done by using either the BlueSnap SDK UI or your own UI, at your convenience. Please note that by using your own UI, you will be required to handle the data-transmission to BlueSnap as well, by using the `BlueSnapService` class for performing API calls.

## Checkout flow:
This flow is the most commonly used: the shopper chooses to buy something from you app, and you perform an end-to-end checkout flow. If you choose to use BlueSnap SDK UI than you’ll call BluesnapCheckoutActivity to handle the UI and data-transmission to BlueSnap; the shopper is asked to choose the payment method (CC/Google Pay/PayPal) and then fills the relevant payment details. The shopper's payment data is tokenized and sent directly to BlueSnap's servers. In this flow we support subscription chrage as well. The activity result returns some of the (non secure) information, but mainly all you need to do now is to complete the transaction: let your app server do an API call to BlueSnap, sending just the token, and the transaction will be completed, or the subscription will become active in case of subscription charge. In case of PayPal, you don't even have to do that, since PayPal flow already completes the transaction. If you are using you own UI, you’ll need to collect and send the shopper’s payment info to BlueSnap server.
Steps required for this flow (more information on each below):Steps required for this flow (more information on each below):
* Generate a token for the transaction
* Initialize the SDK with the token
* Launch BluesnapCheckoutActivity (using BlueSnap SDK UI) or collect and send shopper's payment info (using your own UI)
* Get the SdkResult (if using our UI)
* Complete the transaction or activate the subscription

## Configure shopper flow:
This flow will be used in apps where you wish to save the shopper's payment details upon registration, and use it later in a quick and easy fashion. This flow can be run only for an existing shopper (you can easily create the shopper using BlueSnap API). If the shopper chooses a Credit card, we collect the billing details and store them on BlueSnap servers, so that the (later) charge will not require the shopper to type any information. If the shopper chooses Google Pay or PayPal, we simply keep this preference, so that in the next step (Create Payment flow), the shopper will automatically get the GooglePay pop-up or the PayPal page.
Steps required for this flow (more information on each below):
* Generate a token for the choose payment (for the existing shopper)
* Initialize the SDK with the token
* Launch the BluesnapChoosePaymentMethodActivity to submit shopper's chosen payment info
* Once you get the activity result, there is nothing else for you to do beside check that it was successful; if successful, then the shopper details are already updated on BlueSnap servers.

## Pay with selected payment method flow:
This is where you quickly complete the payment with the shopper’s chosen payment method. This flow can be run only for an existing shopper that HAS valid chosen payment details. If (in the previous step: Configure shopper flow) the shopper chose a credit card, you will get the response immediately; if the shopper chose Google Pay or PayPal, they will now do the payment flow. In both cases, the result is the same as in the Checkout flow: The SDK result returns some (non secure) information, but mainly all you need to do now is let your app server do an API call to BlueSnap, sending just the token, and the transaction will be completed. In case of PayPal, you don't even have to do that, since PayPal flow already completes the transaction.
Steps required for this flow (more information on each below):
* Generate a token for the transaction (for the existing shopper)
* Initialize the SDK with the token
* Launch the BluesnapCreatePaymentActivity
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

## Use BlueSnap SDK UI
The SDK includes a pre-built form, enabling you to easily collect the shopper's information. You won't have to handle card number or expiration date validations, or the storage of sensitive information. 

- Checkout flow - To launch this flow, you'll create a `SdkRequest` instance with the purchase amount and currency, and then start the `BluesnapCheckoutActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method.
In case of subscription charge, you'll create an `SdkRequestSubscriptionCharge` instance instead, while the purchase amount and currency are optional.

- Configure shopper flow - To launch this flow, you'll create an `SdkRequestShopperRequirements` instance (without the purchase amount and currency), and then start the `BluesnapChoosePaymentMethodActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method

- Pay with selected payment method flow - To launch this flow, you'll create an `SdkRequest` instance (with the purchase amount and currency), and then start the `BluesnapCreatePaymentActivity` by creating an Android Intent and passing the `SdkRequest` using the setSdkRequest method

### Create an SdkRequest instance 
An `SdkRequest` instance is required to pass information about the purchase to the SDK.
The instance include:
 - Price Details (mandatory for 1st and 3rd flow, though optional for subscription in 1st flow):
   
   - the current purchase amount 
   
   - purchase currency (as an [ISO 4217](https://developers.bluesnap.com/docs/currency-codes) currency code)
 
 - Shopper Checkout Requirements
   
   - billingRequired: if false, the SDK will only collect name, country and zip code; if true, it will collect also the billing address.
   
   - emailRequired: if true, the SDK will collect email as part of the billing information.
   
   - shippingRequired: if true, the SDK will collect shipping details.
 
```
SdkRequest sdkRequest = new SdkRequest(Double amount, String currencyNameCode,  boolean billingRequired, boolean emailRequired, boolean shippingRequired)
```
**Note:**  Please make sure to pass the allowed number of digits after the decimal separator, according to the purchase currency, as specified in [Supported Currencies](https://developers.bluesnap.com/docs/currency-codes) in **Decimal places** column

An `SdkRequest` instance also contains the following:

- `allowCurrencyChange` property: if true, the SDK will allow the shopper to change the purchase currency. By default it is true; if you wish to prevent your shoppers from changing the currency, you can specifically change this value like this:
    ```
    sdkRequest.setAllowCurrencyChange(false);
    ```
- `setGooglePayActive` method: if you support Google Pay as a payment method (in BlueSnap console), it will be enabled for the shopper inside the SDK (in case the device supports it).
If you wish to disable Google Pay for this purchase, you can do it like this:
    ```
    sdkRequest.setGooglePayActive(false);
    ```
- `googlePayTestMode` property: if true (default), Google Pay flow will work in TEST mode, which means any card you enter will result in dummy card details.
If you set it to false, the SDK will instatiate Google Pay in PRODUCTION mode, which requires Google's approval of the app. If your app is not approved and you set to PRODUCTION mode, when clicking on the Google Pay button, you will get a pop-up saying "This merchant is not enabled for Google Pay"). 
Google Pay TEST mode is only supported in BlueSnap's Sandbox environment; if you try it in production, the app flow will work, but your transaction will fail.
You can specifically change this value like this:
    ```
    sdkRequest.setGooglePayTestMode(false);
    ```
- `hideStoreCardSwitch` property: if true, the SDK will hide the "Securely store my card" switch from the shopper and the card will **not** be stored in BlueSnap server.
By default it is false; if you wish to hide the store card switch, you can specifically change this value like this:
    ```
    sdkRequest.setHideStoreCardSwitch(true);
    ```
- `activate3DS` property: if true, the SDK will require a 3D Secure Authentication from the shopper when paying with credit card.
By default it is false; if you wish to activate 3DS Authentication, you can specifically change this value like this:
    ```
    sdkRequest.setActivate3DS(true);
    ```
    For more information see [3D Secure Authentication](https://github.com/bluesnap/bluesnap-android-int#3D-Secure-Authentication).

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
Before launching the activity set the `SdkRequest` in `BlueSnapService`.
```
BlueSnapService.getInstance().setSdkRequest(sdkRequest);
```

### Launch BluesnapCheckoutActivity / BluesnapChoosePaymentMethodActivity / BluesnapCreatePaymentActivity
Launch the suitable activity for the desired flow. For example, for the checkout flow:
```
Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
startActivityForResult(intent);
```
This function will launch the checkout activity, display the checkout form with the details you provided in `sdkRequest`, and handle the interaction with the shopper.

> **Reminder:** As part of the checkout process, the shopper's card details will be sent directly and securely to BlueSnap's servers, so you won't have to touch the sensitive data yourself.

Once the shopper completes the flow, you'll get an Android Activity Result with an `SdkResult` instance.

**Note:** For every time you launch the activity you need to activate the `BlueSnapService.getInstance().setup(...)` before and set a new SdkRequest `BlueSnapService.getInstance().setSdkRequest(sdkRequest);`.

### Get the SdkResult
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
In case of BluesnapChosenPaymentMethodActivity, you get the chosen PM details as well as the chosen PM itself.

For credit card transactions, the card last four digits, card type, and shopper name will be included. For PayPal transactions, the PayPal transaction ID will be included. 

The following code shows some methods of the `SdkResult` class that you can use to obtain the purchase details: 

```
sdkResult.getCurrencyNameCode(); //e.g USD
sdkResult.getAmount(); //20.5
sdkResult.getPaypalInvoiceId(); // A string with the invoice Id
sdkResult.getChosenPaymentMethodType(); // for 2nd flow
```

## Build your own UI
You need to create UI layout files and activities on your own. Please use the entities and methods provide business logic as described below:

### Collect all payment info
Use your own UI to collect all payment info from the shopper.

### Generate a PurchaseDetails instance (in case of a credit card purchase)
A `PurchaseDetails` instance is required to pass the purchase details to the BlueSnap server.
The object includes the properties:
 - `billingContactInfo` - The shopper's billing information
 - `shippingContactInfo` - The shopper's shipping information
 - `creditCard` (mandatory) - The shopper's credit card information
 - storeCard - True if you wish to store this card in BlueSnap server, false otherwise. By default it is false.

##### creditCard

If you are submitting a new credit card (for either a new or an existing shopper), you should store:
 - number
 - expirationMonth
 - expirationYear
 - cvc
 - newCreditCard=true
 
If you are submitting an existing card (for an existing shopper and a credit card that was previously submitted and stored), you should store:
 - cardLastFourDigits **or** number
 - cardType
 - newCreditCard=false
 
```
PurchaseDetails purchaseDetails = new PurchaseDetails(creditCard,billingContactInfo,shippingContactInfo,storeCard);
```

### Submit the shopper's details into BlueSnap server

```
BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
```

Use `BlueSnapHTTPResponse` to verify that the API call response code is 200 (HTTP_OK).

### Handle 3D Secure authentication

BlueSnap SDK integrates Cardinal SDK to provide a full handling of 3D secure authentication. The `CardinalManager` class provides an easy infrastructure for all data-transmission to BlueSnap and Cardinal servers. Mainly all you need to do is a single call to a `CardinalManager` method. To do so, first you'll need to register a BroadcastReceiver with the event `CardinalManager.THREE_DS_AUTH_DONE_EVENT` to handle the authentication result; than, you will call the following method:
```
CardinalManager.getInstance().authWith3DS(currency, amount, activity, creditCard);
```
Where **creditCard** is the credit card you've already submitted to BlueSnap server (in the previous step: Submit details into BlueSnap server).

In case the card's 3DS version is supported and the shopper identity verification is required: a Cardinal activity will be lunched and the shopper will be asked to enter the authentication code. 

Once the 3D Secure flow is done, you will catch the event and the 3D Secure result will be available:
```
String result = CardinalManager.getInstance().getThreeDSAuthResult();
```
If a server error occurred, the error description will be available in **onReceive()** of the BroadcastReceiver.
```
String error = intent.getStringExtra(CardinalManager.THREE_DS_AUTH_DONE_EVENT_NAME);
```

For 3DS result options see
[3D Secure Authentication](https://github.com/bluesnap/bluesnap-android-int#3D-Secure-Authentication).

## Complete the transaction (for Checkout and Create Payment flows)
If the shopper purchased via PayPal, then the transaction has successfully been submitted and no further action is required.

If the shopper purchased via credit card, you will need to make a server-to-server call to BlueSnap's Payment API with the Hosted Payment Field token you initialized in the SDK. You should do this after the shopper has completed checkout and has left the SDK checkout screen. Visit the [API documentation](https://developers.bluesnap.com/v8976-Basics/docs/completing-tokenized-payments) to see how.

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
The SDK includes an integrated Kount SDK for anti fraud functionality. A `kountSessionId` will be sent to BlueSnap servers and also with the server to server call. For more information see [Fraud Prevention Documentation](https://developers.bluesnap.com/docs/fraud-prevention).

## 3D Secure Authentication
The SDK includes an integrated Cardinal SDK for 3DS Authentication. 

If you are using BlueSnap SDK UI: if you choose to activate this service and the shopper chooses credit card as a payment method, the cardinal result will be passed as part of the `SdkResult`:
 ```
 String result = sdkResult.getThreeDSAuthenticationResult();
 ```
 If you're using your own UI, the cardinal result will be available in the `CardinalManager` instance:
 ```
 String result = CardinalManager.getInstance().getThreeDSAuthResult();
 ```

if 3DS Authentication was successful, the result will be one of the following:
* `AUTHENTICATION_SUCCEEDED` = 3D Secure authentication was successful because the shopper entered their credentials correctly or the issuer authenticated the transaction without requiring shopper identity verification.
* `AUTHENTICATION_BYPASSED` = 3D Secure authentication was bypassed due to the merchant's configuration.

If 3DS Authentication was **not** successful, the result will be one of the following errors:
* `AUTHENTICATION_UNAVAILABLE` = 3D Secure authentication is unavailable for this card.
* `AUTHENTICATION_FAILED` = Card authentication failed in cardinal challenge.
* `THREE_DS_ERROR` = Either a Cardinal internal error or a server error occurred.
* `CARD_NOT_SUPPORTED` = No attempt to run 3D Secure challenge was done due to unsupported 3DS version.
* `AUTHENTICATION_CANCELED` (only possible when using your own UI) = The shopper canceled the challenge or pressed the 'back' button in Cardinal activity.

In that case, you can decide whether you want to proceed with the transaction without 3DS Authentication or not.
**Please note** that you will be able to proceed with the transaction only If the option **Process failed 3DS transactions** is enabled in **Settings > Fraud Settings** in the BlueSnap Console.

## Customization and UI Overrides
The SDK allows you to customize the checkout experience, change colors, icons and basic layouts. One way to achieve that is by overriding the SDK resources files in your application and provide matching resource file names to override the SDK default values.

## Translation
The SDK includes translated resources for many languages. The Android framework will automatically pick up the translation according to the Android framework locale.

# Demo application
The Demo application is working with our sandbox servers. It shows a very basic example of the steps required to integrate the SDK with your app.
It also covers most of the SDK features. 

### Demo app token
The Demo app will obtain a merchant token from BlueSnap sandbox servers using HTTP calls and demo credentials. This procedure should be replaced by your server-side calls. In production applications, you do not need to put your BlueSnap API credetials in your app code.

To get started with the demo application, you will need Sandbox API credentials.

1. Clone the git repository.
2. Import the project by choosing "Import Project" and selecting the build.gradle file in the checkout directory.
4. Copy the file local.gradle.example to local.gradle and Put your Sandbox API credentials in it. 
3. Build and run the DemoApp on your device.



### ProGuard exclude
If you're running ProGuard as part of your build process make sure to exclude the Gson. to do this please add [this](https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg) to your proguard.cfg file.


## License
The MIT License (MIT)
Copyright (c) 2016 BlueSnap Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
