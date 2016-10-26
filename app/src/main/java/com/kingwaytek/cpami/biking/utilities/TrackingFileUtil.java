package com.kingwaytek.cpami.biking.utilities;

import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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

            if (!location.isEmpty())
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

    public static boolean isTrackingFileEmpty() {
        return !isTrackingFileExist() || locationTrackFile.length() == 0;
    }

    public static boolean isTrackingFileContainsData() {
        return isTrackingFileExist() && locationTrackFile.length() > 0;
    }

    public static ArrayList<LatLng> readTrackingLatLng() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(locationTrackFile));

            StringBuilder sb = new StringBuilder();
            String eachLine;

            while ((eachLine = reader.readLine()) != null) {
                sb.append(eachLine);
            }
            reader.close();

            if (sb.toString().indexOf("&") == 0)
                sb.deleteCharAt(0);

            String[] latLngArray = sb.toString().split("&");
            ArrayList<LatLng> latLngList = new ArrayList<>();

            for (String latLngString : latLngArray) {
                String[] latLng = latLngString.split(",");
                latLngList.add(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])));
            }

            return latLngList;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Utility.toastShort("Tracking File is Not Exists!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeTrackFile(String jsonString) {
        File trackFile = new File(sdPath, AppController.getInstance().getString(R.string.file_path_track_saved));

        try {
            if (!trackFile.exists())
                trackFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(trackFile, false));
            writer.write(jsonString);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readTrackFile() {
        File trackFile = new File(sdPath, AppController.getInstance().getString(R.string.file_path_track_saved));

        if (trackFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(trackFile));

                StringBuilder sb = new StringBuilder();
                String eachLine;

                while ((eachLine = reader.readLine()) != null) {
                    sb.append(eachLine);
                }
                reader.close();

                return sb.toString();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Utility.toastShort("Track File is Not Exists!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}