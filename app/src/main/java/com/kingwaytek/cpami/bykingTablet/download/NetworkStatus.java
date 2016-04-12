package com.kingwaytek.cpami.bykingTablet.download;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 網路狀態
 * 
 * @author eden
 * @date 2013/12/9下午6:23:26
 */
public class NetworkStatus {
	public static Dialog dialog;

	/**
	 * 是否有開啟網路
	 * 
	 * @param con
	 *            Context
	 * @return boolean true or false
	 * @date 2013/12/9 下午6:23:42
	 */
	public static boolean isMobileNetworkAvailable(final Context con) {

		boolean success = false;
		// 獲得網路連接服務
		final ConnectivityManager connManager = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 網路連線資訊
		final NetworkInfo info = connManager.getActiveNetworkInfo();

		if (info != null) {
			if (info.isConnected() && info.isAvailable())
				success = true;
			else
				success = false;
		} else {
		}
		if (!success) {
			// 無連線
			return false;
		} else {
			// 有連線
			return true;
		}

	}

//	private static Handler NetHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			NetworkStatus.NetWorkTask.getInstance((CanConnectCallBack) msg.obj)
//					.execute();
//
//		}
//	};
//
//	public static class NetWorkTask extends AsyncTask<Void, Void, Boolean> {
//
//		// 當沒連上Google,每3秒檢查一次
//		private static final long TESTNETTIME = 3000;
//		private static CanConnectCallBack MyconnectCallBack;
//		private static NetWorkTask myNetWorkTask;
//
//		public static NetWorkTask getInstance(CanConnectCallBack connectCallBack) {
//			MyconnectCallBack = connectCallBack;
//			if (myNetWorkTask != null) {
//				myNetWorkTask.cancel(true);
//				myNetWorkTask = null;
//				myNetWorkTask = new NetWorkTask();
//			} else if (myNetWorkTask == null) {
//				myNetWorkTask = new NetWorkTask();
//			}
//			return myNetWorkTask;
//		}
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			try {
//				URL url = new URL("http://www.google.com");
//				HttpURLConnection urlc = (HttpURLConnection) url
//						.openConnection();
//				urlc.setConnectTimeout(3000);
//				urlc.connect();
//				if (urlc.getResponseCode() == 200) {
//					return true;
//				} else {
//					return false;
//				}
//			} catch (MalformedURLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return false;
//		}
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//
//			super.onPostExecute(result);
//			MyconnectCallBack.Connected(result);
//			if (!result) {
//				Message msg = NetHandler.obtainMessage(0, MyconnectCallBack);
//				NetHandler.sendMessageDelayed(msg, TESTNETTIME);
//
//			} else {
//				if (dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//				}
//			}
//		}
//
//		public interface CanConnectCallBack {
//			public void Connected(Boolean isOnline);
//		}
//
//	}

}
