package com.kingwaytek.cpami.bykingTablet.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.kingwaytek.jni.GPSTagNtvEngine;
import com.kingwaytek.jni.LocationInfo;

public class UICamera extends Activity implements SurfaceHolder.Callback {

	private static final int BACK_FROM_CAMERA = 99999;
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private Button mBtnCapture;
	private Button mBtnCancel;
	private boolean bIsPreview = false;
	private Intent intent;
	private boolean bIsTakingPIC = false;
	private boolean bFocused = false;
	private boolean bFocusing = false;
	private boolean bForcesave = false;
	private boolean bEnoughSpace = false;

	private long free_space = 0;
	public static final String PREFS_NAME = "NaviKing";
	public static final String PREF_PHOTO_ID = "photoid";

	private static int RES_WIDTH = 320; // Resolution width

	private static int RES_HEIGHT = 480; // Resolution height

	// The file path that we are using to store the taken picture
	private static final String CAPTURE_FILE_PATH = "/sdcard/DCIM/";

	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("UICamera.java", "Enter onCreate");
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		setContentView(R.layout.camera);
		// verifyAlbumDirectoryExists();
		init();

		// Set the volume control to media stream, not the default ring stream
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	static public boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Initializes the camera view
	 */
	protected void init() {
		bFocused = false;
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			free_space = availableBlocks * blockSize;
			if (free_space > (1024 * 1024 * 10))// reserve 10M Space for
												// matching process
			{
				bEnoughSpace = true;
			}
		}

