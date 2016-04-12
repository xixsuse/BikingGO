package com.kingwaytek.api.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.kingwaytek.api.cache.DiskLruCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class FileApi {
	
	final static String FILE_NOMEDIA =".nomedia" ;
	
	/**
	 * 測試是否可以讀寫某個位置,因為4.4.2如果利用 isWriteabble 是不準的
	 * 
	 * @param testFilePath /sdcard/test.tmp 或 /sdcard 或 /sdcard/
	 */
	public static boolean canExtStorageCreatable(String testFilePath) throws IOException{
		/*
    	boolean canWritable = false ;    	
    	boolean isPathNotWithFileName = !testFilePath.endsWith(".tmp");
    	if(isPathNotWithFileName){    		
    		testFilePath = connectFolderAndFileName(testFilePath, "test.tmp");    		
    	}
    	 
    	
    	File f = new File(testFilePath) ;//ext_sd    	
    	try {
			canWritable = f.createNewFile() ;
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	
    	if(canWritable){
    		canWritable = f.delete();
    	}
    	*/
		
		File result = File.createTempFile("test", null,new File(testFilePath));
		if(result != null){
			result.delete();
		}
    	return result != null ;
    }
	
	/**
	 * 將 /sdcard/  與 navi_cache 這樣的名稱做連接,目的是判斷避免串接起 /sdcard//navi_cache這樣兩層的 case
	 */
	public static String connectFolder(String rootPath,String folderName){
		String result = null;				
		if(rootPath != null){
			result = rootPath ;
		}		
		if(folderName != null){
			result += "/" + folderName + "/" ;
		}		
		result = replaceSplitChar (result);		
		return result ;
	}
	
	public static String connectFolder(File rootPath,String folderName){
		String path = null;
		if(rootPath != null){
			path = rootPath.getAbsolutePath();			
		}
		return connectFolder(path,folderName);
	}
	
	public static String connectFolderAndFileName(String rootPath,String fileName){
		String result = null;				
		if(rootPath != null){
			result = rootPath ;
		}		
		if(fileName != null){
			result += "/" + fileName ;
		}		
		result = replaceSplitChar (result);		
		return result ;
	}
	
	static String replaceSplitChar(String path){
		if(path != null){
			path = path.replace("//", "/");
			path = path.replace("\\\\", "\\");
			path = path.replace("\\/", "\\");
			path = path.replace("/\\", "\\");
		}
		return path;
	}
	
	public static void makeDir(String path){
		File file = new File(path);
		file.mkdir(); 
	}
	
	public static boolean moveTo(String sourceFolderPath,String targetFolderPath){
		File sourceFile = new File(sourceFolderPath);
		File targetFile = new File(targetFolderPath);		
		return moveTo(sourceFile,targetFile) ;
	}
	
	public static boolean moveTo(File sourceFile,File targetFile){
		boolean result = false ; 
		result = sourceFile.renameTo(targetFile);
		return result ;
	}
	
	public static boolean createFolderIfNotExist(String folderPath){
		File folder = new File(folderPath);
		boolean bNotExist = !folder.exists();
		boolean result = true ;
		if(bNotExist){
			result = folder.mkdirs();
		}
		return result; 
	}
	
	public static void createParentFolderIfNotExist(String path){		
		try{
			File f = new File(path).getParentFile();
			if(!f.exists()){
				f.mkdirs();
			} 	
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static void deleteFile(String path){
		if(path != null && path.length() >0){
			try{
				File file = new File(path);
				if(file.exists()){
					file.delete();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteDir(String file){
		deleteDir(new File(file));
	}
	
	public static void deleteDir(File file){ 
		if (file.exists()){
			if (file.listFiles() != null) {
				for (File child : file.listFiles()) {
					if (child.isDirectory()) {
						deleteDir(child);
					} else {
						child.delete();
					}
				}
			}
			file.delete();
		}
	} 
	
	/**
	 * 複製整個資料夾檔案到另一個資料夾
	 * @param destDirectoryPath
	 * @param srcDirectoryPath
	 */
	public static void copyDirectory(String srcDirectoryPath, String destDirectoryPath) {
		File srcDirectory = new File(srcDirectoryPath);
		File destDirectory = new File(destDirectoryPath);
		try {
			if (srcDirectory.isDirectory()) {
				if (!destDirectory.exists()) {
					destDirectory.mkdirs();
				}

				for (String child : srcDirectory.list()) {
					File srcFile = new File(connectFolderAndFileName(srcDirectoryPath, child));
					File destFile = new File(connectFolderAndFileName(destDirectoryPath, child));
					if(srcFile.isDirectory()) {
						copyDirectory(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
					} else {
						copy(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
					}
				}
			} else {
				copy(srcDirectoryPath, destDirectoryPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 複製單一檔案
	 * @param destFilepath
	 * @param srcFilePath
	 */
	public static void copy(String srcFilePath, String destFilepath) {
		File localFile1 = new File(srcFilePath);
		File localFile2 = new File(destFilepath);
		try {
			if (!localFile2.exists()) {
				localFile2.createNewFile();
			}

			FileInputStream localFIS = new FileInputStream(localFile1);
			FileOutputStream localFOS = new FileOutputStream(localFile2);

			byte[] arrayOfByte = new byte[1024];
			int j;
			for (int i = localFIS.read(arrayOfByte);; i = j) {
				if (i == -1) {
					localFOS.close();
					localFIS.close();
					return;
				}
				localFOS.write(arrayOfByte, 0, i);
				j = localFIS.read(arrayOfByte);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class Size{
		/**
		 * Get System Avail Space
		 * @return MegaBytes (MB)
		 */
		public static int getPathAvailSpace(){		
			File root = Environment.getDataDirectory();
			StatFs sf = new StatFs(root.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			long availSpcae = availCount*blockSize / (1024 * 1024) ; // MB
			//Log.d("File" , "blockSize:" + blockSize+ ",blockCount:" + blockCount + ",BlockTotal:" + blockSize*blockCount/ 1024 + "KB" );
			//Log.d("File" , "AvailCount:" + availCount+ ",availSpace:" + availSpcae  + "MB" );		
			return (int) availSpcae ; 
		}
		
		public static long getFolderSize(File dir) {
		    long size = 0;
		    if(dir.exists()){
			    for (File file : dir.listFiles()) {
			        if (file.isFile()) {                
			            size += file.length();
			        }
			        else{
			            size += getFolderSize(file);
			        }
			    }
		    }
		    return size;
		}
		
		public static long getDirSize(File dir) {
			if (dir.exists()) {
				long result = 0;
				File[] fileList = dir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].isDirectory()) {
						result += getDirSize(fileList[i]);
					} else {
						result += fileList[i].length();
					}
				}
				return result;
			}
			return 0;
		}
		
		public static StatFs getStatFs(String path){		
			StatFs sf ;
			try{			
				sf = new StatFs(path);
			}catch(Exception e){			
				e.printStackTrace();
				sf = new StatFs("/sdcard/");
			}
			return sf;
		}		
	}
		
	public static boolean createNoMediaFile(String path){
		try{
			File fPath = new File(path);
			if(fPath.exists() && path.length() > 0){
				path = checkPathLastSlash(path) ;
				File mPath = new File(path + FILE_NOMEDIA) ;
				return mPath.createNewFile() ; 
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false ;
	}
	
	/**
	 * <h1>See if file path-string has  / , 沒有就自動加上</h1>
	 *   
	 * For example. /mnt/sdcard , 會幫忙加上 /mnt/sdcard/ <br /> 
	 */
	public static String checkPathLastSlash(String path){
		if(!path.substring(path.length()-1).equals("/")){
			path = path + "/" ;
		}
		return path ;
	}
	
	public static class Text{
		/**
		 * 讀取單一個文字檔內容 
		 */
		public static String readTextFile(String filePath){
					
			File f = new File(filePath);
			String result = null; 
			if (f.exists())
			{
				try
				{
					BufferedReader buf = new BufferedReader(new FileReader(f));				
					result = buf.readLine() ;
					buf.close();
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}catch (IOException e){
					e.printStackTrace();
				}catch(IndexOutOfBoundsException e){
					e.printStackTrace();
				}catch(NullPointerException e){
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}			
			}

			return result;
		}
				
		public static void readTextFileByUtf8(String utf8FilePath){		
			String UTF8 = "utf8";
			int BUFFER_SIZE = 8192;

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(utf8FilePath), UTF8),BUFFER_SIZE);
				String strLine = br.readLine() ;
				br.close();
				Log.i("Utf","Utf:" + strLine ) ;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		public static void writeFile(String filePath,String writeText) throws IOException{
			File f = new File(filePath);
			if(!f.exists()){			
				f.createNewFile();
			}
			FileWriter fw;
			
			fw = new FileWriter(filePath);			
			fw.write(writeText); 
			fw.flush();
			fw.close();				
			fw = null;		
		}
	}	
	
	public static class FileSystem{
		/** 
		 * 修改Linux檔案權限為 555 
		 */
		public static void changeFileMode(String dstFilePath){
		    ProcessBuilder cmd;
		    String result = "";        
		    try{
		    	//Log.v("FileUntils","Change " + dstFilePath +" File Mode 555 .");
		    	String[] args = { "chmod", "555" ,dstFilePath}  ;
				cmd = new ProcessBuilder(args);
				Process process = cmd.start();
				InputStream in = process.getInputStream();
				byte[] re = new byte[1024];
				while (in.read(re) != -1)
				{
					result = result + new String(re);
				}
				in.close();
				process.waitFor();
				//Log.v("FileUntils","Process Result:" + result);
			}catch (IOException ex){
				//Log.v("FileUntils",dstFilePath + "Copy Error!!!");
				ex.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}
		
		public static void chmod(File file) {
		     try { 
		         String[] command = {"chmod", "777", file.getPath()};  
		         ProcessBuilder builder = new ProcessBuilder(command); 
		         builder.start();  
		     } catch (IOException e) { 
		        e.printStackTrace();  
		     }
		}
		
		public static void changeDirMode(String destDirPath){
		    ProcessBuilder cmd;
		    //String result = "";        
		    try{
		    	//Log.v("FileUntils","Change " + dstFilePath +" File Mode 555 .");
		    	String[] args = { "chmod", "555" ,destDirPath}  ;
				cmd = new ProcessBuilder(args);
				Process process = cmd.start();
				InputStreamReader in = new InputStreamReader(process.getInputStream());
				BufferedReader br = new BufferedReader(in);

			    String lineRead;
			    while ((lineRead = br.readLine()) != null) {
			    }

//			    int rc = p.waitFor();
//				byte[] re = new byte[1024];
//				while (in.read(re) != -1)
//				{
//					result = result + new String(re);
//				}
			    br.close();
//				in.close();
				int result = process.waitFor();
				Log.v("FileUntils","Process Result:" + result);
			}catch (IOException ex){
				Log.v("FileUntils",destDirPath + "Copy Error!!!");
				ex.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}
	}
	
	public static class BitmapApi{
		public final static int FETCH_IMAGE_TIMEOUT = 8*1000 ;
		public static Bitmap getBitmapFromUrl(String strURL){
			Bitmap bitmap = null;
			try {
				URL url = new URL(strURL);		
				URLConnection urlConnection = url.openConnection();
				urlConnection.setReadTimeout(FETCH_IMAGE_TIMEOUT);
		        bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());				
		    }catch (OutOfMemoryError e){
		    	e.printStackTrace();
		    } catch (Exception e) {	        
		        e.printStackTrace();
		    }
			return bitmap ;
		}
		
		public static Bitmap getBitmapFromStorage(String photoPath){
			Bitmap bitmap = null;
			try{
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				bitmap = BitmapFactory.decodeFile(photoPath, options);
			}catch(OutOfMemoryError e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			return bitmap ; 
		}		
		
		public static void saveBitmapToSdcard(Bitmap bitmap,String fileName){
			if(bitmap != null && fileName != null){
				try {
			       FileOutputStream out = new FileOutputStream(fileName);
			       bitmap.compress(DiskLruCache.mCompressFormat, DiskLruCache.mCompressQuality, out);
			       out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public static Bitmap getBitmapFromStorageBySampleSize(String photoPath ,int sampleSize){
		
			Bitmap bitmap = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			// 獲取這個圖片的寬和高
			try{
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeFile(photoPath, options); // 此時返回bitmap為空
			}catch(OutOfMemoryError e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			int inSampleSize = 1;
			// 先根據寬度進行縮小
			while (options.outWidth / inSampleSize > sampleSize) {
				inSampleSize++;
			}
			// 然後根據高度進行縮小
			while (options.outHeight / inSampleSize > sampleSize) {
				inSampleSize++;
			}
			// 重新讀入圖片，注意這次要把options.inJustDecodeBounds 設為 false哦
			try{
				options.inJustDecodeBounds = false;
				options.inSampleSize = inSampleSize;
				bitmap = BitmapFactory.decodeFile(photoPath, options);
			}catch(OutOfMemoryError e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			return bitmap;
		}	
	}	
}