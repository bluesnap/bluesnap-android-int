package com.bluesnap.androidapi.http;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by oz
 */

public class HTTPOperationController {

    private static final String TAG = HTTPOperationController.class.getName();

    private static final String HEADER_ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String DEL = "DELETE";
    private static final int TIME_OUT = 180 * 1000;
    private static final int BUFFER_SIZE_KB = 2048;


    /**
     * Perform a HTTP POST request with parameters.
     *
     * @param urlString   the URL to send the request to.
     * @param body        Json body to send with the request.
     * @param contentType
     * @param accept
     * @param httpParams  HTTP parameters to send with the request.
     * @return
     */
    public static BlueSnapHTTPResponse post(String urlString, String body, String contentType, String accept, List<CustomHTTPParams> httpParams) {
        HttpURLConnection connection = httpConnectionFactory(urlString, POST, contentType, accept, httpParams, true);

        try {
            writeRequestBody(connection, body);
            connection.connect();
            int statusCode = connection.getResponseCode();
            switch (statusCode) {
                case HTTP_CREATED:
                case HTTP_OK:
                    String response = new String(readFullyBytes(connection.getInputStream()));
                    return new BlueSnapHTTPResponse(statusCode, response, connection.getHeaderFields());
                default:
                    String errorResponse = new String(readFullyBytes(connection.getErrorStream()));
                    return new BlueSnapHTTPResponse(statusCode, errorResponse);

            }
        } catch (IOException e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }

    /**
     * Perform a HTTP PUT request with parameters.
     *
     * @param urlString   the URL to send the request to.
     * @param body        Json body to send with the request.
     * @param contentType
     * @param accept
     * @param httpParams
     * @return
     */
    public static BlueSnapHTTPResponse put(String urlString, String body, String contentType, String accept, List<CustomHTTPParams> httpParams) {
        HttpURLConnection connection = httpConnectionFactory(urlString, PUT, contentType, accept, httpParams, true);
        try {
            writeRequestBody(connection, body);
            connection.connect();
            int statusCode = connection.getResponseCode();
            return createHTTPOKResponse(connection, statusCode);

        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }

    /**
     * Perform a HTTP GET request with parameters.
     *
     * @param urlString   the URL to send the request to.
     * @param contentType
     * @param accept
     * @return
     */
    public static BlueSnapHTTPResponse get(String urlString, String contentType, String accept) {
        return get(urlString, contentType, accept, null);
    }

    /**
     * Perform a HTTP GET request with parameters.
     *
     * @param urlString   the URL to send the request to.
     * @param contentType
     * @param accept
     * @param httpParams
     * @return
     */
    public static BlueSnapHTTPResponse get(String urlString, String contentType, String accept, List<CustomHTTPParams> httpParams) {
        HttpURLConnection connection = httpConnectionFactory(urlString, GET, contentType, accept, httpParams, false);
        try {
            connection.connect();
            int statusCode = connection.getResponseCode();
            return createHTTPOKResponse(connection, statusCode);
        } catch (IOException e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }

    @NonNull
    private static BlueSnapHTTPResponse createHTTPOKResponse(final HttpURLConnection connection, int statusCode) throws IOException {
        switch (statusCode) {
            case HTTP_OK:
                String response = new String(readFullyBytes(connection.getInputStream()));
                return new BlueSnapHTTPResponse(statusCode, response);
            default:
                String errorResponse = new String(readFullyBytes(connection.getErrorStream()));
                return new BlueSnapHTTPResponse(statusCode, "", errorResponse);
        }
    }


    /**
     * Perform a HTTP DELETE request with parameters.
     *
     * @param urlString  the URL to send the request to.
     * @param httpParams
     * @return
     */
    public static BlueSnapHTTPResponse delete(String urlString, List<CustomHTTPParams> httpParams) {
        HttpURLConnection connection = httpConnectionFactory(urlString, DEL, null, null, httpParams, false);
        try {
            connection.connect();
            int statusCode = connection.getResponseCode();
            switch (statusCode) {
                case HTTP_NO_CONTENT:
                    String response = new String(readFullyBytes(connection.getInputStream()));
                    return new BlueSnapHTTPResponse(statusCode, response);
                default:
                    return new BlueSnapHTTPResponse(statusCode, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }


    private static boolean isHTTPSURL(@NonNull final String url) {
        return url.contains("https");
    }


    private static byte[] readFullyBytes(final InputStream is) {
        byte[] bytes = null;
        if (is != null) {
            try {
                int readed = 0;
                byte[] buffer = new byte[BUFFER_SIZE_KB];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while ((readed = is.read(buffer)) >= 0) {
                    outputStream.write(buffer, 0, readed);
                }
                outputStream.flush();
                bytes = outputStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, " : readFullyBytes: ", e);
            }
        }
        return bytes;
    }


    /**
     * Create HTTP or HTTPS Connection
     *
     * @param urlString
     * @param method
     * @param contentType
     * @param accept
     * @param httpParams
     * @return
     */
    private static HttpURLConnection httpConnectionFactory(@NonNull final String urlString,
                                                           @NonNull final String method,
                                                           @Nullable final String contentType, @Nullable final String accept,
                                                           @Nullable final List<CustomHTTPParams> httpParams, final boolean isNeedDoInputOutput) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            if (isHTTPSURL(urlString)) {
                httpURLConnection = (HttpsURLConnection) url.openConnection();
            } else { //TODO: remove support for HTTP connections
                Log.e(TAG, "Creating non https connection");
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
            httpURLConnection.setReadTimeout(TIME_OUT);
            httpURLConnection.setConnectTimeout(TIME_OUT);
            httpURLConnection.setRequestMethod(method);
            if (contentType != null && accept != null) {
                httpURLConnection.setRequestProperty(CONTENT_TYPE, contentType);
                httpURLConnection.setRequestProperty(HEADER_ACCEPT, accept);
            }
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    httpURLConnection.setRequestProperty(params.getKey(), params.getValue());
                }
            }
            if (Build.VERSION.SDK_INT > 13) {
                httpURLConnection.setRequestProperty("Connection", "close");
            }
            if (isNeedDoInputOutput) {
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Error on create URL " + ex.getMessage());
        } catch (IOException ex) {
            Log.e(TAG, "Error on create connection " + ex.getMessage());
        }
        return httpURLConnection;
    }

    /**
     * This implementation write json body data into outputstream into opened HttpURLConnection
     *
     * @param connection
     * @param body
     */
    private static void writeRequestBody(@NonNull final URLConnection connection, @Nullable final String body) {
        try {
            String buffer = "";
            if (body != null) buffer = body;

            byte[] outputInBytes = buffer.getBytes(StandardCharsets.UTF_8);
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), BUFFER_SIZE_KB);
            writer.flush();
            writer.close();
            os.close();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error in encode request body ", e);
        } catch (IOException ex) {
            Log.e(TAG, "Error in write request body ", ex);
        }
    }
}
