package com.kingwaytek.cpami.bykingTablet.download;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.Unzip.AntZip;
import com.kingwaytek.cpami.bykingTablet.Unzip.ZipCallBack;
import com.kingwaytek.cpami.bykingTablet.app.Macro;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下載UI Manager
 * 
 * @author eden
 * 
 */
public class DownloadMapUIManager implements ZipCallBack {
	private static final String TAG = "DownloadMapUIManager";
	private static Context mContext;
	private static Resources res;
	/* 下載網址 */
	private static String urlPath;
	/* 下載檔名 */
	private static String fileName;
	/* 下載圖資版本 */
	private static String mapVersion;
	/* Dialog */
	private static Dialog alertProcess = null;
	private static Dialog alertMap = null;
	private static Dialog alertTip = null;
	private static Dialog alertMapUnZip = null;

	/* 完成按鈕 */
	private static Button install_btn;
	/* 重試按鈕 */
	private static Button reset_btn;
	/* 進度Bar Map */
	private static Map<String, ProgressBar> apkMap = new HashMap<String, ProgressBar>();
	/* 進度數值 Map */
	private static Map<String, TextView> valueMap = new HashMap<String, TextView>();
	/* 解壓縮 CallBack */
	private static ZipCallBack zCB;
	/* SharedPreferences 參數 */
	private static SharedPreferences sp;
	private static final String mapDownload = "mapDownload";
	private static final String mapDownloadFinish = "mapDownloadFinish";
	private static final String mapUnzipFinish = "mapUnzipFinish";

	// Handler Status
	public static final int FAILURE = -1;
	public static final int START = 0;
	public static final int PROCESSING = 1;
	public static final int FINISH = 2;
	public static final int SPACENOEGO = 3;
	public static final int ENDDIALOG = 4;
	public static final int STARTDOWNLOAD = 5;
	public static final int CONNECTIONERROR = 6;
	public static boolean isConnection = false;
	// Handler
	private static Handler handler = new UIHandler();

	private static UtilDialog zipCompeleteDailog;
	
	private static final float scale = 0.8f;

	/**
	 * 更新機制
	 * 
	 * @author eden
	 * @param mapVersion
	 *            圖資版本
	 * @param urlPath
	 *            下載網址
	 * @param fileName
	 *            下載檔名
	 */
	public DownloadMapUIManager(final Activity mActivity, Context mContext, String mapVersion, String urlPath, String fileName) {

		DownloadMapUIManager.mContext = mContext;
		DownloadMapUIManager.mapVersion = mapVersion;
		DownloadMapUIManager.fileName = fileName;
		// 下載檔案不使用Call api 方式,因為斷點續傳需要,改變連結
		DownloadMapUIManager.urlPath = urlPath;

		zipCompeleteDailog = new UtilDialog(mContext) {
			@Override
			public void click_btn_1() {
				Intent intent = new Intent();
				intent.putExtra("FINISH", 1);
				mActivity.setResult(-10, intent);
				super.click_btn_1();
				mActivity.finish();

			}

			@Override
			public void click_btn_2() {

				super.click_btn_2();
			}
		};
		zCB = this;
		res = mContext.getResources();
		// 取得紀錄狀態
		sp = mContext.getSharedPreferences(mapDownload, Context.MODE_PRIVATE);
		downloadMap(fileName);

	}

	/**
	 * 更新機制
	 * 
	 * @author eden
	 * @param mapVersion
	 *            圖資版本
	 */
	public DownloadMapUIManager(Context mContext, String mapVersion) {
		// 已是最新版本
		DownloadMapUIManager.mContext = mContext;
		DownloadMapUIManager.mapVersion = mapVersion;
		res = mContext.getResources();
		showMapIsNewDialog();
	}

