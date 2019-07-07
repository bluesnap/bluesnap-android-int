package com.bluesnap.androidapi.services;

import android.content.Context;
import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthRequest;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.CardinalJWT;
import com.cardinalcommerce.cardinalmobilesdk.Cardinal;
import com.cardinalcommerce.shared.models.parameters.CardinalConfigurationParameters;
import com.cardinalcommerce.shared.models.parameters.CardinalEnvironment;

import org.json.JSONObject;

import static java.net.HttpURLConnection.HTTP_OK;

public class CardinalManager {
    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    private static Cardinal cardinal = Cardinal.getInstance();
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private CardinalJWT cardinalToken;


    public static CardinalManager getInstance() {
        if (instance == null) {
            instance = new CardinalManager();
            return instance;
        } else {
            return instance;
        }
    }

    // This method can and should be caleld before a token is obtained to save time later
    public void configureCardinal(Context context) {
        CardinalConfigurationParameters cardinalConfigurationParameters = new CardinalConfigurationParameters();
        //TODO: Staging or production
        cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.STAGING);
        cardinal.configure(context, cardinalConfigurationParameters);
    }

    public void init(CardinalJWT cardinalJWT) {
    }


    public BS3DSAuthResponse authWith3DS(String currency, Double amount) throws Exception {
        BS3DSAuthRequest authRequest = new BS3DSAuthRequest(currency , amount, cardinalToken.getJWT());
        BlueSnapHTTPResponse response = blueSnapAPI.tokenizeDetails(authRequest.toJson().toString());
        JSONObject jsonObject;
        if (response.getResponseCode() != HTTP_OK) {
            throw new Exception("BS API Exception - tokenize 3ds"); // TODO create BSExceptions
        }
        jsonObject = new JSONObject(response.getResponseString());
        return BS3DSAuthResponse.fromJson(jsonObject);
    }
    /**
     *
     * @return
     * @throws Exception TODO: This should throw specific error
     */
    public CardinalJWT createCardinalJWT() throws Exception {
        BlueSnapHTTPResponse response = blueSnapAPI.createCardinalJWT();
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "Get cardinal error:\n" + response);
            throw  new Exception("unable to get Cardinal token")  ;
        }

        CardinalJWT  cardinalJWT = new CardinalJWT();
        cardinalJWT.parse(response.getResponseString());
        this.cardinalToken = cardinalJWT;
        return cardinalJWT;
    }

    public CardinalJWT getCardinalToken() {
        return cardinalToken;
    }
}
