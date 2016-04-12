package com.kingwaytek.cpami.bykingTablet.download;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.app.Macro;

public class CheckSpaceUtility {

	public static boolean CheckSpace(int mFileSize) {
		double dSize = mFileSize / 1024;
		String strSize;
		String strUnit;
		if (dSize > 1024) {
			dSize /= 1024;
			strSize = String.format("%4.2f", dSize);
			strUnit = "MB";
		} else {
			strSize = String.format("%d", (int) dSize);
			strUnit = "KB";
		}
		Log.e("CheckSpaceUtility", "strSize=" + strSize + strUnit);
		int less = CheckAvailSpaceEnough((int) dSize);
		if (less > 0) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param mFileSize
	 * @return 回傳值 > 0：空間不足
	 */
	private static int CheckAvailSpaceEnough(int mFileSize) {
		File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		StatFs sf = FileUtility.GetStatFs(root.getPath());

		File tempFolder = new File(Macro.MAP_DOWNLOAD_FOLDER);
		long tempSize = FileUtility.GetFolderSize(tempFolder) / (1024 * 1024);
		long blockSize;
		long availCount;
		long availSpcae;

		// do something for phones running an SDK before JELLY_BEAN_MR2
		blockSize = sf.getBlockSize();
		availCount = sf.getAvailableBlocks();
		availSpcae = (availCount * blockSize / (1024 * 1024)) + tempSize;

		Log.e("CheckSpaceUtility", "CheckAvailSpaceEnough="
				+ (int) ((long) mFileSize * 3 - availSpcae));
		return (int) ((long) mFileSize * 3 - availSpcae);
	}
}
