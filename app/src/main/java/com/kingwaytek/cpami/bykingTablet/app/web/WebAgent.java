package com.kingwaytek.cpami.bykingTablet.app.web;

import android.support.annotation.Nullable;
import android.util.Log;

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
import com.kingwaytek.cpami.bykingTablet.utilities.DebugHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Util;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

/**
 * 網路活動的方法都應該要集中在這裡，<br>
 * 並且統一使用 Volley。
 *
 * @author Vincent (2016/4/18)
 */
public class WebAgent {

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

        String apiUrl = MessageFormat.format(ApiUrls.API_GOOGLE_DIRECTION, origin, destination, mode, avoid,
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
                    final URL url = new URL(ApiUrls.API_UBIKE_TAIPEI);
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
}
