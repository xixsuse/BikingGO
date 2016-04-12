package com.kingwaytek.cpami.bykingTablet.download;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadTask implements Runnable {
	private static final String TAG = "DownloadTask";
	private File saveDir;
	public String path;
	private FileDownload loader;
	private Handler handler;
	// 檔案大小
	private int maxSize;
	public static String urlPath;
	private Context mContext;

	

	public DownloadTask(Context mContext, File saveDir, Handler handler) {
		this.mContext = mContext;
		this.saveDir = saveDir;
		this.handler = handler;
	}

	public void exit() {
		if (loader != null)
			loader.exit();
	}

	private DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
		@Override
		public void onDownloadSize(String apkName, int size) {
			// 回傳進度
			Message msg = handler.obtainMessage();
			msg.what = DownloadMapUIManager.PROCESSING;
			msg.getData().putInt("size", size);
			msg.getData().putInt("MAXsize", maxSize);
			msg.getData().putString("name", apkName);
			Log.e(TAG, "onDownloadSize size = " + size + "/" + maxSize);
			Log.e(TAG, "onDownloadSize name = " + apkName);
			handler.sendMessage(msg);
			
		}

		@Override
		public void onDownloadFinish(String substring) {
			Message msg = handler.obtainMessage();
			msg.what = DownloadMapUIManager.FINISH;
			msg.getData().putString("name", substring);
			handler.sendMessage(msg);
		}

		@Override
		public void onDownloadFail(String substring) {
			Message msg = handler.obtainMessage();
			msg.what = DownloadMapUIManager.FAILURE;
			msg.getData().putString("name", substring);
			handler.sendMessage(msg);
		}

	};

	@Override
	public void run() {
		try {
			Message msg = handler.obtainMessage();
			msg.what = DownloadMapUIManager.START;
			handler.sendMessage(msg);
			
			loader = new FileDownload(mContext, (urlPath + path), saveDir, 1);

			maxSize = loader.getFileSize();
			if(CheckSpaceUtility.CheckSpace(maxSize))
				loader.download(downloadProgressListener);
			else{
				handler.sendMessage(handler.obtainMessage(DownloadMapUIManager.SPACENOEGO,(maxSize / 1024f / 1024f)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(DownloadMapUIManager.FAILURE));
		}

	}
}
