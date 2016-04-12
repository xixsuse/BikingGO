package com.kingwaytek.cpami.bykingTablet.app;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class NewMain {

	private String url;
	private String path;
	private long filesize;
	private RemoteFile rf;

	public NewMain(String DownloadURL) {
		url = DownloadURL;
		path = Environment.getExternalStorageDirectory() + "/BikingTemp" + DownloadURL.substring(url.lastIndexOf("/"));
		rf = getRemoteFile(url);
	}

	public boolean isDownloadComplete() {
		long remoteSize = rf.size;
		File f = new File(path);

		if (f.exists()) {
			filesize = f.length();
		} else {
			filesize = 0;
		}
		
		if (remoteSize == filesize) {
			return true;
		} else {
			return false;
		}
	}

	public void RunDownLoad(Handler MultipleDownLoadHandler) {
		// TODO Auto-generated method stub
		// final String url = DownloadURL;
		// final String path = Environment.getExternalStorageDirectory() +
		// "/BikingTemp"
		// + DownloadURL.substring(url.lastIndexOf("/"));
		// final long filesize;
		// RemoteFile rf = getRemoteFile(url);
		long remoteSize = rf.size;
		final String realUrl = rf.realUrl;

		System.out.println("URL = " + realUrl);
		System.out.println("remoteSize = " + remoteSize);
		Message msg = new Message();
		msg.what = 1;
		msg.obj = remoteSize;
		MultipleDownLoadHandler.sendMessage(msg);

		File BikingTemp = new File(Environment.getExternalStorageDirectory() + "/BikingTemp");

		if (!BikingTemp.exists()) {
			BikingTemp.mkdir();
		}

		File f = new File(path);

		if (f.exists()) {
			filesize = f.length();
		} else {
			filesize = 0;
		}
		if (filesize == remoteSize) {
			Message msg3 = new Message();
			msg3.what = 3;

			MultipleDownLoadHandler.sendMessage(msg3);
			return;
		}
		System.out.println("filesize = " + filesize);
		if (filesize < remoteSize) {
			try {
				System.out.println("Start");
				URL u = new URL(realUrl);
				HttpURLConnection connection = (HttpURLConnection) u.openConnection();
				connection.setRequestProperty("RANGE", "bytes=" + filesize + "-");

				connection.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.1.4322)");
				InputStream input = connection.getInputStream();
				RandomAccessFile SavedFile = new RandomAccessFile(path, "rw");
				SavedFile.seek(filesize);
				byte[] b = new byte[1024];
				int nRead;
				long readed = filesize;
				while ((nRead = input.read(b, 0, 1024)) > 0) {
					readed += nRead;
					Message msg2 = new Message();
					msg2.what = 2;
					msg2.obj = readed;
					MultipleDownLoadHandler.sendMessage(msg2);
					System.out.println("readed = " + readed);
					SavedFile.write(b, 0, nRead);
				}
				connection.disconnect();
				SavedFile.close();
				Message msg3 = new Message();
				msg3.what = 3;

				MultipleDownLoadHandler.sendMessage(msg3);
			} catch (Exception e) {

			}

		}
	}

	public static RemoteFile getRemoteFile(String url) {
		long size = 0;
		String realUrl = "";
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
			size = conn.getContentLength();
			realUrl = conn.getURL().toString();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		RemoteFile rf = new RemoteFile(size, realUrl);
		return rf;
	}

}

class RemoteFile {
	long size;
	String realUrl;

	RemoteFile(long size, String realUrl) {
		this.size = size;
		this.realUrl = realUrl;
	}
}
