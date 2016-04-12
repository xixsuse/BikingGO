package com.kingwaytek.cpami.bykingTablet.download;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownload {
	private static final String TAG = "FileDownloader";
	private Context context;
	private FileServices fileService;
	/* 停止下載 */
	private boolean exit;
	/* 已下載文件長度 */
	private int downloadSize = 0;
	/* 原始文件長度 */
	private int fileSize = 0;
	/* 線程數 */
	private DownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFile;
	/* 緩存各線程下載長度 */
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* 每條線程下載長度 */
	private int block;
	/* 下載路徑 */
	private String downloadUrl;

	public int getThreadSize() {
		return threads.length;
	}

	public void exit() {
		this.exit = true;
	}

	public boolean getExit() {
		return this.exit;
	}

	public int getFileSize() {
		return fileSize;
	}

	protected synchronized void append(int size) {
		downloadSize += size;
	}

	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);
		this.fileService.update(this.downloadUrl, threadId, pos);
	}
	private final String USER_AGENT = "Mozilla/5.0";
	/**
	 * 下載器
	 * 
	 * @param downloadUrl
	 *            下載路徑
	 * @param fileSaveDir
	 *            文件保存目錄
	 * @param threadNum
	 *            下載線程數
	 */
	public FileDownload(Context context, String downloadUrl, File fileSaveDir,
			int threadNum) {
		try {
			this.context = context;
			this.downloadUrl = downloadUrl;
			fileService = new FileServices(this.context);
			URL url = new URL(this.downloadUrl);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(),
					url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
			url = uri.toURL();
			if (!fileSaveDir.exists())
				fileSaveDir.mkdirs();
			this.threads = new DownloadThread[threadNum];
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type","application/zip");
			conn.setRequestProperty("Accept","*/*");
			conn.setConnectTimeout(200 * 1000);
			//add request header
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Connection", "keep-alive");
			conn.connect();
			printResponseHeader(conn);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				this.fileSize = conn.getContentLength();
				if (this.fileSize <= 0)
					throw new RuntimeException("Unkown file size ");

				Log.d("Eden", "fileSize = " + fileSize);
				String filename = getFileName(conn);
				Log.d("Eden", "filename = " + filename);
				this.saveFile = new File(fileSaveDir, filename);
				if(!this.saveFile.exists()){
					fileService.delete(this.downloadUrl);// 下載失敗,重新下載
				}
				Map<Integer, Integer> logdata = fileService
						.getData(downloadUrl);
				if (logdata.size() > 0) {
					for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
						data.put(entry.getKey(), entry.getValue());
				}
				if (this.data.size() == this.threads.length) {
					for (int i = 0; i < this.threads.length; i++) {
						this.downloadSize += this.data.get(i + 1);
					}
					Log.d("Eden", "已下載長度 = " + downloadSize);
				}
				this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize
						/ this.threads.length
						: this.fileSize / this.threads.length + 1;
			} else {
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			throw new RuntimeException("don't connection this url");
		}
	}

	private String getFileName(HttpURLConnection conn) {
		String filename = "";
		if (this.downloadUrl.contains("=")) {
			filename = this.downloadUrl.substring(this.downloadUrl
					.lastIndexOf('=') + 1);
		} else {
			filename = this.downloadUrl.substring(this.downloadUrl
					.lastIndexOf('/') + 1);
		}
		if (filename == null || "".equals(filename.trim())) {
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;
				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}
			filename = UUID.randomUUID() + ".tmp";// 取一个文件名
		}
		return filename;
	}

	/**
	 * Start Download
	 * 
	 * @param listener
	 *            listener can set null
	 * @return 已經下載大小
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception {
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");

			if (this.fileSize > 0)
				randOut.setLength(this.fileSize);   // 分配fileSize大小

			randOut.close();

			URL url = new URL(this.downloadUrl);

			if (this.data.size() != this.threads.length) {  // 與原先設定下載線程數不一致,則重新規劃長度
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i + 1, 0);    // 初始化每條線程已經下載的長度為0
				}
				this.downloadSize = 0;
			}
			for (int i = 0; i < this.threads.length; i++) { // 開始下載
				int downLength = this.data.get(i + 1);

				if (downLength < this.block && this.downloadSize < this.fileSize)   // 是否已經下載完成
                {
					this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i + 1), i + 1);
					this.threads[i].setPriority(Thread.MAX_PRIORITY);
					ExecutorService cachedThreadPool = Executors.newSingleThreadExecutor();

					cachedThreadPool.execute(this.threads[i]);
				}
                else
					this.threads[i] = null;
			}
			fileService.delete(this.downloadUrl);// 如果存在下載紀錄，刪除然後重新填加
			fileService.save(this.downloadUrl, this.data);

			boolean notFinish = true;// 下載是否完成

			while (notFinish) { // 判斷所有下載是否完成
				
				if (!NetworkStatus.isMobileNetworkAvailable(context)) {
					if (listener != null)
						listener.onDownloadFail(downloadUrl);
					break;
				}
				Thread.sleep(1 * 1000);
				notFinish = false;// 設定下載完成
				for (int i = 0; i < this.threads.length; i++) {
					
					if (this.threads[i] != null && !this.threads[i].isFinish()) // 如果有未完成下載的
                    {
						notFinish = true;   // 還沒下載完成
						Log.d("Eden", "notFinish||||||");
						if (this.threads[i].getDownLength() == -1) {    // 如果下载失败,再重新下载
							this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i + 1), i + 1);
							this.threads[i].setPriority(Thread.MAX_PRIORITY);
							ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

							cachedThreadPool.execute(this.threads[i]);
						}
					}
				}
				if (listener != null) {
					if (this.downloadSize > 0)
						listener.onDownloadSize(this.downloadUrl, this.downloadSize);   // download Size
				}
			}
			if (downloadSize == this.fileSize) {
				if (listener != null)
					listener.onDownloadFinish(downloadUrl);
				fileService.delete(this.downloadUrl);   // Finish
			}
            else if (downloadSize > this.fileSize) {
				if (listener != null)
					listener.onDownloadFail(downloadUrl);
				fileService.delete(this.downloadUrl);   // 下載失敗,重新下載
			}
			
		} catch (Exception e) {
			print(e.toString());
			if (listener != null)
				listener.onDownloadFail(downloadUrl);
			throw new Exception("file download error");
		}
		return this.downloadSize;
	}

	/**
	 * Http ResponseHeader
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(
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

	/**
	 * Http Header
	 * 
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}
}
