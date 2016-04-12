package com.kingwaytek.cpami.bykingTablet.Unzip;

import android.util.Log;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Apache Ant 工具org.apache.tools.zip
 */
public class AntZip {
	private ZipFile zipFile;
	private static int bufSize; // size of bytes
	private byte[] buf;
	/*Setting Callback*/
	private static ZipCallBack zCB;
	private static final int bufferSize = 1024 * 1024;
	private static long beforTime = 0;
	/*解壓縮Entry List*/
	private static final List<ZipEntry> entries = new ArrayList<ZipEntry>();
	/*Entry Size*/
	private static long entrySize = 0;
	/*解壓完成 Size*/
	private static long finishSize = 0;
	/*解壓完成檔案 數量*/
	private static int fileSize = 0;
	/*解壓檔案數量*/
	private static int totalFileSize = 0;
	
	public AntZip() {
		this(bufferSize);
		init();
	}

	/**
	 * 
	 * 
	 * @param bufSize
	 *            設定緩衝大小
	 */
	public AntZip(int bufSize) {
		AntZip.bufSize = bufSize;
		this.buf = new byte[AntZip.bufSize];
		init();
	}

	public void init(){
		beforTime = System.currentTimeMillis();
		entrySize = 0;
		finishSize = 0;
		fileSize = 0;
		totalFileSize = 0;
		entries.clear();
	}
	/**
	 * 解壓指定路徑zip文件。其中"GB18030"解决中文亂碼
	 * 多執行緒解壓縮
	 * @param unZipFileName
	 *            zip File name
	 * @param outputPath
	 *            輸出路徑
	 */
	public void unMultiZip(final String unZipFileName, final String outputPath) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.execute(new Runnable() {

			@Override
			public void run() {
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(new File(unZipFileName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				InitialZipEntry(outputPath, zipFile);

				try {
					multiThreadUnZip(zipFile, entries, outputPath);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					zCB.onZipFail();
				}
			}
		});
	}
	/**
	 * Initialize File and get zipEntry list
	 * @param outputPath
	 * @param zipFile
	 */
	private void InitialZipEntry(final String outputPath, ZipFile zipFile) {
		@SuppressWarnings("unchecked")
        Enumeration<? extends ZipEntry> entriesTemp = zipFile.getEntries();
		while (entriesTemp.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entriesTemp.nextElement();
			File file = new File(outputPath + File.separator
					+ entry.getName());

			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}

			}
			if (entry != null) {
				if (!entry.isDirectory())
					entrySize += entry.getSize();

			}
			entries.add(entry);
		}
	}
	/**
	 * 解壓指定路徑zip文件。其中"GB18030"解决中文亂碼
	 * 
	 * @param unZipFileName
	 *            zip File name
	 * @param outputPath
	 *            輸出路徑
	 */
	public void unZip(final String unZipFileName, final String outputPath) {
		
		FileOutputStream fileOut;
		File file;
		InputStream inputStream;
		BufferedInputStream bi = null;

		try {
			this.zipFile = new ZipFile(unZipFileName);

			InitialZipEntry(outputPath, this.zipFile);
			totalFileSize = entries.size();
			for (ZipEntry ze : entries) {
				file = new File(outputPath + ze.getName());
				fileSize++;
				if (!ze.isDirectory()) {
					inputStream = zipFile.getInputStream(ze);
					bi = new BufferedInputStream(inputStream);
					fileOut = new FileOutputStream(file);

					int readedBytes;
					while ((readedBytes = bi.read(this.buf)) > 0) {

						fileOut.write(this.buf, 0, readedBytes);
						finishSize += readedBytes;
						zCB.onZipCB(finishSize, entrySize, fileSize,
								totalFileSize);
						fileOut.flush();
					}
					fileOut.close();
					inputStream.close();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			zCB.onZipFail();
		} finally {
			if (bi != null) {
				try {
					bi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.zipFile != null) {
				try {
					zCB.onZipSuccess(0);
					this.zipFile.close();
					Log.e("DEBUG", "diff Time = " + ((System.currentTimeMillis() - beforTime) / 1024));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public void multiThreadUnZip(ZipFile zipFile, List<ZipEntry> entries,
			String outputDirectory) throws IOException {
		totalFileSize = entries.size();
		ExecutorService es = Executors.newFixedThreadPool(5);
		for (ZipEntry ze : entries) {

			MultiThreadEntry mte = new MultiThreadEntry(zipFile, ze,
					outputDirectory);
			es.execute(mte);
		}
	}

	

	/*
	 * Use MultiThread to read each entry
	 */
	private class MultiThreadEntry implements Runnable {
		public final int BUFFER_SIZE = bufferSize;
		private BufferedInputStream bis;
		private ZipEntry zipEntry;
		private String outputDirectory;

		public MultiThreadEntry(ZipFile zipFile, ZipEntry zipEntry,
				String outputDirectory) throws IOException {
			this.zipEntry = zipEntry;
			this.outputDirectory = outputDirectory;
			bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
		}

		public void run() {
			try {
				unzipFiles(zipEntry, outputDirectory);
			} catch (IOException e) {
				e.printStackTrace();
				try {
					bis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void unzipFiles(ZipEntry zipEntry, String outputDirectory) throws IOException {
			String entryName = zipEntry.getName();
			File file = new File(outputDirectory + File.separator + entryName);
			fileSize++;
			if (!zipEntry.isDirectory()) {
				byte[] data = new byte[BUFFER_SIZE];
				
				
				FileOutputStream fos = new FileOutputStream(file);

				int count;

				while ((count = bis.read(data)) > 0) {
					fos.write(data, 0, count);
					addCount(count);
					zCB.onZipCB(finishSize, entrySize, fileSize, totalFileSize);
//					Log.e("DEBUG"," finishSize = "
//							+ ((float) finishSize / (float) entrySize * 100)
//							+ "%");

					fos.flush();
				}

				fos.close();
				
			}
			if (finishSize == entrySize) {
				Log.e("DEBUG", "diff Time = " + ((System.currentTimeMillis() - beforTime) / 1024));
				zCB.onZipSuccess(0);
			}

		}
	}
	 
	public static synchronized void addCount(int count) {
		finishSize += count;
	}
	/**
	 * 
	 * 
	 * @param bufSize
	 *            設定緩衝大小
	 */
	public void setBufSize(int bufSize) {
		AntZip.bufSize = bufSize;
	}
	/**
	 * 設定 Status Callback
	 * @param zCB
	 */
	public void setZipCallBack(ZipCallBack zCB) {
		AntZip.zCB = zCB;
	}

}
