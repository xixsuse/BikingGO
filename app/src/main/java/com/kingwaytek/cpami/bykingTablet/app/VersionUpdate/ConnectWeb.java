package com.kingwaytek.cpami.bykingTablet.app.VersionUpdate;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;

public class ConnectWeb extends Activity {
	/* DIRs */
	private static final String DIR_TEMP = "BikingAPK";

	/* Download URL */
	private String downloadUrl;

	/* Download fileName */
	private String downloadFileName;

	private DownloadManager downloadManager;

	private ProgressDialog progressDialog;

	private Handler handler;

	/* Preferences */
	private SharedPreferences prefs;
	private static final String PREF = "BIKING";
	private static final String PREF_DOWNLOAD_ID = "DOWNLOAD_ID";
	private static final String PREF_DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_web);

		Bundle extra = this.getIntent().getExtras();

		if (extra != null) {

			downloadFileName = extra.getString("filename");

			// downloadUrl =
			// "http://dl.dropboxusercontent.com/u/90416799/biking_tablet_2014_3_5_r2.apk";
			
			downloadUrl = "http://biking.cpami.gov.tw/Service/files/"
					+ downloadFileName;

			this.initialDownloadManager();

			this.prepareDownload();

		} else {

			this.showWarningDialog("下載發生異常！", "", "請重新下載");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		/* Register Download manager call back listener */
		IntentFilter filter_complete = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);

		this.registerReceiver(completeReceiver1, filter_complete);

		// Check download
		long downloadID = this.getDownloadID();

		if (downloadID != 0) {
			// Back to APP and the download progress is still exist
			String fileName = this.getFileName();

			Query query = new Query();
			query.setFilterById(downloadID);

			Cursor cursor = downloadManager.query(query);

			if (cursor != null && cursor.moveToFirst()) {

				int columnIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS);

				this.showProgressDialog("apk下載中， \n請勿強制關閉程式。", "已完成 0 %");

				handler.post(querySateRunnable);

				if (DownloadManager.STATUS_SUCCESSFUL == cursor
						.getInt(columnIndex)) {

					this.handleDownloadComplete(fileName);
				}

			} else {
				// Download progress has been canceled
				File tempDir = Environment
						.getExternalStoragePublicDirectory(DIR_TEMP);

				if (tempDir.exists()) {

					this.deleteTempDir();
				}

				progressDialog.dismiss();

				this.showWarningDialog("警告!" + fileName + " 下載未完成，請重新下載", "",
						"確定");

				this.clearPrefs();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		handler.removeCallbacks(querySateRunnable);

		this.unregisterReceiver(completeReceiver1);
	}

	/* Listening back key event */
	@Override
	public void onBackPressed() {

		long downloadID = this.getDownloadID();

		if (downloadID != 0) {
			return;
		}

		this.finish();

		return;
	}

	/** Initial DownloadManager */
	private void initialDownloadManager() {

		downloadManager = (DownloadManager) this
				.getSystemService(Context.DOWNLOAD_SERVICE);

		prefs = this.getSharedPreferences(PREF, Context.MODE_PRIVATE);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);

		handler = new Handler();
	}

	/** Prepare download */
	private void prepareDownload() {

		boolean hasConnection = this.checkNetworkState();

		if (!hasConnection) {
			this.showWarningDialog("目前沒有網路連線", "", "確定");

			return;
		}

		// Create apk folder
		this.createTempDir();

		// check free space of SDCard
		if (this.checkFreeSpace()) {

			DownloadManager.Request request = new DownloadManager.Request(
					Uri.parse(downloadUrl));

			request.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_WIFI
							| DownloadManager.Request.NETWORK_MOBILE)
					.setAllowedOverRoaming(true)
					.setTitle("單車ing APK 下載")
					.setDescription(downloadFileName)
					.setDestinationInExternalPublicDir(DIR_TEMP,
							downloadFileName);

			long downloadID = downloadManager.enqueue(request);

			this.setDownloadID(downloadID);

			this.setFileName(downloadFileName);

			this.startDownload();

		} else {

			this.showWarningDialog("警告! SD 卡空間不足！", "", "確定");
		}
	}

	/* Check network state */
	private boolean checkNetworkState() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}

	/* Create template folder */
	private boolean createTempDir() {
		File dir = Environment.getExternalStoragePublicDirectory(DIR_TEMP);

		if (!dir.exists()) {
			dir.mkdir();
		}

		return dir.exists();
	}

	/* Delete template folder */
	private boolean deleteTempDir() {
		File dir = Environment.getExternalStoragePublicDirectory(DIR_TEMP);

		if (dir.exists() && dir.isDirectory()) {

			for (File file : dir.listFiles()) {
				file.delete();
			}

			dir.delete();
		}

		return dir.exists();
	}

	/* Check Free Space in SD card */
	private boolean checkFreeSpace() {

		String sdcard = Environment.getExternalStorageDirectory().toString();

		StatFs stat = new StatFs(sdcard);

		/* return value is in bytes */
		@SuppressWarnings("deprecation")
		double free_memory = (double) stat.getAvailableBlocks()
				* (double) stat.getBlockSize();

		if (free_memory < 7 * 1024 * 1024) {

			return false;
		}

		return true;
	}

	/** Start Download */
	private void startDownload() {

		long downloadID = this.getDownloadID();

		if (downloadID != 0) {

			this.showProgressDialog("apk下載中，\n請勿強制關閉程式。", "已完成 0 %");

			handler.post(querySateRunnable);
		}
	}

	/** Runnable */
	private Runnable querySateRunnable = new Runnable() {

		@Override
		public void run() {

			ConnectWeb.this.updateProgressStatus();

			handler.postDelayed(querySateRunnable, 1000);
		}
	};

	/* Update progress status */
	private void updateProgressStatus() {
		long downloadID = this.getDownloadID();

		Cursor cursor = downloadManager.query(new DownloadManager.Query()
				.setFilterById(downloadID));

		if (cursor == null) {

			this.handleIncorrectDownload();

		} else {

			if (!cursor.moveToFirst()) {

				this.handleIncorrectDownload();

				cursor.close();

				return;
			}

			long current = cursor
					.getLong(cursor
							.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

			long total = cursor.getLong(cursor
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

			progressDialog.setMessage("已完成 " + (int) (100 * current / total)
					+ " %");

			cursor.close();
		}
	}

	/* Handle incorrect download */
	private void handleIncorrectDownload() {
		progressDialog.dismiss();

		this.showWarningDialog("下載發生異常！", "", "請重新下載");

		handler.removeCallbacks(querySateRunnable);

		this.clearPrefs();
	}

	/* Handle download complete */
	private void handleDownloadComplete(String fileName) {
		handler.removeCallbacks(querySateRunnable);

		progressDialog.dismiss();

		this.openAPK();

		this.clearPrefs();

		// this.deleteTempDir();

		this.finish();
	}

	/* Open apk */
	private void openAPK() {
		File root = new File(Environment.getExternalStorageDirectory(),
				DIR_TEMP);

		File file = new File(root, this.getFileName());

		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		this.startActivity(intent);
	}

	/* Progress Dialog */
	private void showProgressDialog(String title, String message) {
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	/** Preferences */
	private void clearPrefs() {
		prefs.edit().clear().commit();
	}

	private void setDownloadID(long id) {
		prefs.edit().putLong(PREF_DOWNLOAD_ID, id).commit();
	}

	private long getDownloadID() {
		return prefs.getLong(PREF_DOWNLOAD_ID, 0);
	}

	private void setFileName(String fileName) {
		prefs.edit().putString(PREF_DOWNLOAD_FILE_NAME, fileName).commit();
	}

	private String getFileName() {
		return prefs.getString(PREF_DOWNLOAD_FILE_NAME, null);
	}

	/** Warning dialog */
	private void showWarningDialog(String message, String positiveBtnText,
			String negativeBtnText) {

		new AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton(positiveBtnText,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setNegativeButton(negativeBtnText,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								ConnectWeb.this.finish();

							}
						}).show();
	}

	/** Receiver for download complete */
	BroadcastReceiver completeReceiver1 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, 0);

				long downloadID = ConnectWeb.this.getDownloadID();

				Log.i("", String.valueOf(downloadID));

				Query query = new Query();
				query.setFilterById(downloadId);

				Cursor cursor = downloadManager.query(query);

				if (cursor.moveToFirst()) {

					int columnIndex = cursor
							.getColumnIndex(DownloadManager.COLUMN_STATUS);

					Log.i("", String.valueOf(cursor.getInt(columnIndex)));

					if (DownloadManager.STATUS_SUCCESSFUL == cursor
							.getInt(columnIndex)) {

						ConnectWeb.this
								.handleDownloadComplete(downloadFileName);

					} else {
						ConnectWeb.this.handleIncorrectDownload();
					}
				}
			}
		}
	};
}
