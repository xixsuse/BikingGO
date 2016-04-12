package com.kingwaytek.cpami.bykingTablet.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	private final File saveFile;
	private final URL downUrl;
	private final int block;
	/* 開始位置 */
	private int threadId = -1;
	private int downLength;
	private boolean finish = false;
	private final FileDownload downloader;
	private static final int bufferReaderKB = 256;
	private int currentapiVersion;

	public DownloadThread(FileDownload downloader, URL downUrl, File saveFile,
			int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
		currentapiVersion = android.os.Build.VERSION.SDK_INT;
	}

	@Override
	public void run() {
		if (downLength < block) {// 未下載完成
			try {
				if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					// set timeout parameters for HttpClient
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters,
							30 * 1000);
					HttpConnectionParams
							.setSoTimeout(httpParameters, 30 * 1000);

					DefaultHttpClient httpClient = new DefaultHttpClient();
					httpClient.setParams(httpParameters);
					HttpClient client = httpClient;
					HttpGet request = new HttpGet();
					int startPos = block * (threadId - 1) + downLength;// 開始位置
					int endPos = block * threadId - 1;// 結束位置
					request.addHeader("RANGE", "bytes=" + startPos + "-"
							+ endPos);
					request.addHeader("Connection", "Keep-Alive");
					request.setURI(downUrl.toURI());
					HttpResponse response = client.execute(request);
					StatusLine status = response.getStatusLine();
					
					HttpEntity entity = response.getEntity();
					
					BufferedInputStream bis = new BufferedInputStream(
							entity.getContent());
					// opens input stream from the HTTP connection
					// opens an output stream to save into file
					byte[] buffer = new byte[bufferReaderKB * 1024];
					int offset = 0;
					RandomAccessFile threadfile = new RandomAccessFile(
							this.saveFile, "rwd");

					threadfile.seek(startPos);

					while (!downloader.getExit()
							&& (offset = bis.read(buffer)) != -1) {
						threadfile.write(buffer, 0, offset);
						downLength += offset; // 累加大小

						downloader.update(this.threadId, downLength); // 更新指定線程下載最后的位置
						downloader.append(offset); // 累加已下載大小
						
						// Log.e("DEBUG", this.threadId + " : size = "
						// + (startPos + downLength) + " / (" + startPos
						// + " : " + endPos + ")  downLength = " + downLength
						// + " block = " + block);

					}

					bis.close();
					threadfile.close();
					print("Thread " + this.threadId + " download finish");

				} else {
					HttpURLConnection http = (HttpURLConnection) downUrl
							.openConnection();
					http.setReadTimeout(20 * 1000);
					http.setConnectTimeout(30 * 1000);
					int startPos = block * (threadId - 1) + downLength;// 開始位置
					int endPos = block * threadId - 1;// 結束位置
					// http.setRequestProperty("Accept","*/*");
					//
					http.setRequestProperty("RANGE", "bytes=" + startPos + "-"
							+ endPos);
					// Log.e("Eden", "bytes=" + startPos + "-");
					http.setRequestProperty("Connection", "Keep-Alive");

					// opens input stream from the HTTP connection
					BufferedInputStream bis = new BufferedInputStream(
							http.getInputStream(), bufferReaderKB * 1024);
					printResponseHeader(http);
					// opens an output stream to save into file
					byte[] buffer = new byte[bufferReaderKB * 1024];
					int offset = 0;
					RandomAccessFile threadfile = new RandomAccessFile(
							this.saveFile, "rwd");

					threadfile.seek(startPos);

					while (!downloader.getExit()
							&& (offset = bis.read(buffer)) != -1) {
						threadfile.write(buffer, 0, offset);
						downLength += offset; // 累加大小

						downloader.update(this.threadId, downLength); // 更新指定線程下載最后的位置
						downloader.append(offset); // 累加已下載大小
						
						// Log.e("DEBUG", this.threadId + " : size = "
						// + (startPos + downLength) + " / (" + startPos
						// + " : " + endPos + ")  downLength = " + downLength
						// + " block = " + block);

					}

					bis.close();
					threadfile.close();
					print("Thread " + this.threadId + " download finish");
				}
				this.finish = true;
			} catch (Exception e) {
				this.downLength = -1;
				print("Thread " + this.threadId + ":" + e);
			}
		}
	}

	/**
	 * Http Header
	 * 
	 * @param http
	 */
	private static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	/**
	 * Http ResponseHeader
	 * 
	 * @param http
	 * @return
	 */
	private static Map<String, String> getHttpResponseHeader(
			HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	private static void print(String msg) {
//		if (Macro.isDebug)
			Log.i(TAG, msg);
	}

	public boolean isFinish() {
		return finish;
	}

	public long getDownLength() {
		return downLength;
	}
}
