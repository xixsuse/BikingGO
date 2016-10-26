package com.kingwaytek.cpami.biking.utilities;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/** 
 * 用來讀取檔案版號，取最後面四個byte
 * @author Jeff.lin 
 */
public class FileUtility
{
	final static int VER_LENGTH = 4 ; 
	final static String VER_FORMAT = "%s.%s.%s.%s" ; 			//  ex 1.YY.MM.DD
	public static final int VERSION_TYPE_WITHOUT_SPLIT 	= 0 ; 
	public static final int VERSION_TYPE_SPLIT_BY_DOT 	= 1 ;  
	
	
	public static String ReadVersion(String filePath,int type) throws IOException{				
		String result = "0"; 
		
		File file = new File(filePath) ;
		long skipLength = file.length() - VER_LENGTH  ;		
		FileInputStream fis = new FileInputStream(new File(filePath)) ;		
					
		fis.skip(skipLength);
		int field_01 = fis.read() ;
		int field_02yy = fis.read() ;
		int field_03mm = fis.read() ;
		int field_04dd = fis.read() ;
		fis.close();
		fis = null ;
		file = null ;
			
		switch(type){
		case VERSION_TYPE_SPLIT_BY_DOT :
			result = String.format(VER_FORMAT,  field_01 , field_02yy , field_03mm , field_04dd ) ;
			break;
		case VERSION_TYPE_WITHOUT_SPLIT :
			result = String.valueOf( field_01 * 1000000 + field_02yy *10000 + field_03mm * 100 + field_04dd ) ;
			break;
		}
			
		return result;		
	}

	public static String ReadTxtFileVersion(String filePath){
		return ReadTxtFileVersion(filePath,VERSION_TYPE_WITHOUT_SPLIT) ;
	}
	
	
	public static String ReadTxtFileVersion(String filePath , int type){
		
		String ver = "0";
		File f = new File(filePath);
		StringBuilder sb = new StringBuilder();
		if (f.exists())
		{

			FileReader fileReader;
			try
			{
				fileReader = new FileReader(f);
				int mChar = 0;
				while ((mChar = fileReader.read()) != -1)
				{
					sb.append((char)mChar);
				}				
				fileReader.close();
				String strVer = sb.toString() ;
				f = null ;
				sb = null ;				
				
				if(type == VERSION_TYPE_WITHOUT_SPLIT){
					String[] strVerNums = strVer.split("\\."); // indexOutOfBounds
					ver = String.valueOf(Integer.parseInt(strVerNums[0]) * 1000000 
						+ Integer.parseInt(strVerNums[1]) * 10000 
						+ Integer.parseInt(strVerNums[2]) * 100
						+ Integer.parseInt(strVerNums[3])) ;
				}else{
					ver = strVer ;
				}
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
			}
			
		}

		return ver;
	}

	/**
	 * 讀取單一個文字檔內容 
	 * @param filePath
	 * @return 如果沒有該檔或者錯誤就回傳null
	 */
	public static String ReadOneLineTxtFileData(String filePath){
				
		File f = new File(filePath);
		String result = null; 
		if (f.exists())
		{
			try
			{
				@SuppressWarnings("resource")
				BufferedReader buf = new BufferedReader(new FileReader(f));				
				result = buf.readLine() ;
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
	
	
	/**
	 * Get System Avail Space
	 * @return MegaBytes (MB)
	 */
	@SuppressWarnings("deprecation")
	public static int GetRootAvailSpace(){
		
		File root = Environment.getDataDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		long availSpcae = availCount*blockSize / (1024 * 1024) ; // MB
		//Log.d("File" , "blockSize:" + blockSize+ ",blockCount:" + blockCount + ",BlockTotal:" + blockSize*blockCount/ 1024 + "KB" );
		//Log.d("File" , "AvailCount:" + availCount+ ",availSpace:" + availSpcae  + "MB" );
		
		return (int) availSpcae ; 
	}
	
	/* File to File */	
	public static void Copy(String destFilepath, String srcFilePath)
    {
      File localFile1 = new File(srcFilePath);
      File localFile2 = new File(destFilepath);
      
      try
      {
        FileInputStream localFIS = new FileInputStream(localFile1);
        FileOutputStream localFOS = new FileOutputStream(localFile2);
        
        byte[] arrayOfByte = new byte[1024];
        int j;
        for (int i = localFIS.read(arrayOfByte); ; i = j)
        {
          if (i == -1)
          {
            localFOS.close();
            localFIS.close();
            return;
          }
          localFOS.write(arrayOfByte, 0, i);
          j = localFIS.read(arrayOfByte);
        }
      }
      catch (Exception localException)
      {
    	  Log.v("File", destFilepath +"檔案複製錯誤!");
    	  localException.printStackTrace();
      }
    }
	
	public static void WriteFile(String filePath,String str){
		FileWriter fw;
		try
		{
			fw = new FileWriter(filePath);
			fw.write(str); 
			fw.flush();
			fw.close();
		}
		catch (IOException e)
		{			
			e.printStackTrace();
		}
		
		fw = null;		
	}
	
	
	/** 修改Linux檔案權限為 555 */
	public static void ChangeFileMode(String dstFilePath){
		// Run cmd
	    ProcessBuilder cmd;
	    String result = "";        
	    try{
	    	Log.v("FileUntils","Change " + dstFilePath +" File Mode 555 .");
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
			Log.v("FileUntils","Process Result:" + result);
		}
		catch (IOException ex)
		{
			Log.v("FileUntils",dstFilePath + "Copy Error!!!");
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Caculate the folder total size
	 * @param dir
	 * @return 
	 */
	public static long GetFolderSize(File dir) {
	    long size = 0;
	    if(dir.exists()){
		    for (File file : dir.listFiles()) {
		        if (file.isFile()) {                
		            size += file.length();
		        }
		        else{
		            size += GetFolderSize(file);
		        }
		    }
	    }
	    return size;
	}
	
	// Hero 會造成無法抓取到 /mnt/sdcard/空間
	public static StatFs GetStatFs(String path){		
		StatFs sf ;
		try{			
			sf = new StatFs(path);
		}catch(Exception e){			
			e.printStackTrace();
			sf = new StatFs("/sdcard/");
		}
		return sf;
	}
	
	final static String FILE_NOMEDIA_NAME =".nomedia" ; 
	public static boolean CreateNoMediaFile(String path){
		try{
			File fPath = new File(path);
			if(fPath.exists() && path.length() > 0){
				path = checkPathLastSlash(path) ;
				File mPath = new File(path + FILE_NOMEDIA_NAME) ;
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

	public static void deleteDir(File f)
	{
		if (f.exists())
		{
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory()){
					deleteDir(files[i]);
				}
				else{
					files[i].delete();					
				}
			}
			f.delete();
		}
	}
}