	/**
	 * 下載進度
	 * */
	private static class UIHandler extends Handler {
		public void handleMessage(Message msg) {
			TextView protext = valueMap.get(fileName);
			ProgressBar probar = apkMap.get(fileName);
			switch (msg.what) {
			case STARTDOWNLOAD:
				// 建立下載程序
				task = new DownloadTask(mContext, (File) msg.obj, this);
				// Set download url path
				DownloadTask.urlPath = urlPath;
				// Set download filename
				task.path = fileName;

				showMapDialog();
				break;
			case PROCESSING: // 更新
				isConnection = true;
				int progress = msg.getData().getInt("size");
				int MAXprogress = msg.getData().getInt("MAXsize");
				// Show Process
				probar.setIndeterminate(false);
				probar.setMax(MAXprogress);
				probar.setProgress(progress);

				float valueMB = progress / 1024 / 1024f;
				float maxValueMB = MAXprogress / 1024 / 1024f;
				Log.d("DEBUG", "valueMB = " + valueMB);
				Log.d("DEBUG", "maxValueMB = " + maxValueMB);
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);// 小數後兩位
				nf.setMinimumFractionDigits(2);
				protext.setText(nf.format(valueMB) + " MB / " + nf.format(maxValueMB) + " MB");
				break;
			case FINISH: // 下載完成
				sp.edit().putBoolean(mapDownloadFinish, true).commit();
				startUnZip();
				break;
			case FAILURE:// 下載失敗
				isConnection = false;
				// Toast.makeText(mContext, "失敗 ", Toast.LENGTH_LONG).show();
				// if (alertProcess != null && alertProcess.isShowing()) {
				// alertProcess.dismiss();
				// }

				/* 關閉檢查Dialog */
				this.sendMessage(this.obtainMessage(CONNECTIONERROR));
				showErrorDialog();
				break;
			case START: // 開始檢查
				TextView protexts = valueMap.get(fileName);
				protexts.setText("連線中...");
				
				probar.setIndeterminate(true);
				// time out 60 秒
				// this.sendMessageDelayed(this.obtainMessage(CONNECTIONERROR),
				// 60 * 1000);
				break;
			case SPACENOEGO: // 空間不足
				showSpaceDialog((Float) msg.obj);
				if (alertProcess != null && alertProcess.isShowing()) {
					alertProcess.dismiss();
				}

				break;
			case ENDDIALOG:// Time out Progress Dialog dismiss
				protext.setText("連線失敗...");
				break;
			case CONNECTIONERROR:
				if (!isConnection) {
					protext.setText("連線失敗...");
					probar.setIndeterminate(false);
					reset_btn.setVisibility(View.VISIBLE);
					install_btn.setEnabled(true);
					if (task != null) {
						task.exit();
						task = null;
					}
				}
				break;
			}
		}

	}

	/**
	 * 解壓縮進度
	 * */
	private static Handler unzipHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 進度更新
                    UnZipBean uzb = (UnZipBean) msg.obj;
                    View unZipView = alertMapUnZip.getWindow().getDecorView();
                    TextView unzip_per = (TextView) unZipView.findViewById(R.id.unzip_per);

                    TextView unzip_filesize = (TextView) unZipView.findViewById(R.id.unzip_filesize);

                    unzip_filesize.setText(uzb.fileSize + " / " + uzb.totalFileSize);
                    ProgressBar unzip_process = (ProgressBar) unZipView.findViewById(R.id.unzip_process);
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);// 小數後兩位
                    nf.setMinimumFractionDigits(2);
                    unzip_per.setText(String.valueOf(nf.format((float) uzb.finishSize / (float) uzb.maxSize * 100)) + " %");
                    unzip_process.setMax((int) uzb.maxSize);
                    unzip_process.setProgress((int) uzb.finishSize);
                    break;

                case 1:// 解壓縮完成
                    if (alertMapUnZip != null && alertMapUnZip.isShowing())
                        alertMapUnZip.dismiss();
                    install_btn.setEnabled(true);
                    // sp.edit().putBoolean(mapUnzipFinish, true).commit();
                    sp.edit().putBoolean(mapDownloadFinish, false).commit();
                    sp.edit().putBoolean(mapUnzipFinish, false).commit();

                    zipCompeleteDailog = new UtilDialog(mContext) {
                      @Override
                      public void click_btn_1() {
                          super.click_btn_1();
                          AppController.getInstance().restartAppImmediately();
                      }
                    };
                    zipCompeleteDailog.showDialog_route_plan_choice("圖資", "下載完成！請重新開始程式", "確定", null);
                    break;
            }
		}
	};

	/**
	 * 開始解壓縮
	 */
	private static void startUnZip() {
		final AntZip zip = new AntZip();
		zip.setZipCallBack(zCB);
		showMapUnZipDialog();
		ExecutorService es = Executors.newFixedThreadPool(2);

		es.execute(new Runnable() {

			@Override
			public void run() {
				zip.unZip(Macro.MAP_DOWNLOAD_FOLDER + fileName,
						Macro.UNZIP_FOLDER);
			}
		});
	}


	private static float getWidth(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.widthPixels;
	}

	private static float getHeight(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * Show下載進度 Dialog
	 */
	private static void showProcessDialog() {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);
		// set window params

		

		LayoutInflater lif = LayoutInflater.from(mContext);
		View updateView = lif.inflate(R.layout.updateprocess_dialog, null);
		builder.setContentView(updateView);
		final LinearLayout content_ll = (LinearLayout) updateView
				.findViewById(R.id.content_ll);
		TextView updateProc_title = (TextView) updateView
				.findViewById(R.id.updateProc_title);
		updateProc_title.setText(R.string.show_map_dialog_title);

		install_btn = (Button) updateView.findViewById(R.id.install_btn);
		reset_btn = (Button) updateView.findViewById(R.id.reset_btn);
		install_btn.setEnabled(false);
		install_btn.setText("完成");
		TextView header_txt = (TextView) updateView
				.findViewById(R.id.header_txt);

		header_txt.setVisibility(View.GONE);
		TextView content_txt = (TextView) updateView
				.findViewById(R.id.content_txt);
		StringBuilder sb = new StringBuilder();
		sb.append(res.getString(R.string.map_version_title));
		sb.append(mapVersion);
		content_txt.setText(sb.toString());

		// Item cell
		View updateprocess_item = lif
				.inflate(R.layout.updateprocess_item, null);
		TextView name = (TextView) updateprocess_item
				.findViewById(R.id.updateprocess_name);
		TextView updateprocess_value = (TextView) updateprocess_item
				.findViewById(R.id.updateprocess_value);
		updateprocess_value.setVisibility(View.VISIBLE);
		ProgressBar probar = (ProgressBar) updateprocess_item
				.findViewById(R.id.updateprocess_probar);
		name.setText(" " + fileName);
		content_ll.addView(updateprocess_item);
		apkMap.put(fileName, probar);
		valueMap.put(fileName, updateprocess_value);

		content_ll.setVisibility(View.VISIBLE);
		Button download_list = (Button) updateView
				.findViewById(R.id.download_list);
		download_list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (content_ll.isShown())
					content_ll.setVisibility(View.INVISIBLE);
				else
					content_ll.setVisibility(View.VISIBLE);
			}
		});
		install_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertProcess != null)
					alertProcess.dismiss();
			}
		});
		reset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (task != null) {
					ExecutorService cachedThreadPool = Executors
							.newCachedThreadPool();

					cachedThreadPool.execute(task);
				} else {
					task = new DownloadTask(mContext, new File(
							Macro.MAP_DOWNLOAD_FOLDER), handler);
					// Set download url path
					DownloadTask.urlPath = urlPath;
					// Set download filename
					task.path = fileName;

					ExecutorService cachedThreadPool = Executors
							.newCachedThreadPool();

					cachedThreadPool.execute(task);
				}
				reset_btn.setVisibility(View.GONE);
				install_btn.setEnabled(false);
			}
		});

		alertProcess = builder;
		alertProcess.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertProcess.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertProcess.show();
		setDialogScale(alertProcess);
	}

	private static void setDialogScale(Dialog dialog) {
		Window window = dialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();

		// set width, height by density and gravity

        params.width = (int) ((getWidth(mContext) * scale));
        params.height = (int) ((getHeight(mContext) * scale));
        params.gravity = Gravity.CENTER;

		window.setAttributes(params);
	}

	/**
	 * 顯示圖資更新提醒
	 */
	private static void showMapDialog() {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);

		View updateView = LayoutInflater.from(mContext).inflate(
				R.layout.update_dialog, null);
		builder.setContentView(updateView);
		TextView title = (TextView) updateView.findViewById(R.id.title);

		TextView sub_title = (TextView) updateView.findViewById(R.id.sub_title);

		Button update_btn = (Button) updateView.findViewById(R.id.update_btn);
		Button cancel_btn = (Button) updateView.findViewById(R.id.cancel_btn);

		Button download_button = (Button) updateView
				.findViewById(R.id.download_button);
		TextView content_txt = (TextView) updateView
				.findViewById(R.id.content_txt);
		title.setText(R.string.show_map_dialog_title);
		download_button.setVisibility(View.INVISIBLE);
		sub_title.setText(R.string.show_map_download_dialog_contant);
		StringBuilder sb = new StringBuilder();

		if (!mapVersion.equals("")) {
			sb.append("\n 圖資版本:");
			sb.append(mapVersion);
		}
		content_txt.setText(sb.toString());
		update_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (task != null) {
					ExecutorService cachedThreadPool = Executors
							.newCachedThreadPool();

					cachedThreadPool.execute(task);
				}
				showProcessDialog();
				if (alertMap != null)
					alertMap.dismiss();
			}
		});
		cancel_btn.setText("確定");
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertMap != null)
					alertMap.dismiss();
			}
		});
		alertMap = builder;
		alertMap.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertMap.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertMap.show();
		setDialogScale(alertMap);
	}

	/**
	 * 顯示錯誤提醒
	 */
	private static void showErrorDialog() {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);

		View updateView = LayoutInflater.from(mContext).inflate(
				R.layout.update_dialog, null);
		builder.setContentView(updateView);
		TextView title = (TextView) updateView.findViewById(R.id.title);

		TextView sub_title = (TextView) updateView.findViewById(R.id.sub_title);

		Button update_btn = (Button) updateView.findViewById(R.id.update_btn);
		Button cancel_btn = (Button) updateView.findViewById(R.id.cancel_btn);

		Button download_button = (Button) updateView
				.findViewById(R.id.download_button);
		title.setText(R.string.show_map_dialog_title);
		download_button.setVisibility(View.INVISIBLE);

		// tvUpdateCheck.setText();
		// tvUpdateCheckText.setText();
		// tvUpdateCheckText.setTextColor(Color.WHITE);
		sub_title.setText("下載失敗!!請檢查網路是否連線,確定連線是否穩定,在嘗試下載");
		update_btn.setVisibility(View.GONE);
		cancel_btn.setText("關閉");
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertMap != null)
					alertMap.dismiss();
			}
		});
		alertMap = builder;
		alertMap.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertMap.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertMap.show();
		setDialogScale(alertMap);
	}

	/**
	 * 顯示圖資已是最新版本提醒
	 */
	private static void showMapIsNewDialog() {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);

		View updateView = LayoutInflater.from(mContext).inflate(
				R.layout.update_dialog, null);
		builder.setContentView(updateView);
		TextView title = (TextView) updateView.findViewById(R.id.title);

		TextView sub_title = (TextView) updateView.findViewById(R.id.sub_title);

		Button update_btn = (Button) updateView.findViewById(R.id.update_btn);
		Button cancel_btn = (Button) updateView.findViewById(R.id.cancel_btn);

		Button download_button = (Button) updateView
				.findViewById(R.id.download_button);
		title.setText(R.string.show_map_dialog_title);
		download_button.setVisibility(View.INVISIBLE);
		sub_title.setText(R.string.show_map_isnew_dialog_contant);
		update_btn.setVisibility(View.GONE);
		cancel_btn.setText("確定");
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertMap != null)
					alertMap.dismiss();
			}
		});
		alertMap = builder;
		alertMap.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertMap.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertMap.show();
		setDialogScale(alertMap);
	}

	/**
	 * 顯示圖資解壓縮進度
	 */
	private static void showMapUnZipDialog() {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);

		View unzipView = LayoutInflater.from(mContext).inflate(
				R.layout.unzip_dialog, null);
		builder.setContentView(unzipView);
		builder.setTitle("解壓縮中...");

		alertMapUnZip = builder;
		alertMapUnZip.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertMapUnZip.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertMapUnZip.show();
	}

	/**
	 * 顯示下載空間不足提醒
	 */
	private static void showSpaceDialog(float remainingSpace) {
		Dialog builder = new Dialog(mContext, R.style.selectorDialog2);

		View updateView = LayoutInflater.from(mContext).inflate(
				R.layout.update_dialog, null);
		builder.setContentView(updateView);
		TextView title = (TextView) updateView.findViewById(R.id.title);

		TextView sub_title = (TextView) updateView.findViewById(R.id.sub_title);

		Button update_btn = (Button) updateView.findViewById(R.id.update_btn);
		Button cancel_btn = (Button) updateView.findViewById(R.id.cancel_btn);

		Button download_button = (Button) updateView
				.findViewById(R.id.download_button);
		title.setText(R.string.show_map_dialog_title);
		download_button.setVisibility(View.INVISIBLE);

		StringBuilder sb = new StringBuilder();
		sb.append(String
				.format(mContext
						.getResources()
						.getString(
								R.string.version_update_check_msg_available_space_not_enough2),
						(remainingSpace) * 3));
		sub_title.setText(sb.toString());
		update_btn.setVisibility(View.GONE);
		cancel_btn.setText("確定");
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertMap != null)
					alertMap.dismiss();
			}
		});
		alertMap = builder;
		alertMap.setCanceledOnTouchOutside(false);
		// 鎖返回鍵
		alertMap.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		alertMap.show();
		setDialogScale(alertMap);
	}

	private void downloadMap(String filePath) {
		// download
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			new AsyncCreateFolder().execute(Macro.MAP_DOWNLOAD_FOLDER);
		} else {
			Log.e(TAG, "Sd Error");
		}
	}

	private class AsyncCreateFolder extends AsyncTask<String, Void, File> {

		@Override
		protected File doInBackground(String... params) {
			// 建立資料夾/ASUS/HOTAI/UPDATE/
			File local = new File(params[0]);
			if (!local.exists() && !local.isDirectory()) {
				// create empty directory
				if (local.mkdirs()) {
					Log.i("CreateDir", "App dir created");
				} else {
					Log.w("CreateDir", "Unable to create app dir!");
				}
			} else {
				Log.i("CreateDir", "App dir already exists");
			}
			return local;
		}

		@Override
		protected void onPostExecute(File local) {
			super.onPostExecute(local);
			// start download
			if (local != null) {
				download(local);
			}
		}

	}

	private static DownloadTask task;

	private void download(File savDir) {
		// 確認檔案是否下載完成
		if (!checkFileExist(savDir + File.separator + fileName)) {

			handler.sendMessage(handler.obtainMessage(STARTDOWNLOAD, savDir));
		} else {
			if (sp.getBoolean(mapDownloadFinish, false)) {
				showProcessDialog();
				ProgressBar probar = apkMap.get(fileName);
				probar.setMax(10);
				probar.setProgress(10);
				TextView protext = valueMap.get(fileName);
				protext.setText("下載完成");
				// 下載完成
				if (sp.getBoolean(mapUnzipFinish, false)) {
					// 判斷為解壓縮完成
					unzipHandler.sendMessage(unzipHandler.obtainMessage(1));
				} else {
					// 判斷為還未解壓縮完成
					startUnZip();
				}
			} else {
				handler.sendMessage(handler
						.obtainMessage(STARTDOWNLOAD, savDir));
			}
		}

	}

	public static boolean checkFileExist(String filePathString) {

		File f = new File(filePathString);

		if (f.exists() && !f.isDirectory()) {
			return true;
		} else {
			return false;
		}

	}

	static class UnZipBean {
		public long finishSize;
		public long maxSize;
		public int fileSize;
		public int totalFileSize;
	}

	private static UnZipBean uzb = new UnZipBean();

	@Override
	public void onZipCB(long finishSize, long maxSize, int fileSize,
			int totalFileSize) {// 解壓縮進度
		// TODO Auto-generated method stub
		uzb.finishSize = finishSize;
		uzb.maxSize = maxSize;
		uzb.fileSize = fileSize;
		uzb.totalFileSize = totalFileSize;
		unzipHandler.sendMessage(unzipHandler.obtainMessage(0, uzb));
	}

	@Override
	public void onZipSuccess(int success) {// 解壓縮成功
		// TODO Auto-generated method stub
		reName();

	}

	@Override
	public void onZipFail() {// 解壓縮失敗,可能是zip檔下載未完全
		if (alertMapUnZip != null && alertMapUnZip.isShowing()) {
			alertMapUnZip.dismiss();
		}
		if (alertProcess != null && alertProcess.isShowing()) {
			alertProcess.dismiss();
		}
		handler.sendMessage(handler.obtainMessage(STARTDOWNLOAD, new File(
				Macro.MAP_DOWNLOAD_FOLDER)));
	}

	private void reName() {
		final File tmpDir = new File(Macro.UNZIP_FOLDER);
		final File dataDir = new File(Macro.MAP_FOLDER);

		// Delete unZip folder and original folder to preper got new
		// NaviKingFolder
		final File[] file = tmpDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				String str = filename.toLowerCase();
				return str.startsWith("bikingdata")
						&& dir.getAbsolutePath().compareTo(
								tmpDir.getAbsolutePath()) == 0;
			}
		});
		if (file != null && file.length == 1) {
			// 刪除NaviKingMap Folder
			new AsyncDeleteFolder(file).execute(dataDir);
		}
	}

	class AsyncDeleteFolder extends AsyncTask<File, Void, File> {
		final File[] file;

		public AsyncDeleteFolder(File[] file) {
			this.file = file;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected File doInBackground(File... params) {
			// 刪除原本的 NaviKingMap 將解壓縮後的資料夾Rename為NaviKingMap
			File dataDir = params[0];
			ClearMapPath();
			Log.d("DEBUG", "dataDir exists : " + dataDir.exists());
			return dataDir;
		}

		@Override
		protected void onPostExecute(File local) {
			super.onPostExecute(local);
			// start download
			if (local != null) {
				file[0].renameTo(local);
				// 解壓縮成功，刪除已解壓縮的資料夾
				ClearTempDownloadPath();
				// 解壓縮成功
				unzipHandler.sendMessage(unzipHandler.obtainMessage(1));
			}
		}

	}

	/**
	 * 清除NaviKingMap 資料夾
	 * */
	public static void ClearMapPath() {
		File path = new File(Macro.MAP_FOLDER);
		if (path.exists()) {
			try {
				FileUtility.deleteDir(path);
				path.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* 清除下載暫存資料夾 */
	public static void ClearTempDownloadPath() {
		File path = new File(Macro.MAP_DOWNLOAD_FOLDER);
		if (path.exists()) {
			try {
				FileUtility.deleteDir(path);
				path.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
