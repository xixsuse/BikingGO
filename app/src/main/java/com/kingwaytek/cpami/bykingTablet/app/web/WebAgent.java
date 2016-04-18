package com.kingwaytek.cpami.bykingTablet.app.web;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.DebugHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.UnsupportedEncodingException;

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

    private final static int CONNECT_TIME_OUT_MS  = 30 * 1000 ;

    public interface WebResultImplement {
        void onResultSucceed(String response);
        void onResultFail(String errorMessage);
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
}
