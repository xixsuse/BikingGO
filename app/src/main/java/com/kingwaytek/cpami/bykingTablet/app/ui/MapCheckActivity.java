package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.sonav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vincent.chang on 2016/4/19.
 */
public abstract class MapCheckActivity extends Activity implements OnMapReadyCallback {

    protected abstract void onCheckAllDone();

    private static boolean isInit;
    private sonav engine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    synchronized private void userDatabaseInit() {
        String DATABASE_PATH = getString(R.string.SQLite_Usr_Database_Path);
        String DATABASE_NAME = getString(R.string.SQLite_Usr_Database_Name);

        // 輸出路徑
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        // 檢測是否已經創建
        File dir = new File(outFileName);
        if (dir.exists())
            return;

        // 檢測/創建數據庫的文件夾
        dir = new File(DATABASE_PATH);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs())
                return;
        }

        InputStream input;
        OutputStream output = null;

        // 從資源中讀取數據庫流
        input = getResources().openRawResource(R.raw.biking_data);

        try {
            output = new FileOutputStream(outFileName);

            // 拷貝到輸出流
            byte[] buffer = new byte[2048];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            // 關閉輸出&輸入流
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                    input.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkGps() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
