package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.download.DownloadMapUIManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MapDownloadActivity extends Activity implements OnItemClickListener{

	/* DIRs */
	public static final String DIR_TEMP = "BikingTemp";
	public static final String DIR_DATA = "BikingData";
	// private static final String DIR_DESTINATION = "BikingData";

	/* Single selection list */
	private static final String[] LIST_AREA = new String[] { "全區下載", "北區", "中區", "離島" };
	private static final String[] LIST_AREA_CONTEXT = new String[] { "",
			"臺北市、新北市、宜蘭縣、基隆市、桃園縣、新竹縣、新竹市、苗栗縣", "大臺中、彰化縣、南投縣、雲林縣", "金門" };

	// TODO 圖資下載
	private static final String[] LIST_DOWNLOAD_URL = new String[] {
			"http://biking.cpami.gov.tw/file/data/allArea.zip",
//			"http://biking.cpami.gov.tw/file/data/north.zip",
			"http://demo.queenwaytek.com/EasyWallet/test/north.zip",
			"http://biking.cpami.gov.tw/file/data/center.zip",
			"http://biking.cpami.gov.tw/file/data/island.zip" };

	private static Double[] LIST_DOWNLOAD_SIZE = new Double[] {
			(double) 58 * 1024 * 1024, (double) 60 * 1024 * 1024,
			(double) 82 * 1024 * 1024, (double) 60 * 1024 * 1024 };

	private HashMap<String, Double> MAP_FILE_LIST;

	private static int selectPosition;

	private ListView listView;


	private int area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_map_download);

		try {
			this.area = MapDownloadActivity.getMapArea();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.initData();

		this.initViews();

	}

	private void initData() {
		MAP_FILE_LIST = new HashMap<>();

		for (int i = 0; i < LIST_DOWNLOAD_URL.length; i++) {
			MAP_FILE_LIST.put(LIST_DOWNLOAD_URL[i], LIST_DOWNLOAD_SIZE[i]);
		}
	}

	private void initViews() {
		listView = (ListView) this.findViewById(R.id.listView1);

		MapDownloadAdapter adapter = new MapDownloadAdapter(this, LIST_AREA, LIST_AREA_CONTEXT, area);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);

		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);

	}

	public static int getMapArea() throws IOException {
		File file = new File("/sdcard/BikingData/area");
		if (!file.exists()) {
			return -1;
		}
		FileInputStream fis = new FileInputStream(file);
		BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));
		int temp;
		temp = Integer.valueOf(bfr.readLine());
		return temp;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		selectPosition = arg2;

		for (int i = 0; i < LIST_AREA.length; i++) {
			View view = listView.getChildAt(i);

			if (i == arg2) {
				view.findViewById(R.id.cell_imageview).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.cell_imageview).setVisibility(View.INVISIBLE);
			}
		}
	}

	/* Button event */
	public void titleViewConfirmButtonPressed(View view) {

		this.prepareDownload();
	}
	/* Prepare to download */
	private void prepareDownload() {
		this.createTempDir();

		String url = LIST_DOWNLOAD_URL[selectPosition];

		if (this.checkFreeSpace(url)) {

			String[] dir = url.split("/");
			String fileName = dir[dir.length - 1];
			String urlPath = url.replace(fileName, "");

			new DownloadMapUIManager(MapDownloadActivity.this,MapDownloadActivity.this,
					GetNaviKingVersion.GetMapDBVersion(),
					urlPath,
					fileName);

		} else {
			this.showAlert("警告!", "SD 卡空間不足！");
		}
	}

	/* Check Free Space in SD card */
	private boolean checkFreeSpace(String url) {

		String sdcard = Environment.getExternalStorageDirectory().toString();

		StatFs stat = new StatFs(sdcard);

		/* return value is in bytes */
		double free_memory = (double) stat.getAvailableBlocks()
				* (double) stat.getBlockSize();

		double fileSize = MAP_FILE_LIST.get(url);

		if (free_memory < fileSize) {

			return false;
		}

		return true;
	}

	/* Create template folder */
	private boolean createTempDir() {
		File dir = Environment.getExternalStoragePublicDirectory(DIR_TEMP);

		if (!dir.exists()) {
			dir.mkdir();
		}

		return dir.exists();
	}


	/* Show alert */
	private void showAlert(String title, String message) {

		UtilDialog uit = new UtilDialog(MapDownloadActivity.this);
		uit.showDialog_route_plan_choice(title, message, "確定", null);
	}
}
