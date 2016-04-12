package com.kingwaytek.cpami.bykingTablet.app;

import android.os.Environment;

public class Macro {
	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath();
	// Download Path
	public static final String DOWNLOAD_LOCALPATH = "/"+MapDownloadActivity.DIR_TEMP+"/";
	// Download Path
	public static final String DOWNLOAD_FOLDER = SDCARD_PATH
			+ DOWNLOAD_LOCALPATH;
	// MAP Download Path
	public static final String MAP_DOWNLOAD_LOCALPATH = "/"+MapDownloadActivity.DIR_TEMP+"/";
	// MAP Download Path
	public static final String MAP_DOWNLOAD_FOLDER = SDCARD_PATH
			+ MAP_DOWNLOAD_LOCALPATH;
	// UNZIP Path
	public static final String UNZIP_LOCALPATH = "/"+MapDownloadActivity.DIR_TEMP+"/"+"/UNZIP/";
	// UNZIP Path
	public static final String UNZIP_FOLDER = SDCARD_PATH
			+ UNZIP_LOCALPATH;
	// MAP LOCALPATH Path
	public static final String MAP_LOCALPATH = "/"+MapDownloadActivity.DIR_DATA+"/";
	// MAP FOLDER Path
	public static final String MAP_FOLDER = SDCARD_PATH
			+ MAP_LOCALPATH;
}
