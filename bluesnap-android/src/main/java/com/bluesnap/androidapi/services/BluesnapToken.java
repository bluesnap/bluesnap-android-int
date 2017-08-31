package com.bluesnap.androidapi.services;

/**
 * A BlueSnap Token Representation that resolves URL accordint to token.
 * This must be instantiated with a toekn before accesing URL.
 */
public class BluesnapToken {
    private static final String SANDBOX_URL = "https://sandbox.bluesnap.com/services/2/";
    private static final String PROD_PART_1_URL = "https://ws";
    private static final String PROD_PART_2_URL = ".bluesnap.com/services/2/";
    private String url;
    private String merchantToken;
    private TokenProvider tokenProvider;
    private boolean production = false;

    public BluesnapToken(String merchantToken, TokenProvider tokenProvider) {
        setToken(merchantToken);
        this.tokenProvider = tokenProvider;
    }

    public String getUrl() throws IllegalStateException {
        if (AndroidUtil.isBlank(url))
            throw new IllegalStateException("Token not set");
        return url;
    }

    public void setToken(String token) throws IllegalArgumentException {
        if (AndroidUtil.isBlank(token) || token.length() < 10)
            throw new IllegalArgumentException("Malformed token");

        String lastChar = token.substring(token.length() - 1);
        if ("_".equals(lastChar))
            url = SANDBOX_URL;
        else if ("1".equals(lastChar) || "2".equals(lastChar)) {
            url = PROD_PART_1_URL + lastChar + PROD_PART_2_URL;
            production = true;
        }
        else
            throw new IllegalArgumentException("Illegal token");

        this.merchantToken = token;
    }

    public String getMerchantToken() {
        return merchantToken;
    }

    public boolean isProduction() {
        return production;
    }

    @Override
    public String toString() {
        return "BluesnapToken{" +
                "url='" + url + '\'' +
                ", merchantToken='" + merchantToken.substring(10) + '\'' +
                '}';
    }
}