		mBtnCancel = (Button) this.findViewById(R.id.camera_btn_cancel);
		mBtnCapture = (Button) this.findViewById(R.id.camera_btn_capture);
		mSurfaceView = (SurfaceView) this.findViewById(R.id.camera_view);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(UICamera.this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mBtnCancel.setOnClickListener(onBtnCancel);
		mBtnCapture.setOnClickListener(onBtnCapture);
		if (!bEnoughSpace) {
			mBtnCapture.setTextColor(android.graphics.Color.RED);
			mBtnCapture.setText("?硃?????????");
			mBtnCapture.setClickable(false);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		resetCamera();
	}

	/**
	 * Handles capture button event
	 */
	private OnClickListener onBtnCapture = new OnClickListener() {
		public void onClick(View v) {
			if (!bIsTakingPIC) {
				if (mCamera != null) {
					bIsTakingPIC = true;
					mCamera.autoFocus(cb);
				}
			}
		}
	};

	Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera c) {
			if (mCamera != null) {
				mCamera.takePicture(null, null, jpegCallback);
			}
		}
	};

	Camera.AutoFocusCallback cb_key = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera c) {

			if (bForcesave) {
				if (mCamera != null) {
					mCamera.takePicture(null, null, jpegCallback);
				}
				bForcesave = false;
			}
			bFocused = success;
			bFocusing = false;
		}
	};

	/**
	 * Handles cancel button event
	 */
	private OnClickListener onBtnCancel = new OnClickListener() {
		public void onClick(View v) {

			// Return to the previous activity
			intent.setData(null);
			UICamera.this.setResult(RESULT_OK, intent);
			UICamera.this.finish();
		}
	};

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// if(((bFocused) || (bFocusing)) && (!bIsTakingPIC))
		// {
		// mCamera.cancelAutoFocus();
		// }
		bFocused = false;
		bFocusing = false;
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			if (mCamera != null) {
				if ((!bIsTakingPIC) && (bEnoughSpace)) {
					bIsTakingPIC = true;
					if (bFocused == true) {
						mCamera.takePicture(null, null, jpegCallback);
					} else if (bFocusing) {
						bForcesave = true;
					} else {
						// mCamera.cancelAutoFocus();
						mCamera.autoFocus(cb);
					}
				}
			}
			// capture();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS) {
			if ((!bFocused) && (!bFocusing) && (!bIsTakingPIC)) {
				bFocusing = true;
				mCamera.autoFocus(cb_key);
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Return to the previous activity
			intent.setData(null);
			UICamera.this.setResult(RESULT_OK, intent);
			UICamera.this.finish();

			return true;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			// Return to the previous activity
			intent.setData(null);
			UICamera.this.setResult(RESULT_OK, intent);
			UICamera.this.finish();

			// SceneManager.setUIView(R.layout.info_main);

			return true;
		}

		return false;
	}

	/**
	 * Notifies the camera to take picture
	 */
	private void capture() {
		if (mCamera != null) {
			if (!bIsTakingPIC) {
				if (bFocused == true) {
					mCamera.takePicture(null, null, jpegCallback);
				} else {
					// mCamera.cancelAutoFocus();
					mCamera.autoFocus(cb);
				}
			}
		}
	}

	/**
	 * Initialize the camera and sets picture size & quality
	 */
	private void initCamera() {
		if (!bIsPreview) {
			try {
				mCamera = Camera.open();

			} catch (Exception e) {
				bIsPreview = true;
				e.printStackTrace();
			}
		}

		if (mCamera != null && !bIsPreview) {
			// Display display = ((WindowManager)
			// getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			// int orientation = display.getOrientation();
			// Camera.Parameters parameters = mCamera.getParameters();

			// Camera.Size sz1 = parameters.getPreviewSize();
			// if(orientation == 0)
			// {
			// parameters.set("orientation", "portrait");
			// }
			// else
			// {
			// parameters.set("orientation", "landscape");
			// }
			// test p1
			/*
			 * int h = sz1.height; int w = sz1.width; if(orientation == 0) {
			 * parameters.setPreviewSize(h, w); } else {
			 * parameters.setPreviewSize(w, h); }
			 * 
			 * mCamera.setParameters(parameters);
			 */
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
				// bIsPreview = true;
			}
		}
	}

	/**
	 * Release and close the camera
	 */
	private void resetCamera() {
		if (mCamera != null) {
			try {
				mCamera.stopPreview();
			} catch (Exception e) {

			} finally {
				mCamera.release();
				mCamera = null;
			}
		}
	}

	/**
	 * ShutterCallback
	 */
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Do something when the shutter closes.
		}
	};

	/**
	 * PictureCallback - handles picture file write output
	 */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// Do something when the raw data is received
		}
	};

	/**
	 * PictureCallback
	 */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// Do something with the image JPEG data.

			if (_data != null) {

				// int idCouunt = getPhotoSerialCount();
				java.io.FileOutputStream fStream;
				try {
					fStream = new FileOutputStream(CAPTURE_FILE_PATH
							+ "Photo.jpg");
					fStream.write(_data);
					fStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Time tmt = new Time();
				tmt.setToNow();
				String FILE_NAME = String.format(
						"%02d%02d%02d_%02d%02d%02d.jpg", (tmt.year - 2000),
						(tmt.month + 1), tmt.monthDay, tmt.hour, tmt.minute,
						tmt.second);
				tmt = new Time("GMT");
				tmt.setToNow();
				File file1 = new File(CAPTURE_FILE_PATH + FILE_NAME);
				boolean exists = file1.exists();
				while (exists) {
					file1 = new File(CAPTURE_FILE_PATH + FILE_NAME);
					exists = file1.exists();
					tmt.setToNow();
				}

				// if ((mLatitude != 0) && (mLongitude != 0))
				// {
				// long tmDiff = System.currentTimeMillis() -
				// LocationEngine.currPos.Updatetime;
				// if ((LocationEngine.currPos.Updatetime != 0) &&
				// ((tmDiff/1000) < 60)) {
				if (true) {
					LocationInfo li = new LocationInfo();

					li.GPSDateStamp = String.format("%04d:%02d:%02d", tmt.year,
							(tmt.month + 1), tmt.monthDay);
					li.GPSTimeStamp = String.format("%02d %02d %02d ",
							tmt.hour, tmt.minute, tmt.second);
					li.GPSLatitude = GPSListener.lon;// 19.98765;//LocationEngine.currPos.Lat;
					li.GPSLongitude = GPSListener.lat;// 150.12345;
														// //LocationEngine.currPos.Lon;

					Log.d("GeoBot", "mLatitude=" + li.GPSLatitude
							+ ", mLongitude=" + li.GPSLongitude);

					// li.GPSDateStamp = "2010:01:26";
					// li.GPSTimeStamp = "11 52 03 ";
					// li.GPSLatitude = 25.0268;
					// li.GPSLongitude = 121.5252;

					int i = GPSTagNtvEngine.WriteGPSInfo(CAPTURE_FILE_PATH
							+ "Photo.jpg", CAPTURE_FILE_PATH + FILE_NAME, li);

					Log.i("UICamera.java",
							"WriteGPSInfo retrun i" + String.valueOf(i));
					// Log.i("UICamera.java","yawhaw_lon="+String.valueOf(goetag[1])+"yawhaw_lan="+String.valueOf(goetag[0]));
					Log.d("GeoBot", "FILE_NAME=" + FILE_NAME);

					File filed = new File(CAPTURE_FILE_PATH + "Photo.jpg");
					filed.delete();

					// Return to the application
					File file = new File(CAPTURE_FILE_PATH + FILE_NAME);
					Uri uri = Uri.fromFile(file);
					intent.setData(uri);
				} else {
					File file = new File(CAPTURE_FILE_PATH + "Photo.jpg");
					boolean a = file.renameTo(new File(CAPTURE_FILE_PATH
							+ FILE_NAME));
				}
				// UICamera.this.setResult(RESULT_OK, intent);
				// UICamera.this.finish();

			}
			bIsTakingPIC = false;
			bFocused = false;
			bFocusing = false;
			
			setResult(BACK_FROM_CAMERA);
			
			finish();
		}
	};

	private void setPhotoSerialCount(int count) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(PREF_PHOTO_ID, count);
		editor.commit();
	}

	private int getPhotoSerialCount() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PREFS_NAME, 0);
		int count = sharedPreferences.getInt(PREF_PHOTO_ID, 1);
		return count;
	}

	private void verifyAlbumDirectoryExists() {
		File file = new File(CAPTURE_FILE_PATH);
		boolean exists = file.exists();
		if (!exists) {
			file.mkdir();
		}
	}

}