package com.kingwaytek.cpami.bykingTablet.app.web;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.ApiUrls;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.utilities.DebugHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 網路活動的方法都應該要集中在這裡，<br>
 * 並且統一使用 Volley。
 *
 * @author Vincent (2016/4/18)
 */
public class WebAgent implements ApiUrls, CommonBundle {

    private static final String TAG = "WebAgent";

    private static StringRequest commonRequest;

    private static Thread retryThread;
    private static boolean needRetry;
    private static int retryCount;

    private final static int CONNECT_TIME_OUT_MS  = 20 * 1000 ;

    public interface WebResultImplement {
        void onResultSucceed(String response);
        void onResultFail(String errorMessage);
    }

    public interface FileDownloadCallback {
        void onDownloadFinished();
        void onDownloadFailed(String errorMessage);
    }

    public static void getStringByUrl(final String url, final WebResultImplement webResult) {
        Log.i(TAG, "URL: " + url);
        final AppController appController = AppController.getInstance();

        commonRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i(TAG, "Response: " + response);
                webResult.onResultSucceed(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getMessage() != null && error.getMessage().contains("OutOfMemoryError")) {
                    appController.restartAppWithDelayedTime(2000);
                    return;
                }

                // 最多 Retry 3 次!
                if (retryCount < 3) {
                    if (DebugHelper.SHOW_NETWORK_RETRY) {
                        if (error.getMessage() == null) {
                            String retryMessage = String.format(AppController.getInstance().getAppContext().getString(R.string.network_unstable), (retryCount + 1));
                            Utility.toastLong(retryMessage);
                        } else
                            Utility.toastLong(error.getMessage() + "\nRetrying.." + (retryCount + 1));
                    }
                    retryConnect(url, webResult);
                    Log.i(TAG, "Retrying URL: " + url);
                }
                else {
                    webResult.onResultFail("ConnectionError, " + error.getMessage());
                    retryCount = 0;
                    PopWindowHelper.dismissPopWindow();

                    appController.getRequestQueue().cancelAll(this);
                }
            }
        }) {
            // 這裡的 Override 用來解決中文亂碼的問題
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String originString = new String(response.data, "UTF-8");
                    return Response.success(originString, HttpHeaderParser.parseCacheHeaders(response));
                }
                catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
                catch (OutOfMemoryError error) {
                    Utility.showToastOnNewThread(appController.getAppContext().getString(R.string.load_failed));
                    System.gc();
                    return Response.error(new ParseError(error));
                }
                catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        commonRequest.setRetryPolicy(new DefaultRetryPolicy(
                CONNECT_TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        appController.getRequestQueue().add(commonRequest);
    }

    private static void retryConnect(final String url, final WebResultImplement webResult) {
        retryCount++;
        needRetry = true;
        retryThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1800);
                    if (!isInterrupted() && needRetry)
                        getStringByUrl(url, webResult);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        retryThread.start();
    }

    public static void stopRetryThread() {
        if (retryThread != null) {
            retryThread.interrupt();
            retryThread = null;
        }
        needRetry = false;
        retryCount = 0;

        if (commonRequest != null) {
            AppController.getInstance().getRequestQueue().cancelAll(commonRequest);
            commonRequest.cancel();
            commonRequest = null;
        }
    }

    /**
     * @param avoid Set avoid option to null, if you want to using transit mode.
     */
    public static void getDirectionsData(final String origin, final String destination, final String mode, @Nullable String avoid, final WebResultImplement webResult) {
        if (avoid == null)
            avoid = "";

        String apiUrl = MessageFormat.format(API_GOOGLE_DIRECTION, origin, destination, mode, avoid,
                Utility.getLocaleLanguage(), AppController.getInstance().getAppContext().getString(R.string.GoogleDirectionKey));
        Log.i(TAG, "GoogleDirectionAPi: " + apiUrl);

        StringRequest directionRequest = new StringRequest(apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogHelper.dismissDialog();
                webResult.onResultSucceed(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.dismissDialog();
                webResult.onResultFail("ConnectionError, " + error.getMessage());
            }
        });

        directionRequest.setRetryPolicy(new DefaultRetryPolicy(
                CONNECT_TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        AppController.getInstance().getRequestQueue().add(directionRequest);
    }

    public static void getMultiDirectionsData(String apiUrl, final WebResultImplement webResult) {
        Log.i(TAG, "GoogleDirectionAPi: " + apiUrl);

        StringRequest directionRequest = new StringRequest(apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogHelper.dismissDialog();
                webResult.onResultSucceed(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.dismissDialog();
                webResult.onResultFail("ConnectionError, " + error.getMessage());
            }
        });

        directionRequest.setRetryPolicy(new DefaultRetryPolicy(
                CONNECT_TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        AppController.getInstance().getRequestQueue().add(directionRequest);
    }

    public static void sendPostToUrl(String url, final WebResultImplement webResult) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webResult.onResultSucceed(response);
                        Log.i(TAG, "SendPOST: DONE! " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null)
                            Log.e(TAG, "SendPOST: ERROR! " + error.networkResponse.statusCode + " " + error.getMessage());
                        webResult.onResultFail(error.getMessage());
                    }
                });

        AppController.getInstance().getRequestQueue().add(request);
    }

    public static void downloadTaipeiYouBikeData(final FileDownloadCallback downloadCallback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    final URL url = new URL(API_UBIKE_TAIPEI);
                    Log.i(TAG, "YouBikeTP Url: " + url.toString());

                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(CONNECT_TIME_OUT_MS);
                    connection.setReadTimeout(CONNECT_TIME_OUT_MS);
                    connection.connect();

                    File storagePath = new File(Util.sdPath, AppController.getInstance().getString(R.string.file_path_you_bike_data));
                    if (!storagePath.exists())
                        storagePath.createNewFile();

                    final FileOutputStream fos = new FileOutputStream(storagePath, false);
                    final byte buffer[] = new byte[8 * 1024];

                    final InputStream is = connection.getInputStream();

                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }

                    fos.flush();
                    fos.close();
                    is.close();
                    connection.disconnect();

                    downloadCallback.onDownloadFinished();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    downloadCallback.onDownloadFailed(e.getMessage());
                }
            }
        }.start();
    }

    public static void getListFromBikingService(@NonNull String type, WebResultImplement webResult) {
        sendPostToBikingService(POST_VALUE_MODE_LIST, type, null, null, null, webResult);
    }

    public static void uploadDataToBikingService(@NonNull String type, @NonNull String name, @NonNull String content, WebResultImplement webResult) {
        sendPostToBikingService(POST_VALUE_MODE_UPLOAD, type, name, content, null, webResult);
    }

    public static void downloadDataFromBikingService(@NonNull String id, WebResultImplement webResult) {
        sendPostToBikingService(POST_VALUE_MODE_DOWNLOAD, null, null, null, id, webResult);
    }

    private static void sendPostToBikingService(final String mode, final String type, final String name, final String content, final String id,
                                                final WebResultImplement webResult)
    {
        final StringRequest request = new StringRequest(Request.Method.POST, API_BIKING_SERVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webResult.onResultSucceed(response);
                        Log.i(TAG, "SendPOST: DONE! " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ResponseError!!!");
                        if (error.networkResponse != null)
                            Log.e(TAG, "SendPOST: ERROR! " + error.networkResponse.statusCode + " " + error.getMessage());
                        webResult.onResultFail("ConnectionError: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if (mode != null)
                    params.put(POST_KEY_MODE, mode);
                if (type != null)
                    params.put(POST_KEY_TYPE, type);
                if (name != null)
                    params.put(POST_KEY_NAME, name);
                if (content != null)
                    params.put(POST_KEY_CONTENT, content);
                if (id != null)
                    params.put(POST_KEY_ID, id);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                CONNECT_TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        AppController.getInstance().getRequestQueue().add(request);
    }

    public static void sendPostByURLConnection(final String mode, final String type, final WebResultImplement webResult) {
        new Thread() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(API_BIKING_SERVICE).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(CONNECT_TIME_OUT_MS);
                    connection.setReadTimeout(CONNECT_TIME_OUT_MS);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    writer.write(getPostPairString(mode, type));
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String line;
                        StringBuilder sb = new StringBuilder();

                        while ((line = reader.readLine()) != null ) {
                            sb.append(line);
                        }

                        reader.close();
                        webResult.onResultSucceed(sb.toString());
                    }
                    else
                        webResult.onResultFail("PostConnectionFailed!!!");

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static String getPostPairString(String mode, String type) {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append(URLEncoder.encode(POST_KEY_MODE, "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(mode, "UTF-8"))
                    .append("&")
                    .append(URLEncoder.encode(POST_KEY_TYPE, "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(type, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "PostPairs: " + sb.toString());
        return sb.toString();
    }
}
