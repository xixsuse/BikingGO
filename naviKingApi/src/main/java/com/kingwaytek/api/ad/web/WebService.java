package com.kingwaytek.api.ad.web;

import com.kingwaytek.api.ad.AdDebugHelper;
import com.kingwaytek.api.web.WebServiceApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.URL;

public class WebService extends WebServiceApi {
	public final static String BASE_URL_JSON = "https://ad.localking.com.tw/api/";
	public final static String TEST_URL_JSON = "http://60.251.38.6/Wa_localad/api/";

	public static final int CONNECTION_TIMEOUT = AdDebugHelper.checkOpen() ? 10000 : 25000;
	public static final int READ_TIMEOUT = AdDebugHelper.checkOpen() ? 10000 : 25000;

	public static final int CONNECTION_TIMEOUT_FILE_LENGTH = AdDebugHelper.checkOpen() ? 4000 : 6000;
	public static final int READ_TIMEOUT_FILE_LENGTH = AdDebugHelper.checkOpen() ? 4000 : 6000;

	private static final String TAG = "WebService";

	public static String getResponseByType(WebItem item) {
		return getResponseByType(item, item.getPostData());
	}

	public static String getResponseByType(WebItem item, String jsonData) {
		final String POST_PAYLOUT = jsonData;
		String result = null;
		try {
			boolean bPureJson = true;
			String requestUrl = item.getRequestUrl();

			if (jsonData.equals("")) {
				result = fetchResultByGetData(requestUrl, POST_PAYLOUT, bPureJson);
			} else {
				result = fetchResultByPostData(requestUrl, POST_PAYLOUT, bPureJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	static String fetchResultByPostData(final String _BASE_URL, final String _POST_DATA) throws Exception {
		return fetchResultByPostData(_BASE_URL, _POST_DATA, false);
	}

	static String fetchResultByPostData(final String _BASE_URL, final String _POST_DATA, boolean bJson) throws Exception {

		URL url = new URL(_BASE_URL);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, READ_TIMEOUT);

		HttpClient httpClient = new DefaultHttpClient(httpParameters);

		HttpPost httpPost = new HttpPost(url.toURI());
		setHttpPostHeader(httpPost, bJson);
		setHttpPostEntry(httpPost, _POST_DATA);

		// Execute POST
		HttpResponse response = httpClient.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();

		String result = null;
		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			HttpEntity httpEntiry = response.getEntity();
			boolean bGzipFormat = isHttpCompressHeader(response);
			result = getResponse(bGzipFormat, httpEntiry);
		} else if (statusCode == HttpStatus.SC_NO_CONTENT) {
			// uploadTmcArray - 不會回傳資訊,所以是204 NO_CONTENT
		} else {
			throw new Exception("Http status is :" + statusCode);
		}
		return result;
	}

	static String fetchResultByGetData(final String _BASE_URL, final String _POST_DATA, boolean bJson) throws Exception {
		URL url = new URL(_BASE_URL);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, READ_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpGet httpGet = new HttpGet(url.toURI());
		// Execute Get
		HttpResponse response = httpClient.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		String result = null;
		int statusCode = statusLine.getStatusCode();
		AdDebugHelper.debugLog(TAG, "statusCode:" + statusCode);
		if (statusCode == HttpStatus.SC_OK) {
			HttpEntity httpEntiry = response.getEntity();
			// boolean bGzipFormat = isHttpCompressHeader(response);
			result = EntityUtils.toString(httpEntiry);
		} else {
			throw new Exception("Http status is not 200 OK!");
		}
		return result;
	}
}