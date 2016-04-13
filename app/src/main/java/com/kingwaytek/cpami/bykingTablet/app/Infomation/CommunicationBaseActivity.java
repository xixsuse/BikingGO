package com.kingwaytek.cpami.bykingTablet.app.Infomation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.SocketFactory;

public class CommunicationBaseActivity extends Activity {

	private final static String TAG = "Communication";
	private final static String BAD_RESPONSE = "No Response";
	private final static String NULL_EXCEPTION = "Null Exception";

	/** Time-out constants **/
	private class Constants {
		static public final int CONNECTION_TIMEOUT = 10000;
		static public final int SOCKET_TIMEOUT = 10000;
		static final double MIN_FREE_SPACE = 512.0 * 1024 * 1024;
	}

	/* Dialogs */
	private ProgressDialog progressDialog;

	/* Control Socket */
	private boolean socketSwitch;

	/** OnCreate() **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * startHttpGet()
	 * 
	 * Perform a HTTP request with HTTP request string and prompt messages.
	 **/
	protected boolean startHttpGet(String targetHttpGetString, boolean showProgressDialog, String promptTitle,
			String promptmessage, List<String> extraHeaderNames, List<String> extraHeaderValues) {

		/* Return when URL is null or empty */
		if (targetHttpGetString == null || targetHttpGetString.length() == 0) {
			return false;
		}

		HttpGetThread thread = new HttpGetThread(targetHttpGetString, extraHeaderNames, extraHeaderValues);
		thread.setName("HttpGetBackground");
		thread.start();

		Log.d(TAG, "Start HTTP Get URL: " + targetHttpGetString);

		/* Perform ProgressDialog if needed */
		if (showProgressDialog) {
			this.displayProgressDialog(promptTitle, promptmessage);
		}

		return true;
	}

	/**
	 * HttpGetThread
	 * 
	 * Perform a HTTP request with HTTP request string in thread
	 **/
	class HttpGetThread extends Thread {
		// For HTTP Get
		private String targetHttpGetString;
		private List<String> extraHttpGetHeaderNameArray;
		private List<String> extraHttpGetHeaderValueArray;

		HttpGetThread(String httpGetString, List<String> extraHeaderNameArray, List<String> extraHttpGetHeaderValueArray) {
			this.targetHttpGetString = httpGetString;
			this.extraHttpGetHeaderNameArray = extraHeaderNameArray;
			this.extraHttpGetHeaderValueArray = extraHttpGetHeaderValueArray;
		}

		public void run() {
			CommunicationBaseActivity.this.performHttpGet(targetHttpGetString,
					extraHttpGetHeaderNameArray, extraHttpGetHeaderValueArray);
		}
	}

	/**
	 * performHttpGet()
	 * 
	 * Start HTTP Request
	 **/
	private void performHttpGet(String currentHttpGetString, List<String> extraHttpGetHeaderNameArray, List<String> extraHttpGetHeaderValueArray) {

		String serverResponse = BAD_RESPONSE;
		String httpGetResultString = null;
		Header[] lastHttpGetRespondHeaders = null;

		try {
			HttpParams httpParameters = setConnectionTimeOut();

			/* Start HTTP Request */
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpGet request = new HttpGet(currentHttpGetString);

			if (extraHttpGetHeaderNameArray != null) {
				for (int i = 0; i < extraHttpGetHeaderNameArray.size(); i++) {
					String headerNameTmp = extraHttpGetHeaderNameArray.get(i);
					String headerValueTmp = extraHttpGetHeaderValueArray.get(i);

					request.setHeader(headerNameTmp, headerValueTmp);
				}
			}

			HttpResponse response = client.execute(request);

			String retSrc = "";

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				/* Handle the result string */
				InputStream instream = entity.getContent();
				retSrc = convertStreamToString(instream);
			}

			/* Backup the Respond Headers */
			lastHttpGetRespondHeaders = response.getAllHeaders();

			httpGetResultString = retSrc.replace("/", "");

			Log.i(TAG, "performHttpGet Done with Command: " + currentHttpGetString + " Result:\n" + retSrc);
		}
        catch (Exception e) {
			serverResponse = printException(e);
			Log.e(TAG, "Exception happened in performHttpGet(): " + serverResponse);
		}

