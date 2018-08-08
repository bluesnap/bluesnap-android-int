package com.bluesnap.androidapi.http;

import android.os.Build;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
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
    private static final int TIME_OUT = 2 * 60 * 1000;
    private static final int _4KB = 4 * 1024;


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
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.setProperty("http.keepAlive", "false");
            if (checkHTTPS(urlString)) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod(POST);
            conn.setRequestProperty(CONTENT_TYPE, contentType);
            conn.setRequestProperty(HEADER_ACCEPT, accept);
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    conn.setRequestProperty(params.getKey(), params.getValue());
                }
            }
            if (Build.VERSION.SDK_INT > 13) {
                conn.setRequestProperty("Connection", "close");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            body = (body != null) ? body : "";
            byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            int statusCode = conn.getResponseCode();
            switch (statusCode) {
                case HTTP_CREATED:
                case HTTP_OK:
                    String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
                    return new BlueSnapHTTPResponse(statusCode, response, conn.getHeaderFields());
                default:
                    String errorResponse = new String(readFullyBytes(conn.getErrorStream(), 2 * _4KB));
                    return new BlueSnapHTTPResponse(statusCode, errorResponse);

            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (conn != null)
                conn.disconnect();
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
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.setProperty("http.keepAlive", "false");
            if (checkHTTPS(urlString)) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod(PUT);
            conn.setRequestProperty(CONTENT_TYPE, contentType);
            conn.setRequestProperty(HEADER_ACCEPT, accept);
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    conn.setRequestProperty(params.getKey(), params.getValue());
                }
            }
            if (Build.VERSION.SDK_INT > 13) {
                conn.setRequestProperty("Connection", "close");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            body = (body != null) ? body : "";
            byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status code is : " + statusCode);
            switch (statusCode) {
                case 200:
                    String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
                    Log.d(TAG, "Response String is : " + response);
                    return new BlueSnapHTTPResponse(statusCode, response);
                default:
                    return new BlueSnapHTTPResponse(statusCode, "");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (conn != null)
                conn.disconnect();
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
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.setProperty("http.keepAlive", "false");
            if (checkHTTPS(urlString)) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setRequestMethod(GET);
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    conn.setRequestProperty(params.getKey(), params.getValue());
                }
            }
            conn.setRequestProperty("Accept", accept);
            conn.setRequestProperty("contentType", accept);

            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status is" + statusCode);
            switch (statusCode) {
                case 200:
                    String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
                    Log.d(TAG, "Response String is : " + response);
                    return new BlueSnapHTTPResponse(statusCode, response);
                default:
                    return new BlueSnapHTTPResponse(statusCode, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }


    /**
     * Perform a HTTP DELETE request with parameters.
     *
     * @param urlString  the URL to send the request to.
     * @param httpParams
     * @return
     */
    public static BlueSnapHTTPResponse delete(String urlString, List<CustomHTTPParams> httpParams) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.setProperty("http.keepAlive", "false");
            if (checkHTTPS(urlString)) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setRequestMethod(DEL);
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    conn.setRequestProperty(params.getKey(), params.getValue());
                }
            }
            conn.connect();
            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status is" + statusCode);
            switch (statusCode) {
                case 204:
                    String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
                    Log.d(TAG, "Response String is : " + response);
                    return new BlueSnapHTTPResponse(statusCode, response);
                default:
                    return new BlueSnapHTTPResponse(statusCode, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }


    /**
     * Perform a HTTP DELETE request with parameters.
     *
     * @param urlString  the URL to send the request to.
     * @param body       Json body to send with the request.
     * @param httpParams
     * @return
     */
    public static BlueSnapHTTPResponse delete(String urlString, String body, List<CustomHTTPParams> httpParams) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            System.setProperty("http.keepAlive", "false");
            if (checkHTTPS(urlString)) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setRequestMethod(DEL);
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            if (httpParams != null && httpParams.size() > 0) {
                for (CustomHTTPParams params : httpParams) {
                    conn.setRequestProperty(params.getKey(), params.getValue());
                }
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);
            body = (body != null) ? body : "";
            byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status is" + statusCode);
            switch (statusCode) {
                case 204:
                    String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
                    Log.d(TAG, "Response String is : " + response);
                    return new BlueSnapHTTPResponse(statusCode, response);
                default:
                    return new BlueSnapHTTPResponse(statusCode, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getting response", e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return new BlueSnapHTTPResponse(0, "");
    }


    /**
     * This implementation check if url is HTTP or HTTPS
     *
     * @param url the URL to send the request to.
     * @return
     */
    private static boolean checkHTTPS(String url) {
        return url.contains("https");

    }

    /**
     * Read bytes from InputStream efficiently. All data will be read from
     * stream. This method return the bytes or null. This method will not close
     * the stream.
     */
    private static byte[] readFullyBytes(InputStream is, int blockSize) {
        byte[] bytes = null;
        if (is != null) {
            try {
                int readed = 0;
                byte[] buffer = new byte[blockSize];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((readed = is.read(buffer)) >= 0) {
                    bos.write(buffer, 0, readed);
                }
                bos.flush();
                bytes = bos.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, " : readFullyBytes: ", e);
            }
        }
        return bytes;
    }


}
