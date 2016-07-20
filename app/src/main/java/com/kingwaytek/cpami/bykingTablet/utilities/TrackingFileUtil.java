package com.kingwaytek.cpami.bykingTablet.utilities;

import android.os.Environment;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 軌跡錄製檔案讀寫 Functions
 *
 * @author Vincent (2016/7/19)
 */
public class TrackingFileUtil {

    private static final String TAG = "TrackingFileUtil";

    private static final String sdPath = Environment.getExternalStorageDirectory().getPath();

    private static BufferedWriter writer;
    private static File locationTrackFile;
    private static boolean firstWrite;

    public static void createTrackFolder() {
        File trackFolder = new File(sdPath, AppController.getInstance().getString(R.string.file_path_track_folder));

        if (!trackFolder.exists())
            trackFolder.mkdirs();
    }

    public static void writeLocationTrackingFile(double lat, double lng) {
        if (locationTrackFile == null)
            locationTrackFile = new File(sdPath, AppController.getInstance().getString(R.string.file_path_track_location));

        String location;
        if (firstWrite)
            location = String.valueOf(lat) + "," + String.valueOf(lng);
        else
            location = "&" + String.valueOf(lat) + "," + String.valueOf(lng);

        try {
            if (!locationTrackFile.exists())
                locationTrackFile.createNewFile();

            if (writer == null)
                writer = new BufferedWriter(new FileWriter(locationTrackFile, true));

            writer.write(location);
            writer.flush();
            firstWrite = false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeWriter() {
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanTrackingFile() {
        try {
            if (isTrackingFileExist()) {
                PrintWriter printWriter = new PrintWriter(locationTrackFile);
                printWriter.print("");
                printWriter.close();

                firstWrite = true;
            }
            else
                locationTrackFile.createNewFile();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "LocationTrackingFile NOT FOUND!!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isTrackingFileExist() {
        if (locationTrackFile == null)
            locationTrackFile = new File(sdPath, AppController.getInstance().getString(R.string.file_path_track_location));

        return locationTrackFile.exists();
    }
}