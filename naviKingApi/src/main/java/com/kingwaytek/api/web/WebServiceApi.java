package com.kingwaytek.api.web;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class WebServiceApi {
	
	public static boolean getFileFromUrl(String urlPath ,String filePath){		
		boolean bSuccess = false ; 
		try
		{	
			URL url = new URL(urlPath);			
			HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();
			httpConnect.setDoOutput(true);
			httpConnect.connect();
			
			if(httpConnect.getResponseCode() == HttpURLConnection.HTTP_OK ){
				
				byte[] byteData = new byte[1024] ;
				BufferedInputStream bis = new BufferedInputStream(httpConnect.getInputStream());
				FileOutputStream fileStream = null ;		 
				File f = new File(filePath);
				if(!f.exists()) f.createNewFile();
				fileStream = new FileOutputStream(new File(filePath));
				int len = bis.read(byteData) ;				
				while(len > 0){					
					fileStream.write(byteData,0,len);
					len = bis.read(byteData);
				}
				bis.close();
				fileStream.flush();
				fileStream.close();				
			}else if(httpConnect.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR){
				
			}
			httpConnect.disconnect();				
			bSuccess = true ;
		}catch (FileNotFoundException e){
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
		
		return bSuccess ;
	}
	

	public static String getResponse(boolean bGzipFormat,HttpEntity httpEntiry) throws IllegalStateException, IOException{
		String result = null ;
		if(bGzipFormat){
			result = getResponseFromGzipStream(httpEntiry);
		}else{
			result = getResponseFromStream(httpEntiry);
		}
		return result ;
	}
	
	static String getResponseFromGzipStream(HttpEntity httpEntiry) throws IllegalStateException, IOException{
		String result = null ;
		GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(httpEntiry.getContent()));
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(is));
		String keyin = null;
		StringBuilder sbtemp = new StringBuilder();		
		while((keyin = buffReader.readLine()) != null) { 
			sbtemp.append(keyin);        	  
		}
		result = sbtemp.toString();
		buffReader.close();
		return result ;
	}
	
	static String getResponseFromStream(HttpEntity httpEntiry) throws IllegalStateException, IOException{
		String result = null ;
		BufferedInputStream bis = new BufferedInputStream(httpEntiry.getContent());
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(bis));
		String keyin = null;
		StringBuilder sbtemp = new StringBuilder();		
		while((keyin = buffReader.readLine()) != null) { 
			sbtemp.append(keyin);        	  
		}
		result = sbtemp.toString();
		buffReader.close();
		return result ;
	}
	
	/** 判斷是否包含 Gzip回傳格式 */
	public static boolean isHttpCompressHeader(HttpResponse response){		
		boolean bContain = false ; 
		Header[] headers = response.getHeaders(HTTP.CONTENT_ENCODING); 
		if(headers != null && headers.length >0){
			bContain = true ;
		}		
		return bContain ; 
	}
	
	public static HttpParams getHttpParamsWithTimeOut(int timeount){
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, timeount);
		HttpConnectionParams.setSoTimeout(httpParams, timeount);		
		return httpParams;
	}
	
	public static void setHttpPostHeader(HttpPost httpPost,boolean bUsingJson){		
		if(bUsingJson){
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");	
		}else{
			httpPost.setHeader(HTTP.CONTENT_TYPE, "text/xml; charset=utf-8");	
		}		
		httpPost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		httpPost.setHeader("Accept-Language", "zh-TW,en-US;q=0.8,zh;q=0.6,en;q=0.4");		 
	}
	
	public static void setHttpPostEntry(HttpPost httpPost,final String POST_DATA) throws UnsupportedEncodingException{
		httpPost.setEntity(new StringEntity(POST_DATA,HTTP.UTF_8));
	}
}