package com.kingwaytek.cpami.bykingTablet.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.jni.GPSTagNtvEngine;
import com.kingwaytek.jni.PhotoAttribute;

public class PhotoGallery extends Activity {
	/** Called when the activity is first created. */
	static {
		Log.i("NaviMain", "Enter Activity");
	}

	/* requestCode of camera */
	private static final int BACK_FROM_CAMERA = 99999;
	private static final int BACK_FROM_SHOW_PHOTO = 66666;

	/* resultCode of showPhoto */
	private static final int DELETE_PHOTO = 88888;
	private static final int JUST_FINISH = 77777;

	/* Widgets */
	private ListView listView;
	private Photo_adapter adapter;
	private ImageView gps_take_picture;

	/* Data to show */
	private ArrayList<PhotoAttribute> dataSource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gps_photo);

		/* Photo list */
		listView = (ListView) findViewById(R.id.gps_listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				PhotoAttribute object = dataSource.get(position);

				Intent piIntent = new Intent();

				piIntent.putExtra("PATH", object.filePath);
				piIntent.putExtra("TIME", object.date_time);
				piIntent.putExtra("LAT", object.lat);
				piIntent.putExtra("LON", object.lon);

				piIntent.setClass(PhotoGallery.this, ShowPhoto.class);

				PhotoGallery.this.startActivityForResult(piIntent, BACK_FROM_SHOW_PHOTO);
			}
		});

		/* Take a picture */
		gps_take_picture = (ImageView) findViewById(R.id.gps_take_picture);
		gps_take_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Location loc = null;
				if (ApplicationGlobal.gpsListener != null) {
					loc = ApplicationGlobal.gpsListener.getLastLocation();
				}
				if (loc == null) {

					UtilDialog uit = new UtilDialog(PhotoGallery.this);
					uit.showDialog_route_plan_choice("無法獲得目前位置、請開啟定位功能", null, "確定", null);
				} else {

					Intent piIntent = new Intent();
					piIntent.setClass(PhotoGallery.this, UICamera.class);
					PhotoGallery.this.startActivityForResult(piIntent, BACK_FROM_CAMERA);
				}
			}
		});

		/* Initial photo list */
		this.initPhotoList();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case BACK_FROM_CAMERA:
			this.initPhotoList();
			break;

		case BACK_FROM_SHOW_PHOTO:

			if (resultCode == DELETE_PHOTO) {
				this.initPhotoList();
			}

			if (resultCode == JUST_FINISH) {
				this.setResult(RESULT_OK);
				this.finish();
			}
			break;
		}
	}

	/* Initial photo list */
	private void initPhotoList() {
		dataSource = new ArrayList<PhotoAttribute>();

		dataSource = this.getDataFromSDCard();

		adapter = new Photo_adapter(this, R.layout.gps_cell, dataSource);

		listView.setAdapter(adapter);
	}

	/* Get data from DCIM folder of SDCard */
	private ArrayList<PhotoAttribute> getDataFromSDCard() {
		File root = Environment.getExternalStorageDirectory();

		File dcim = new File(root, "DCIM");

		File[] files = dcim.listFiles(new FilenameFilter() {
			/* Filter files */
			public boolean accept(File dir, String name) {

				return ((name.endsWith(".jpg")));
			}
		});

		ArrayList<PhotoAttribute> result = new ArrayList<PhotoAttribute>();

		for (int i = 0; i < files.length; i++) {

			File file = files[i];

			PhotoAttribute PA = GPSTagNtvEngine.GetPhotoAttribute(file.getPath(), true);

			/* Filter images, stay GPS photo */
			if (PA.lat != 361.0 && PA.lon != 361.0 && PA.lat != 0 && PA.lon != 0) {

				PA.filePath = file.getPath();

				result.add(PA);
			}
		}

		return result;
	}

	/* Photo list adapter */
	class Photo_adapter extends BaseAdapter {
		int mGalleryItemBackground;
		private ArrayList<PhotoAttribute> list;
		private int layout;
		private BitmapFactory.Options options;
		private Bitmap bitmap;

		public Photo_adapter(Context context, int layout, ArrayList<PhotoAttribute> list) {
			this.list = list;
			this.layout = layout;

			options = new BitmapFactory.Options();
			options.inSampleSize = 9;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.gps_cell_photo);
				viewHolder.textView = (TextView) convertView.findViewById(R.id.gps_cell_context);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			PhotoAttribute object = list.get(position);

			try {

				bitmap = BitmapFactory.decodeStream(new FileInputStream(object.filePath), null, options);

				viewHolder.imageView.setImageBitmap(bitmap);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			DecimalFormat df = new DecimalFormat("#.####");
			if (object.lon == 0 && object.lat == 0) {
				viewHolder.textView.setText("無坐標資料");
			} else {
				viewHolder.textView.setText("" + df.format(object.lon) + ", " + df.format(object.lat));
			}
			return convertView;
		}

		private class ViewHolder {
			ImageView imageView;
			TextView textView;
		}
	}

	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}
}
