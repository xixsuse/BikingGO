package com.kingwaytek.api.utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

// TODO 還不能用
public class ImageFetcher extends AsyncTask<String, Object, Boolean>{
	
	Activity mActivity ;
	ListView mListView ;
	static ArrayList<String> mQueueUrl = new ArrayList<String>();
	String mNowUrl ; 
	public ImageFetcher(Activity activity,ListView listView){
		this.mActivity = activity ;
		this.mListView = listView ;		
	}

	@Override
	protected Boolean doInBackground(String... params) {		
		String url = params[0] ;
		mNowUrl = url ; 
		if(mQueueUrl.contains(url)){ // 如果Queeu中有了就不要進行Fetch
			return false ; 
		}		
		mQueueUrl.add(url);
		/*
		String urlEncode = WebService.converUrlToBase64(url);							
		File f = new File(DataDirectoryHelper.getCachePath(mActivity));
		if(!f.exists()){
			f.mkdir();			
		}	
		WebService.getFileFromUrl(params[0], DataDirectoryHelper.getCachePath(mActivity) +  urlEncode + ".jpg") ;
		*/
		return null; 
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if(result){
			if(mListView != null){
				((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();			
			}
		}else{
			// 如果下載失敗且檔案不再, 將從Queue中釋放
//			String urlEncode = WebService.converUrlToBase64(mNowUrl);							
//			File f = new File(IUtils.GetTempFolder() +  urlEncode + ".jpg");
//			if(!f.exists()){
//				mQueueUrl.remove(mNowUrl);
//			}
		}
		//Toast.makeText(mActivity , "Fetch done:" + result , Toast.LENGTH_SHORT).show();
	}
}