		/* Notify the result to upper layer. */
		if (httpGetResultString != null) {
			didFinishGetRequestRunnable runnable = new didFinishGetRequestRunnable(
					currentHttpGetString, httpGetResultString,
					lastHttpGetRespondHeaders);
			this.runOnUiThread(runnable);

		} else {
			didFailGetRequestRunnable runnable = new didFailGetRequestRunnable(
					currentHttpGetString, serverResponse);
			this.runOnUiThread(runnable);
		}
	}

	/** Call back with Succeed HTTP request **/
	private class didFinishGetRequestRunnable implements Runnable {

		private String originalHttpGetString;
		private String originalHttpGetResultString;
		private Header[] originalHttpGetRespondHeaders;

		public didFinishGetRequestRunnable(String httpGetString, String httpGetResultString, Header[] httpGetRespondHeaders) {
			this.originalHttpGetString = httpGetString;
			this.originalHttpGetResultString = httpGetResultString;
			this.originalHttpGetRespondHeaders = httpGetRespondHeaders;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFinishWithGetRequest(originalHttpGetString,
                    originalHttpGetResultString, originalHttpGetRespondHeaders);
		}
	}

	/** Call back with Failed HTTP request or Exception **/
	private class didFailGetRequestRunnable implements Runnable {

		private String originalHttpGetString;
		private String originalHttpGetResultString;

		public didFailGetRequestRunnable(String httpGetString, String httpGetResultString) {

			this.originalHttpGetString = httpGetString;
			this.originalHttpGetResultString = httpGetResultString;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFailWithGetRequest(originalHttpGetString, originalHttpGetResultString);
		}
	}

	/**
	 * startHttpPost()
	 * 
	 * Perform a HTTP Post request with HTTP Post request and prompt messages.
	 **/
	protected boolean startHttpPost(String targetHttpPostString, boolean showProgressDialog, String promptTitle,
			String promptMessage, ArrayList<NameValuePair> targetRequest,
			List<String> extraHeaderNames, List<String> extraHeaderValues) {

		if (targetHttpPostString == null || targetHttpPostString.length() == 0
				|| targetRequest == null || targetRequest.isEmpty()) {
			return false;
		}

		HttpPostThread thread = new HttpPostThread(targetHttpPostString,
				targetRequest, extraHeaderNames, extraHeaderValues);
		thread.setName("HttpPostBackground");
		thread.start();

		Log.d(TAG, "Start HTTP Post URL: " + targetHttpPostString);

		if (showProgressDialog) {
			this.displayProgressDialog(promptTitle, promptMessage);
		}

		return true;
	}

	/**
	 * HttpPostThread
	 * 
	 * Perform a HTTP post in thread
	 **/
	class HttpPostThread extends Thread {
		/* For HTTP Post */
		private String targetHttpPostString;
		private ArrayList<NameValuePair> httpPostContent;
		private List<String> extraHttpPostHeaderNameArray;
		private List<String> extraHttpPostHeaderValueArray;

		HttpPostThread(String httpPostString,
				ArrayList<NameValuePair> httpPostContent,
				List<String> extraHeaderNameArray,
				List<String> extraHttpGetHeaderValueArray) {

			this.targetHttpPostString = httpPostString;
			this.httpPostContent = httpPostContent;
			this.extraHttpPostHeaderNameArray = extraHeaderNameArray;
			this.extraHttpPostHeaderValueArray = extraHttpGetHeaderValueArray;
		}

		public void run() {
			CommunicationBaseActivity.this
					.performHttpPost(targetHttpPostString, httpPostContent,
							extraHttpPostHeaderNameArray,
							extraHttpPostHeaderValueArray);
		}
	}

	private void performHttpPost(String currentHttpPostString,
			ArrayList<NameValuePair> httpPostContent,
			List<String> extraHttpPostHeaderNameArray,
			List<String> extraHttpPostHeaderValueArray) {

		String serverResponse = BAD_RESPONSE;
		String httpPostResultString = null;
		Header[] lastHttpPostRespondHeaders = null;

		try {
			HttpParams httpParameters = setConnectionTimeOut();

			// Start HTTP Request
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post = new HttpPost(currentHttpPostString);

			if (extraHttpPostHeaderNameArray != null) {
				for (int i = 0; i < extraHttpPostHeaderNameArray.size(); i++) {
					String headerNameTmp = (String) extraHttpPostHeaderNameArray
							.get(i);
					String headerValueTmp = (String) extraHttpPostHeaderValueArray
							.get(i);

					post.setHeader(headerNameTmp, headerValueTmp);
				}
			}

			post.setEntity(new UrlEncodedFormEntity(httpPostContent, "UTF-8"));
			HttpResponse response = client.execute(post);

			String retSrc = null;

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				/* Handle the result string */
				InputStream instream = entity.getContent();
				retSrc = convertStreamToString(instream);
			}

			httpPostResultString = retSrc;

			Log.i(TAG, "performHttpPost Done with Command: " + httpPostResultString + " Result:\n" + retSrc);
		}
        catch (Exception e) {
			serverResponse = this.printException(e);
			Log.e(TAG, "Exception happened in performHttpPost(): " + serverResponse);
		}

		/* Notify the result to upper layer. */
		if (httpPostResultString != null) {
			didFinishPostRequestRunnable runnable = new didFinishPostRequestRunnable(currentHttpPostString, httpPostResultString,
					lastHttpPostRespondHeaders);

			this.runOnUiThread(runnable);

		} else {
			didFailPostRequestRunnable runnable = new didFailPostRequestRunnable(
					currentHttpPostString, httpPostContent, serverResponse);

			this.runOnUiThread(runnable);
		}
	}

	/** Call back with succeed HTTP post **/
	private class didFinishPostRequestRunnable implements Runnable {

		private String originalHttpPostString;
		private String originalHttpPostResultString;
		private Header[] originalHttpPostRespondHeaders;

		public didFinishPostRequestRunnable(String httpPostString, String httpPostResultString, Header[] httpPostRespondHeaders) {
			this.originalHttpPostString = httpPostString;
			this.originalHttpPostResultString = httpPostResultString;
			this.originalHttpPostRespondHeaders = httpPostRespondHeaders;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFinishWithPostRequest(originalHttpPostString, originalHttpPostResultString,
					originalHttpPostRespondHeaders);
		}
	}

	/** Call back with failed HTTP post or Exceptions **/
	private class didFailPostRequestRunnable implements Runnable {

		private String originalHttpPostString;
		private ArrayList<NameValuePair> originalHttpPostContent;
		private String serverResponse;

		public didFailPostRequestRunnable(String httpPostString,
				ArrayList<NameValuePair> httpPostContent, String serverResponse) {
			this.originalHttpPostString = httpPostString;
			this.originalHttpPostContent = httpPostContent;
			this.serverResponse = serverResponse;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFailWithPostRequest(originalHttpPostString, originalHttpPostContent, serverResponse);
		}
	}

	/**
	 * startHttpPostFile()
	 * 
	 * Post file to the destination server
	 **/
	protected boolean startHttpPostFile(String targetHttpPostString, boolean showProgressDialog,
                                        String promptTitle, String promptMessage, String postFilePathString) {

		if (targetHttpPostString == null || targetHttpPostString.length() == 0
				|| postFilePathString == null
				|| postFilePathString.length() == 0) {
			return false;
		}

		File file = new File(postFilePathString);
		if (!file.exists()) {
			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
			return false;
		}

		HttpPostFileThread thread = new HttpPostFileThread(
				targetHttpPostString, postFilePathString);
		thread.setName("HttpPostFileBackground");
		thread.start();

		Log.d(TAG, "Start HTTP Post File URL: " + targetHttpPostString);

		if (showProgressDialog) {
			displayProgressDialog(promptTitle, promptMessage);
		}

		return true;
	}

	/**
	 * HttpPostFileThread
	 * 
	 * Perform a HTTP post file in thread
	 **/
	class HttpPostFileThread extends Thread {
		/* For HTTP Post */
		private String targetHttpPostString;
		private String httpPostFilePathString;

		HttpPostFileThread(String httpPostString, String postFilePathString) {
            this.targetHttpPostString = httpPostString;
			this.httpPostFilePathString = postFilePathString;
		}

		public void run() {
			CommunicationBaseActivity.this.performHttpPostFile(targetHttpPostString, httpPostFilePathString);
		}
	}

	/**
	 * Warning! this method would have problem when upload large file due to
	 * Java's limitation of HttpURLConnection.
	 **/
	private void performHttpPostFile(String currentHttpPostString, String httpPostFilePathString) {
		String httpPostResultString = null;
		int serverResponseCode = -1;
		String serverResponseMessage = BAD_RESPONSE;

		HttpURLConnection connection;
		DataOutputStream outputStream;

		String pathToOurFile = httpPostFilePathString;
		String urlServer = currentHttpPostString;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			/* Read file */
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {

				Log.i(TAG, "Post File Send:" + Integer.toString(bytesRead));

				outputStream.write(buffer, 0, bufferSize);
				outputStream.flush();
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			/* Responses from the server (code and message) */
			serverResponseCode = connection.getResponseCode();
			serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

			connection.disconnect();

			Log.d(TAG, "Post File Done With Command: " + currentHttpPostString);

			httpPostResultString = "";

		} catch (Exception e) {

			serverResponseMessage = this.printException(e);

			Log.e(TAG, "Exception happened in performHttpPostFile(): "
					+ serverResponseMessage);
		}

		/* Notify the result to upper layer. */
		if (httpPostResultString != null) {
			didFinishPostFileRequestRunnable runnable = new didFinishPostFileRequestRunnable(
					currentHttpPostString, httpPostFilePathString,
					serverResponseCode, serverResponseMessage);
			this.runOnUiThread(runnable);

		} else {
			didFailedPostFileRequestRunnable runnable = new didFailedPostFileRequestRunnable(
					currentHttpPostString, httpPostFilePathString,
					serverResponseMessage);
			this.runOnUiThread(runnable);
		}
	}

	/** Call back with succeed HTTP post file **/
	private class didFinishPostFileRequestRunnable implements Runnable {

		private String originalHttpPostString;
		private String originalFilePath;
		private int resultCode;
		private String serverResponseMessage;

		public didFinishPostFileRequestRunnable(String httpPostString,
				String postFilePath, int resultCode,
				String serverResponseMessage) {

			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
			this.resultCode = resultCode;
			this.serverResponseMessage = serverResponseMessage;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFinishWithPostFileRequest(
					originalHttpPostString, originalFilePath, resultCode,
					serverResponseMessage);
		}
	};

	/** Call back with failed HTTP post file or Exception **/
	private class didFailedPostFileRequestRunnable implements Runnable {

		private String originalHttpPostString;
		private String originalFilePath;
		private String serverResponse;

		public didFailedPostFileRequestRunnable(String httpPostString,
				String postFilePath, String serverResponse) {

			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
			this.serverResponse = serverResponse;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFailedWithPostFileRequest(
					originalHttpPostString, originalFilePath, serverResponse);
		}
	};

	/** Download file to SDcard ***/
	protected boolean startDownloadFile(String targetDownloadURL,
			String fileName, boolean showProgressDialog, String promptTitle,
			String promptMessage) {

		if (targetDownloadURL == null || targetDownloadURL.length() == 0
				|| fileName == null || fileName.length() == 0) {
			return false;
		}

		if (!checkFreeSpace()) {
			return false;
		}

		File file = new File(fileName);

		if (file.exists()) {

			return false;

		} else {
			file.mkdir();
		}

		DownloadFileThread thread = new DownloadFileThread(targetDownloadURL,
				file);
		thread.setName("DownloadFileBackground");
		thread.start();

		Log.d(TAG, "Start Download File form URL: " + targetDownloadURL);

		if (showProgressDialog == true) {
			this.displayProgressDialog(promptTitle, promptMessage);
		}

		return true;
	}

	/**
	 * Download file from target URL
	 **/
	class DownloadFileThread extends Thread {

		private String targetdownloadURLString;
		private File targetPath;

		DownloadFileThread(String targetdownloadURLString, File targetPath) {
			this.targetdownloadURLString = targetdownloadURLString;
			this.targetPath = targetPath;
		}

		public void run() {
			CommunicationBaseActivity.this.startDownloadFile(
					targetdownloadURLString, targetPath);
		}
	}

	private void startDownloadFile(String targetURL, File fileName) {

		boolean isDownloadSucceed = false;
		String serverResponse = BAD_RESPONSE;

		try {
			URL url = new URL(targetURL);
			URLConnection connection = url.openConnection();
			connection.connect();

			/* download the file */
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(fileName);

			byte data[] = new byte[1024];
			int count;
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();

			Log.d(TAG, "File download succeed, path: " + fileName);

			isDownloadSucceed = true;

		} catch (MalformedURLException e) {

			serverResponse = this.printException(e);
			Log.e(TAG, "Exception happened in downloadFile(): "
					+ serverResponse);

		} catch (IOException e) {

			serverResponse = this.printException(e);
			Log.e(TAG, "Exception happened in downloadFile(): "
					+ serverResponse);
		}

		if (isDownloadSucceed) {
			DowonloadSucceed downloadSucceed = new DowonloadSucceed(targetURL,
					fileName.toString());
			this.runOnUiThread(downloadSucceed);

		} else {
			DowonloadFailed downloadfailed = new DowonloadFailed(targetURL,
					fileName.toString(), serverResponse);
			this.runOnUiThread(downloadfailed);
		}
	}

	/** Call back with download complete **/
	private class DowonloadSucceed implements Runnable {
		private String originalHttpPostString;
		private String originalFilePath;

		public DowonloadSucceed(String httpPostString, String postFilePath) {

			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFinishWithDownloadFile(
					originalHttpPostString, originalFilePath);
		}
	};

	/** Call back with download failed **/
	private class DowonloadFailed implements Runnable {
		private String originalHttpPostString;
		private String originalFilePath;
		private String serverResponse;

		public DowonloadFailed(String httpPostString, String postFilePath,
				String serverResponse) {
			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
			this.serverResponse = serverResponse;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFailedWithDownloadFile(
					originalHttpPostString, originalFilePath, serverResponse);
		}
	};

	/**
	 * startHttpPostLargeFile()
	 * 
	 * Use to post large file, when the file size is over Java's limitation of
	 * HttpURLConnection.
	 **/
	protected boolean startHttpPostLargeFile(String requestURL, int port,
			String header, String beforeheader, String afterHeader,
			String filePath, boolean showProgressDialog, String promptTitle,
			String promptMessage) {

		if (requestURL == null || requestURL.length() == 0 || header == null
				|| header.length() == 0 || filePath == null
				|| filePath.length() == 0) {

			return false;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
			return false;
		}

		HttpPostLargeFileThread thread = new HttpPostLargeFileThread(
				requestURL, port, header, beforeheader, afterHeader, filePath);
		thread.setName("HttpPostLargeFileBackground");
		thread.start();

		Log.d(TAG, "Start HTTP Post Large File URL: " + requestURL);

		if (showProgressDialog == true) {
			this.displayProgressDialog(promptTitle, promptMessage);
		}

		return true;
	}

	/**
	 * DownloadFileThread
	 * 
	 * Perform a HTTP post large file in thread
	 **/
	class HttpPostLargeFileThread extends Thread {

		private String requestURL;
		private int port;
		private String headerContent;
		private String beforeHeader;
		private String afterHeader;
		private String filePath;

		HttpPostLargeFileThread(String requestURL, int port,
				String headerContent, String beforeHeader, String afterHeader,
				String filePath) {
			this.requestURL = requestURL;
			this.port = port;
			this.headerContent = headerContent;
			this.beforeHeader = beforeHeader;
			this.afterHeader = afterHeader;
			this.filePath = filePath;
		}

		public void run() {
			CommunicationBaseActivity.this.performPostLargeFile(requestURL,
					port, headerContent, beforeHeader, afterHeader, filePath);
		}
	}

	private void performPostLargeFile(String requestURL, int port,
			String header, String beforeHeader, String afterHeader,
			String filePath) {

		boolean isPostSucceed = false;
		String serverRespnse = BAD_RESPONSE;

		try {

			/* Setup Socket */
			Socket socket = SocketFactory.getDefault().createSocket();

			SocketAddress remoteaddr = new InetSocketAddress(requestURL, port);

			socket.connect(remoteaddr, Constants.SOCKET_TIMEOUT);

			BufferedOutputStream out = new BufferedOutputStream(
					socket.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(
					socket.getInputStream());

			out.write(header.getBytes("UTF-8"));
			out.write(beforeHeader.getBytes("UTF-8"));

			FileInputStream fileInputStream = new FileInputStream(new File(
					filePath));

			int bytesAvailable = fileInputStream.available();
			Log.i(TAG,
					"Post Large File Size: " + String.valueOf(bytesAvailable));

			int bufferSize = 64 * 1024;
			byte[] buffer = new byte[bufferSize];
			byte[] InBuffer = new byte[bufferSize];

			int len;
			int currentTotalOutputLen = 0;
			int currentTotalInputLen = 0;

			long start = System.currentTimeMillis();

			while ((len = fileInputStream.read(buffer, 0, bufferSize)) != -1) {
				currentTotalOutputLen += len;
				Log.i(TAG,
						"Send:" + Integer.toString(len) + " TotalOutput:"
								+ Integer.toString(currentTotalOutputLen)
								+ " TotalInput:"
								+ Integer.toString(currentTotalInputLen));

				out.write(buffer, 0, len);
				out.flush();

				/* Handle Input Part */
				if (in.available() > 0) {
					len = in.read(InBuffer, 0, bufferSize);
					currentTotalInputLen += len;
					String inputString = new String(InBuffer);
					Log.i(TAG, "Input:" + inputString);
				}
			}

			long end = (System.currentTimeMillis() - start) / 1000;

			Log.i(TAG, "Post Large FileTtime: " + String.valueOf(end));

			out.write(afterHeader.getBytes());
			fileInputStream.close();
			out.close();
			in.close();
			socket.close();

			isPostSucceed = true;
			Log.i(TAG, "Large File Post Done.");

		} catch (UnsupportedEncodingException e) {

			serverRespnse = this.printException(e);
			Log.e(TAG, "IOException happened in performPostLargeFile(): "
					+ serverRespnse);

		} catch (SocketTimeoutException e) {

			serverRespnse = this.printException(e);
			Log.e(TAG, "IOException happened in performPostLargeFile(): "
					+ serverRespnse);

		} catch (IOException e) {

			serverRespnse = this.printException(e);
			Log.e(TAG, "IOException happened in performPostLargeFile(): "
					+ serverRespnse);
		}

		if (isPostSucceed) {

			didFinishPostLargeFileRequestRunnable didFinishWithPostLargeFile = new didFinishPostLargeFileRequestRunnable(
					requestURL, filePath);
			this.runOnUiThread(didFinishWithPostLargeFile);

		} else {
			didFailedPostLargeFileRequestRunnable didFailedWithPostLargeFile = new didFailedPostLargeFileRequestRunnable(
					requestURL, filePath, serverRespnse);
			this.runOnUiThread(didFailedWithPostLargeFile);
		}
	}

	/** Call back with succeed HTTP post large file **/
	private class didFinishPostLargeFileRequestRunnable implements Runnable {

		private String originalHttpPostString;
		private String originalFilePath;

		public didFinishPostLargeFileRequestRunnable(String httpPostString,
				String postFilePath) {
			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFinishWithPostLargeFileRequest(
					originalHttpPostString, originalFilePath);
		}
	};

	/** Call back with Post Large File failed **/
	private class didFailedPostLargeFileRequestRunnable implements Runnable {
		private String originalHttpPostString;
		private String originalFilePath;
		private String serverResponse;

		public didFailedPostLargeFileRequestRunnable(String httpPostString,
				String postFilePath, String serverResponse) {
			this.originalHttpPostString = httpPostString;
			this.originalFilePath = postFilePath;
			this.serverResponse = serverResponse;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.didFailedWithPostLargeFileRequest(
					originalHttpPostString, originalFilePath, serverResponse);
		}
	};

	/** Open Socket **/
	protected boolean startOpenSocket(String requestURL, int port,
			String header, boolean showProgressDialog, String promptTitle,
			String promptMessage) {

		if (requestURL == null || requestURL.length() == 0 || header == null
				|| header.length() == 0) {
			return false;
		}

		OpenSocketThread thread = new OpenSocketThread(requestURL, port, header);
		thread.setName("OpenSocketBackground");
		thread.start();

		if (showProgressDialog == true) {
			this.displayProgressDialog(promptTitle, promptMessage);
		}

		return true;
	}

	/**
	 * OpenSocketThread
	 * 
	 * Perform a HTTP post large file in thread
	 **/
	class OpenSocketThread extends Thread {

		private String requestURL;
		private int port;
		private String header;

		OpenSocketThread(String requestURL, int port, String header) {
			this.requestURL = requestURL;
			this.port = port;
			this.header = header;
		}

		public void run() {
			CommunicationBaseActivity.this.performOpenSocket(requestURL, port,
					header);
		}
	}

	private void performOpenSocket(String requestURL, int port, String header) {

		String exception;
		String resultString;

		try {
			/* Setup Socket */
			Socket socket = SocketFactory.getDefault().createSocket();

			SocketAddress remoteaddr = new InetSocketAddress(requestURL, port);

			socket.connect(remoteaddr, Constants.SOCKET_TIMEOUT);

			InputStream inputStream = socket.getInputStream();

			BufferedReader is = new BufferedReader(new InputStreamReader(inputStream));

			DataOutputStream os = new DataOutputStream(socket.getOutputStream());

			os.write(header.getBytes());

			Log.d(TAG, "Open Socket Succeed: " + requestURL);

			socketSwitch = true;

			while (socketSwitch) {
				resultString = is.readLine();
				Log.i(TAG, "Socket Response: " + resultString);

				socketRunnable runnable = new socketRunnable(requestURL, port, resultString);
				this.runOnUiThread(runnable);
			}
			socket.close();
		}
        catch (IOException e) {
			exception = this.printException(e);
			socketExceptionRunnable runnable = new socketExceptionRunnable(requestURL, port, exception);
			this.runOnUiThread(runnable);
			Log.e(TAG, "IOException happened in performOpenSocket(): " + exception);
		}
		socketRunnable runnable = new socketRunnable(requestURL, port, "Socket Closed");
		this.runOnUiThread(runnable);
	}

	/** Call back of Socket **/
	private class socketRunnable implements Runnable {
		private String socketIP;
		private int socketPort;
		private String serverMessage;

		public socketRunnable(String socketIP, int socketPort, String serverMessage) {
			this.socketIP = socketIP;
			this.socketPort = socketPort;
			this.serverMessage = serverMessage;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.socketResponse(socketIP, socketPort, serverMessage);
		}
	}

	/** Call back when Socket Exception happened **/
	private class socketExceptionRunnable implements Runnable {
		private String socketIP;
		private int socketPort;
		private String exception;

		public socketExceptionRunnable(String socketIP, int socketPort, String exception) {
			this.socketIP = socketIP;
			this.socketPort = socketPort;
			this.exception = exception;
		}

		@Override
		public void run() {
			CommunicationBaseActivity.this.dismissProgressDialog();
			CommunicationBaseActivity.this.socketBadResponse(socketIP, socketPort, exception);
		}
	}

	/** Call this method when you want to close Socket connection **/
	public void closeSocket() {
		this.socketSwitch = false;
	}

	/** Check Free Space in SD card **/
	private boolean checkFreeSpace() {

		String sdcard = Environment.getExternalStorageDirectory().toString();

		StatFs stat = new StatFs(sdcard);

		/* return value is in bytes */
		@SuppressWarnings("deprecation")
		double free_memory = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();

		return free_memory > Constants.MIN_FREE_SPACE;
	}

	/** Time out parameter of Connection methods **/
	private HttpParams setConnectionTimeOut() {
		/* Setup the timeout */
		HttpParams httpParameters = new BasicHttpParams();

		/*
		 * Set the timeout in milliseconds until a connection is established.
		 * The default value is zero, that means the timeout is not used.
		 */
		int timeoutConnection = Constants.CONNECTION_TIMEOUT;

		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		/*
		 * Set the default socket timeout (SO_TIMEOUT) in milliseconds which is
		 * the timeout for waiting for data.
		 */
		int timeoutSocket = Constants.SOCKET_TIMEOUT;

		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		return httpParameters;
	}

	/**
	 * To convert the InputStream to String we use the BufferedReader.readLine()
	 * method. We iterate until the BufferedReader return null which means
	 * there's no more data to read. Each line will appended to a StringBuilder
	 * and returned as String.
	 **/
	private String convertStreamToString(InputStream is) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is/* ,"UTF-8" */));

		StringBuilder sb = new StringBuilder();

		String line;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
        catch (IOException e) {
            Log.e(TAG, "IOException happened in downloadFile(): " + printException(e));
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                Log.e(TAG, "IOException happened in downloadFile(): " + printException(e));
            }
        }
        return sb.toString();
	}

	/** Use to Print Exception **/
	private String printException(Exception exception) {
		String exceptionMessage = NULL_EXCEPTION;

		// if (exception != null) {
		// exceptionMessage = exception.getMessage().toString();
		// }

		return exceptionMessage;
	}

	/** ProgressDialog methods **/
	private void displayProgressDialog(String title, String message) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(this, title, message);
		}
	}

	private void dismissProgressDialog() {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * The result of GET will be passed to upper layer with
	 * didFinishWithGetRequest() and didFailWithGetRequest()
	 **/
	public void didFinishWithGetRequest(String requestString,
			String resultString, Header[] respondHeaders) {
	}

	public void didFailWithGetRequest(String requestString, String resultString) {
	}

	/**
	 * The result of POST will be passed to upper layer with
	 * didFinishWithPostRequest() and didFailWithPostRequest()
	 **/
	public void didFinishWithPostRequest(String requestString,
			String resultString, Header[] respondHeaders) {
	}

	public void didFailWithPostRequest(String requestString,
			ArrayList<NameValuePair> requestContent, String serverResponse) {
	}

	/**
	 * The result of POST File will be passed to upper layer with
	 * didFinishWithPostFileRequest() and didFailWithPostFileRequest()
	 **/
	public void didFinishWithPostFileRequest(String requestString,
			String filePath, int resultCode, String serverMessage) {
	}

	public void didFailedWithPostFileRequest(String requestString,
			String filePath, String serverResponse) {
	}

	/**
	 * The result of Download File will be passed to upper layer with
	 * didFinishWithDownloadFileRequest() and didFailWithDownloadFileRequest()
	 **/
	public void didFinishWithDownloadFile(String requestString, String filePath) {
	}

	public void didFailedWithDownloadFile(String requestString,
			String filePath, String serverResponse) {
	}

	/**
	 * The result of POST Large File will be passed to upper layer with
	 * didFinishWithPostLargeFileRequest() and didFailWithPostLargeFileRequest()
	 **/
	public void didFinishWithPostLargeFileRequest(String requestString,
			String filePath) {
	}

	public void didFailedWithPostLargeFileRequest(String requestString,
			String filePath, String serverResponse) {
	}

	/** Socket response **/
	public void socketResponse(String socketIP, int socketPort,
			String serverResponse) {
	}

	public void socketBadResponse(String socketIP, int socketPort,
			String serverResponse) {
	}
}
