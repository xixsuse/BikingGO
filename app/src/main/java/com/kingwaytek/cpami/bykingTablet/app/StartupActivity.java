package com.kingwaytek.cpami.bykingTablet.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.sonavtek.sonav.sonav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is the first activity for launching. Shows splash screen and do
 * initialization .
 *
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class StartupActivity extends CommunicationBaseActivity {

    private static final int START_INIT = 1;

    private static final int INIT_SUCCESS = 2;

    private static final int INIT_FAILED = 3;
    private static final String DIR_DATA = "BikingData";
    private sonav engine;

    // private AnimationDrawable anim; // splash animation

    private static Handler handler; // dealing with initialization events

    private static Thread initializer; // Thread which doing initialization

    private LocationManager manager;

    private Intent startActivityIntent;

    private ImageView splash;
    private int intScreenX;
    private int intScreenY;
    private ImageView declaration;
    private boolean isBikingDataExist = true;
    private boolean isLocationOpen;
    public static String DataVersion;
    public String pv6 = "010601,010701,070401,071008,071415,080401";
    public String pv4 = "";
    public String pv2 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14";

    private boolean isFirstIn = true;

    // private SharedPreferences prefs;
    // private static final String PREF = "BIKING";
    // private static final String PREF_DOWNLOAD_FILE_NAME =
    // "DOWNLOAD_FILE_NAME";
    // private static final String DIR_TEMP = "BikingTemp";
    // private static final String DIR_DATA = "BikingData";
    // private MTUnzipHandler mMTUnzipHandler;
    // private ProgressDialog progressDialog;
    // private static int zipCompeleteThread = 0;
    // private static int zipThreadNum = 0;
    // private UtilDialog zipCompeleteDialog;
    // private boolean zipStart = false;

    // private AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // GCM();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set views
        setContentView(R.layout.startup);

        // prefs = this.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        // zipCompeleteDialog = new UtilDialog(StartupActivity.this) {
        // @Override
        // public void click_btn_1() {
        // super.click_btn_1();
        // finish();
        // }
        // };
        // mMTUnzipHandler = new MTUnzipHandler();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        intScreenX = dm.widthPixels;
        intScreenY = dm.heightPixels;

        userDatabaseInit(this);
        Log.i("StartActivity.java", "intScreenX=" + intScreenX + "  intScreenY=" + intScreenY);

        // if(intScreenX==320&&intScreenY==569){
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_fwvga);
        // }else if(intScreenX==320&&intScreenY==533){
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_wvga);
        // }else if(intScreenX==480&&intScreenY==800){
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_wvga);
        // }else if(intScreenX==320&&intScreenY==427){
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_ldpi);
        // }else if(intScreenX==320&&intScreenY==480){
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_mdpi);
        // }else{
        // splash = (ImageView)findViewById(R.id.splash);
        // splash.setImageResource(R.drawable.welcome_page_xlarge);
        // }

        // create splash animation
        // ImageView animImg = (ImageView) findViewById(R.id.splash);
        // animImg.setBackgroundResource(R.anim.splash);
        // anim = (AnimationDrawable) animImg.getBackground();

        // enable GPS listener
        if (ApplicationGlobal.gpsListener == null) {
            ApplicationGlobal.gpsListener = new GPSListener(this, 5000, 1);
        }

        // create instance of engine
        engine = sonav.getInstance();

        startActivityIntent = StartupActivity.this.getIntent();
        Log.i("StartActivity.java", "startActivityIntent.getIntExtra(SMS,0)=" + startActivityIntent.getIntExtra("SMS", 0));
        // if(startActivityIntent.getIntExtra("SMS",0)== 1){
        // Log.i("StartActivity.java","SMS lon"+String.valueOf(startActivityIntent.getDoubleExtra("Lon",0.0)));
        // Log.i("StartActivity.java","SMS lat"+String.valueOf(startActivityIntent.getDoubleExtra("Lat",0.0)));
        // }

        if (handler == null) {
            handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what) {
                        case START_INIT:

                            initializer = new Thread() {

                                @Override
                                public void run() {
                                    initialize();

                                    while (engine.getState() != sonav.STATE_READY) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException ex) {
                                            Log.e(getClass().toString(), ex.getMessage(), ex);
                                        }
                                    }
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    handler.sendMessage(handler.obtainMessage(INIT_SUCCESS));
                                }

                            };

                            initializer.start();

                            break;
                        case INIT_SUCCESS:
                            // goTo(MapActivity.class, true);
                            // Log.i("startActivity.java",
                            // "isAnnouncementEnabled="+PreferenceActivity.isAnnouncementEnabled(StartupActivity.this));
                            // if(PreferenceActivity.isAnnouncementEnabled(StartupActivity.this).equalsIgnoreCase("false")){
						/*
						 * 320*427是tattoo320*480是hero 320*569是moto
						 * 320*533是desire
						 */
                            // if((intScreenX==320&&intScreenY==427)||(intScreenX==320&&intScreenY==480)){
                            // setContentView(R.layout.declaration_v);
                            // }else if(intScreenX==320&&intScreenY==533){
                            // setContentView(R.layout.declaration_320_533);
                            // }else{
                            // setContentView(R.layout.declaration);
                            // if(intScreenX==320&&intScreenY==569){
                            // declaration =
                            // (ImageView)findViewById(R.id.terms_of_use_image);
                            // declaration.setImageResource(R.drawable.terms_of_use_fwvga);
                            // }else if(intScreenX==320&&intScreenY==533){
                            // declaration =
                            // (ImageView)findViewById(R.id.terms_of_use_image);
                            // declaration.setImageResource(R.drawable.terms_of_use_wvga);
                            // }
                            // }
                            //
                            // Button button =
                            // (Button)findViewById(R.id.startup_terms_of_use_summit);
                            // final CheckBox checkbox =
                            // (CheckBox)findViewById(R.id.checkbox_terms_of_use_summit);
                            //
                            // button.setOnClickListener(new OnClickListener() {
                            //
                            // @Override
                            // public void onClick(View v) {
                            // engine.setpoiauto(0);
                            // engine.setpoivisible(pv6,pv4,pv2,0);
                            // engine.setpoivisible(pv6,pv4,pv2,1);
                            // engine.setpoivisible(pv6,pv4,pv2,2);
                            // engine.setpoivisible(pv6,pv4,pv2,3);
                            // engine.setpoivisible(pv6,pv4,pv2,4);
                            // engine.setpoivisible(pv6,pv4,pv2,5);
                            // /***清除marker***/
                            // engine.setflagpoint(0, -1, -1);
                            // engine.setflagpoint(1, -1, -1);
                            // engine.setflagpoint(2, -1, -1);
                            // engine.setflagpoint(3, -1, -1);
                            // engine.setflagpoint(5, -1, -1);
                            // /***************/
                            // PreferenceActivity.setAnnouncementEnabled(StartupActivity.this,checkbox.isChecked());
                            // Bundle params=getIntent().getExtras();
                            // Intent intent = new
                            // Intent(StartupActivity.this,MapActivity.class);
                            // if (params!=null){
                            // intent.putExtra("SMSToMapActivity",1);
                            // intent.putExtras(params);
                            // }
                            // startActivity(intent);
                            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            // finish();

                            // }
                            // });
                            // }else{
                            // try {
                            // new Thread() {
                            // @Override
                            // public void run() {
                            // try {
                            //
                            // }
                            // catch(Exception e){
                            // }
                            // }
                            // };
                            // Thread.sleep(1500);
                            // } catch (InterruptedException e) {
                            // // TODO Auto-generated catch block
                            // e.printStackTrace();
                            // }
                            for (int i = 0; i <= 300000; i++) {

                            }
                            engine.setpoiauto(0);
                            engine.setpoivisible(pv6, pv4, pv2, 0);
                            engine.setpoivisible(pv6, pv4, pv2, 1);
                            engine.setpoivisible(pv6, pv4, pv2, 2);
                            engine.setpoivisible(pv6, pv4, pv2, 3);
                            engine.setpoivisible(pv6, pv4, pv2, 4);
                            engine.setpoivisible(pv6, pv4, pv2, 5);
                            /*** 清除marker ***/
                            // engine.setflagpoint(0, -1, -1);
                            engine.setflagpoint(1, -1, -1);
                            engine.setflagpoint(2, -1, -1);
                            engine.setflagpoint(3, -1, -1);
                            engine.setflagpoint(5, -1, -1);
                            /***************/
                            Bundle params = getIntent().getExtras();
                            Intent intent = new Intent(StartupActivity.this, AnnounceActivity.class);
                            if (params != null) {
                                intent.putExtra("SMSToMapActivity", 1);
                                intent.putExtras(params);
                            }
                            startActivity(intent);
                            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            finish();

                            // }

                            break;
                        case INIT_FAILED:

                            AlertDialogUtil.showMessage(StartupActivity.this, getString(R.string.msg_err),
                                    R.drawable.dialog_error);
                            break;

                        default:

                            break;
                    }
                }

            };
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteBikingDataDir();

        // check GPS
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        // ApplicationGlobal.gpsListener.setEnabled(true);
        // } else {
        // if (isFirstIn) {
        // UtilDialog uit = new UtilDialog(StartupActivity.this) {
        // @Override
        // public void click_btn_1() {
        // final Intent i = new
        // Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // super.click_btn_1();
        // startActivity(i);
        // }
        // };
        // uit.showDialog_route_plan_choice("您尚未開啟系統GPS功能,請先開啟。", null, "確定",
        // null);
        // isFirstIn = false;
        // }
        // }
        // Check BikingData File
        File dir = new File("/sdcard/BikingData/");
        if (!dir.exists()) {
            isBikingDataExist = false;

            UtilDialog uit = new UtilDialog(StartupActivity.this) {
                @Override
                public void click_btn_1() {
                    final Intent i = new Intent(StartupActivity.this, MapDownloadActivity.class);
                    super.click_btn_1();
                    startActivity(i);
                    finish();

                }

                @Override
                public void click_btn_2() {
                    super.click_btn_2();
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
            };
            uit.showDialog_route_plan_choice("尚未安裝圖資，是否前往下載?", null, "確定", null);
            // Check GPS
        } else {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ApplicationGlobal.gpsListener.setEnabled(true);
                handler.sendMessage(handler.obtainMessage(START_INIT));
            } else {
//				if (isFirstIn) {
                UtilDialog uit = new UtilDialog(StartupActivity.this) {
                    @Override
                    public void click_btn_1() {
                        final Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        super.click_btn_1();
                        startActivity(i);
                    }

                    @Override
                    public void click_btn_2() {
                        super.click_btn_2();
                        handler.sendMessage(handler.obtainMessage(START_INIT));
                    }
                };
                uit.showDialog_route_plan_choice("您尚未開啟系統GPS功能,請先開啟。", null, "確定", "取消");
//					isFirstIn = false;
//				} else {
//					handler.sendMessage(handler.obtainMessage(START_INIT));
//				}
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // anim.start();
    }

    /**
     * Prevent KeyEvent.KEYCODE_MENU event to be propagated in this activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        } else {
            return false;
        }
    }

    private void initialize() {
        // clear_myloc();

        try {
            // engine.setresizefont(3.5);//xxhdpi
            engine.setIconSize(1);
            engine.setresizefont(2);// xhdpis
            engine.init(this, PreferenceActivity.getDataDirectory(this));
        } catch (Throwable t) {
            Log.e(getClass().toString(), t.getMessage(), t);
            handler.sendMessage(handler.obtainMessage(INIT_FAILED));
        }

        // cuber can not get engine version

        // DataVersion = engine.getmapver();
        DataVersion = getResources().getString(R.string.DataVersion);
        // if (Integer.parseInt(DataVersion.substring(0, 5)) == 20112) {
        // Log.i("StartActivity.java", "DataVersion.substring(5,8)=" +
        // DataVersion.substring(5, 8));
        // if ((Integer.parseInt(DataVersion.substring(5, 8)) >= 001 &&
        // Integer.parseInt(DataVersion.substring(5, 8)) <= 999)) {
        // return;
        // } else {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // }
        //
        // } else {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // }
    }

    private boolean isInit = false;

    synchronized private void userDatabaseInit(Context context) {
        String DATABASE_PATH = context.getString(R.string.SQLite_Usr_Database_Path);
        String DATABASE_NAME = context.getString(R.string.SQLite_Usr_Database_Name);
        if (isInit)
            return;
        // 輸出路徑
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        // 檢測是否已經創建
        File dir = new File(outFileName);
        if (dir.exists())
            return;
        // 檢測/創建數據庫的文件夾

        dir = new File(DATABASE_PATH);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                return;
            }
        }

        InputStream input = null;
        OutputStream output = null;
        // 從資源中讀取數據庫流
        input = context.getResources().openRawResource(R.raw.biking_data);

        try {
            output = new FileOutputStream(outFileName);

            // 拷貝到輸出流
            byte[] buffer = new byte[2048];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 關閉輸出流
            try {
                output.flush();
                output.close();
            } catch (IOException e) {
            }
            // 關閉輸入流
            try {
                input.close();
            } catch (IOException e) {
            }
        }

        isInit = true;
    }

    /**
     * 每次啟動時把myloc檔裡的內容清空
     */
    private void clear_myloc() {

        File file = new File("/sdcard/BikingData/myloc");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkIsGPSOpen() {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            ApplicationGlobal.gpsListener.setEnabled(true);
            return true;
        }
        return false;
    }

    // private void GCM() {
    // checkNotNull(SERVER_URL, "SERVER_URL");
    // checkNotNull(SENDER_ID, "SENDER_ID");
    // // Make sure the device has the proper dependencies.
    // GCMRegistrar.checkDevice(this);
    // // Make sure the manifest was properly set - comment out this line
    // // while developing the app, then uncomment it when it's ready.
    // GCMRegistrar.checkManifest(this);
    // // setContentView(R.layout.main);
    // // mDisplay = (TextView) findViewById(R.id.display);
    // registerReceiver(mHandleMessageReceiver, new
    // IntentFilter(DISPLAY_MESSAGE_ACTION));
    // final String regId = GCMRegistrar.getRegistrationId(this);
    // if (regId != "") {
    //
    // } else if (regId.equals("")) {
    // // Automatically registers application on startup.
    // // registerReceiver(mHandleMessageReceiver, new
    // // IntentFilter(DISPLAY_MESSAGE_ACTION));
    // GCMRegistrar.register(this, SENDER_ID);
    // }// Device is already registered on GCM, check server.
    // if (GCMRegistrar.isRegisteredOnServer(this)) {
    // // Skips registration.
    // // mDisplay.append(getString(R.string.already_registered) +
    // // "\n");
    // } else {
    // // Try to register again, but not in the UI thread.
    // // It's also necessary to cancel the thread onDestroy(),
    // // hence the use of AsyncTask instead of a raw thread.
    // final Context context = this;
    // mRegisterTask = new AsyncTask<Void, Void, Void>() {
    //
    // @Override
    // protected Void doInBackground(Void... params) {
    // boolean registered = ServerUtilities.register(context, regId);
    // Log.i("DEBUG", "" + registered);
    //
    // // At this point all attempts to register with the app
    // // server failed, so we need to unregister the device
    // // from GCM - the app will try to register again when
    // // it is restarted. Note that GCM will send an
    // // unregistered callback upon completion, but
    // // GCMIntentService.onUnregistered() will ignore it.
    // if (!registered) {
    // GCMRegistrar.unregister(context);
    // }
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute(Void result) {
    // mRegisterTask = null;
    // }
    //
    // };
    // mRegisterTask.execute(null, null, null);
    // }
    // }
    //
    // private void checkNotNull(Object reference, String name) {
    // if (reference == null) {
    // throw new NullPointerException(getString(R.string.error_config, name));
    // }
    // }
    //
    // private final BroadcastReceiver mHandleMessageReceiver = new
    // BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
    // // mDisplay.append(newMessage + "\n");
    // }
    // };
    //
    // @Override
    // protected void onDestroy() {
    //
    // if (mRegisterTask != null) {
    // mRegisterTask.cancel(true);
    // }
    //
    // unregisterReceiver(mHandleMessageReceiver);
    // GCMRegistrar.onDestroy(getApplicationContext());
    // finish();
    // super.onDestroy();
    // }
    private void deleteDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                System.out.println("file delete:" + file.getName());
                file.delete();
            }
        }
        System.out.println("dir delete:" + dir.getName());
        dir.delete();
    }

    //
    private boolean deleteBikingDataDir() {

        File dir = Environment.getExternalStoragePublicDirectory(DIR_DATA + "_1");

        if (!dir.exists()) {
            return dir.exists();
        }

        deleteDirectory(dir);
        return dir.exists();
    }
    //
    // private void moveFileToTargetDir(String fileName) {
    //
    // final File destinationDir = Environment.getExternalStorageDirectory();
    //
    // // if (!destinationDir.exists()) {
    // // destinationDir.mkdir();
    // // }
    //
    // File tempDir = Environment.getExternalStoragePublicDirectory(DIR_TEMP);
    //
    // final File source = new File(tempDir, fileName);
    //
    // if (source.exists()) {
    // progressDialog = new ProgressDialog(this);
    // progressDialog.setCancelable(false);
    // progressDialog.show();
    // progressDialog.setTitle("解壓縮中");
    // progressDialog.setMessage("請稍等...");
    // // deleteBikingDataDir();
    // // new UnZipAsyncTask(source.getAbsolutePath(),
    // // destinationDir.toString()).execute();
    // new startMTunzip(source.getAbsolutePath(), destinationDir.toString(),
    // mMTUnzipHandler).execute();
    // }
    // }
    //
    // public class startMTunzip extends AsyncTask<Void, Void, Void> {
    //
    // String filePath;
    // String destination;
    // Handler MTUnzipHandler;
    //
    // public startMTunzip(String filePath, String destination, Handler
    // MTUnzipHandler) {
    // this.filePath = filePath;
    // this.destination = destination;
    // this.MTUnzipHandler = MTUnzipHandler;
    //
    // }
    //
    // @Override
    // protected Void doInBackground(Void... params) {
    //
    // try {
    // MTUnzip.unzip(filePath, destination, MTUnzipHandler);
    // } catch (ZipException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    //
    // }
    //
    // @SuppressLint("HandlerLeak")
    // private class MTUnzipHandler extends Handler {
    // @Override
    // public void handleMessage(Message msg) {
    // super.handleMessage(msg);
    //
    // switch (msg.what) {
    // case 0:// 失敗
    // runOnUiThread(unZipError);
    // break;
    // case 1:// 完成
    // break;
    // case 2:// 完成檔案大小
    // zipCompeleteThread++;
    // int percent = (int) ((float) zipCompeleteThread * 100 / (float)
    // zipThreadNum);
    // if (zipCompeleteThread <= zipThreadNum) {
    // // dialog_progressbar.setProgress(percent);
    // progressDialog.setMessage("已解壓：" + percent + "%\n" + zipCompeleteThread +
    // "/" + zipThreadNum);
    // if (zipCompeleteThread == zipThreadNum) {
    // progressDialog.dismiss();
    // StartupActivity.this.deleteTempDir();
    // zipCompeleteDialog.showDialog_route_plan_choice("圖資", "解壓縮完成！請重新開始程式",
    // "確定", null);
    // }
    // }
    // break;
    // case 3:// 檔案總大小
    // zipThreadNum = Integer.valueOf(msg.obj.toString());
    // break;
    // }
    // }
    // }

    // private Runnable unZipError = new Runnable() {
    //
    // @Override
    // public void run() {
    //
    // progressDialog.dismiss();
    // UtilDialog uit = new UtilDialog(StartupActivity.this) {
    // @Override
    // public void click_btn_1() {
    // StartupActivity.this.deleteTempDir();
    // StartupActivity.this.clearPrefs();
    // super.click_btn_1();
    // }
    //
    // @Override
    // public void click_btn_2() {
    // moveFileToTargetDir(getFileName());
    // super.click_btn_2();
    // }
    // };
    // uit.showDialog_route_plan_choice("解壓縮檔案發生異常", "請重新下載，或是重新解壓縮", "重新下載",
    // "重新解壓縮");
    // }
    // };
    //
    // private boolean deleteTempDir() {
    // File dir = Environment.getExternalStoragePublicDirectory(DIR_TEMP);
    //
    // if (dir.exists() && dir.isDirectory()) {
    //
    // for (File file : dir.listFiles()) {
    // file.delete();
    // }
    //
    // dir.delete();
    // }
    //
    // return dir.exists();
    // }

    // private void clearPrefs() {
    // prefs.edit().clear().commit();
    // }
    //
    // private String getFileName() {
    // return prefs.getString(PREF_DOWNLOAD_FILE_NAME, null);
    // }
}