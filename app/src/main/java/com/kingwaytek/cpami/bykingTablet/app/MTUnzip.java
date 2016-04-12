package com.kingwaytek.cpami.bykingTablet.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MTUnzip {

	private static int compeletecount = 0;

	// private static final List<ZipEntry> entries = new ArrayList<ZipEntry>();

	public static void unZipDirectory(String zipFileDirectory,
			String outputDirectory, Handler MTUnzipHandler)
			throws ZipException, IOException {
		/* 資料查中的所有zip都解 */
		File file = new File(zipFileDirectory);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".zip")) {
				unzip(zipFileDirectory + File.separator + files[i].getName(),
						outputDirectory, MTUnzipHandler);
			}
		}
	}

	/*
	 * unzip the file to destination directory @param fileURI hold the file
	 * location @param outputDirectory hold the output directory of unpacked
	 * files @throws ZipException,IOException
	 */
	public static void unzip(String fileURI, String outputDirectory,
			Handler MTUnzipHandler) throws ZipException, IOException {
		/**
		 * fileURI = .zip檔路徑 , outputDirectory = 解壓縮至某資料夾
		 */
		List<ZipEntry> entries = new ArrayList<ZipEntry>();
		File file = new File(fileURI);
		unzip(file, outputDirectory, entries, MTUnzipHandler);
	}

	/*
	 * unzip the file to destination directory @param file hold the zip file
	 * 
	 * @param outputDirectory hold the output directory of unpacked files
	 * 
	 * @throws ZipException,IOException
	 */
	public static void unzip(File file, String outputDirectory,
			List<ZipEntry> entries, Handler MTUnzipHandler)
			throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(file);
		/* 找到zip檔裡所有檔案 */
		Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();

		while (enu.hasMoreElements()) {

			ZipEntry entry = (ZipEntry) enu.nextElement();// 指向下一個

			if (entry.isDirectory()) {// 如果是資料夾
				String fileName = entry.getName().substring(0,
						entry.getName().length() - 1);
				String directoryPath = outputDirectory + File.separator
						+ fileName;
				File directory = new File(directoryPath);
				if (!directory.exists()) {
					if (directory.mkdir()) {
						System.out.println("create file:" + directoryPath);
					} else {
						System.out
								.println("create file error:" + directoryPath);
					}
				}
			}
			entries.add(entry);
		}
		unzip(zipFile, entries, outputDirectory, MTUnzipHandler);
	}

	public static void unzip(ZipFile zipFile, List<ZipEntry> entries,
			String outputDirectory, Handler MTUnzipHandler) throws IOException {
		Iterator<ZipEntry> it = entries.iterator();
		it = entries.iterator();

		int zipThreadNum = 0;
		/* 計算將開啟幾個Thread */
		while (it.hasNext()) {
			ZipEntry zipEntry = (ZipEntry) it.next();
			zipThreadNum++;
		}
		Message message = new Message();
		message.what = 3;
		message.obj = zipThreadNum;
		MTUnzipHandler.sendMessage(message);

		/* 開始Thread解壓縮 */
		it = entries.iterator();
		int count = 0;
		while (it.hasNext()) {
			ZipEntry zipEntry = (ZipEntry) it.next();
			MultiThreadEntry mte = new MultiThreadEntry(zipFile, zipEntry,
					outputDirectory, MTUnzipHandler);
			Thread thread = new Thread(mte);
			thread.start();
			System.out.println("開啟Thread開始解壓縮:" + count++);
		}

	}

	/*
	 * Use MultiThread to read each entry
	 */
	private static class MultiThreadEntry implements Runnable {
		public static final int BUFFER_SIZE = 2048;
		private BufferedInputStream bis;
		private ZipEntry zipEntry;
		private String outputDirectory;
		private Handler MTUnzipHandler;

		public MultiThreadEntry(ZipFile zipFile, ZipEntry zipEntry,
				String outputDirectory, Handler MTUnzipHandler)
				throws IOException {
			this.zipEntry = zipEntry;
			this.outputDirectory = outputDirectory;
			this.MTUnzipHandler = MTUnzipHandler;
			bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
		}

		public void run() {
			try {
				unzipFiles(zipEntry, outputDirectory, MTUnzipHandler);
			} catch (IOException e) {
				try {
					bis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("**********IOException**********");
					Message message = new Message();
					message.what = 2;
					MTUnzipHandler.sendMessage(message);
				}
			} finally {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("**********IOException**********");
					Message message = new Message();
					message.what = 2;
					MTUnzipHandler.sendMessage(message);
				}
			}
		}

		public void unzipFiles(ZipEntry zipEntry, String outputDirectory,
				Handler MTUnzipHandler) throws IOException {
			byte[] data = new byte[BUFFER_SIZE];

			String entryName = zipEntry.getName();

			if (zipEntry.isDirectory()) {
				Message message = new Message();
				message.what = 2;
				MTUnzipHandler.sendMessage(message);
				System.out.println("Thread compelete:" + compeletecount++);
			} else {
				FileOutputStream fos = new FileOutputStream(outputDirectory
						+ File.separator + entryName);
				System.out.println("*************" + outputDirectory
						+ File.separator + entryName);
				BufferedOutputStream bos = new BufferedOutputStream(fos,
						BUFFER_SIZE);
				int count = 0;
				while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
					bos.write(data, 0, count);
				}
				Message message = new Message();
				message.what = 2;
				MTUnzipHandler.sendMessage(message);
				System.out.println("Thread compelete:" + compeletecount++);
				bos.flush();
				bos.close();
				fos.close();
			}
		}
	}
}
