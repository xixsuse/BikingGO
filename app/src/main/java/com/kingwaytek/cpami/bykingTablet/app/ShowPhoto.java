package com.kingwaytek.cpami.bykingTablet.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ContextMenuOptions;

import facebook.FacebookActivity;

public class ShowPhoto extends Activity {

	private static final int DELETE_PHOTO = 88888;

	private static final int JUST_FINISH = 77777;

	public double exifLat;
	public double exifLon;
	private ImageView image;
	private TextView LocationText;
	private TextView TimeText;
	private ImageView gps_take_picture;
	private Intent setPointIntent;

	private Bitmap a;
	private ActionSheet actionSheet;
	private ImageView actionsheet_btn;
	private int[][] sub_view;

	private String filePath;
	private double latitude;
	private double longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);
		Bundle extra = this.getIntent().getExtras();

		if (extra != null) {

			filePath = extra.getString("PATH");
			String time = extra.getString("TIME");

			latitude = extra.getDouble("LAT");
			longitude = extra.getDouble("LON");

			LocationText = (TextView) findViewById(R.id.location_text);
			TimeText = (TextView) findViewById(R.id.time_text);

			double[] goetag = new double[2];
			goetag[0] = latitude;
			goetag[1] = longitude;
			DecimalFormat df = new DecimalFormat("#.####");
			LocationText.setText("座標:" + df.format(goetag[1]) + ", " + df.format(goetag[0]));

			String[] mTime = time.split(":");

			TimeText.setText("日期:" + mTime[0] + "/" + mTime[1] + "/" + time.substring(0, 2));

			setImage();
		}

		setPointIntent = new Intent();
		gps_take_picture = (ImageView) findViewById(R.id.gps_take_picture);
		gps_take_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent piIntent = new Intent();
				piIntent.setClass(ShowPhoto.this, UICamera.class);
				startActivity(piIntent);
			}
		});

		setActionSheet();
		actionsheet_btn = (ImageView) findViewById(R.id.actionsheet_btn);
		actionsheet_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionSheet.show();
				actionsheet_btn.setClickable(false);
			}
		});
	}

	public void setActionSheet() {
		sub_view = new int[7][2];
		sub_view[0][0] = R.id.actionsheet_gps_01;
		sub_view[1][0] = R.id.actionsheet_gps_02;
		sub_view[2][0] = R.id.actionsheet_gps_03;
		sub_view[3][0] = R.id.actionsheet_gps_04;
		sub_view[4][0] = R.id.actionsheet_gps_05;
		sub_view[5][0] = R.id.actionsheet_gps_06;
		sub_view[6][0] = R.id.actionsheet_gps_07;
		sub_view[1][1] = 1;
		actionSheet = (ActionSheet) findViewById(R.id.actionSheet1);
		actionSheet.setContext(ShowPhoto.this);
		actionSheet.setActionSheetLayout(R.layout.action_sheet_gps, sub_view);
		actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

			@Override
			public void onButtonClick(ActionSheet actionsheet, int index, int id) {
				int flag = 0;
				switch (index) {
				case 0:
					Intent intentFB = new Intent(ShowPhoto.this, FacebookActivity.class);

					intentFB.putExtra("FILEPATH", filePath);

					ShowPhoto.this.startActivity(intentFB);
					break;
				case 1:
					Intent intent = new Intent();
					intent.putExtra("SMS_Action", 2);
					intent.putExtra("Photo_Lon", latitude);
					intent.putExtra("Photo_Lat", longitude);
					intent.setClass(ShowPhoto.this, SMS.class);
					startActivity(intent);

					break;
				case 5:

					final File f = new File(filePath);
					f.exists();

					UtilDialog uit = new UtilDialog(ShowPhoto.this) {
						@Override
						public void click_btn_1() {
							f.delete();
							setResult(DELETE_PHOTO);
							super.click_btn_1();
							finish();
						
						}
					};
					uit.showDialog_route_plan_choice("您確定要刪除" + "`" + f.getName() + "`" + "?", null, "確定", "取消");
					break;
				case 2:// 設定起點
						// if (count <= 0) {
						// showNoPictureDialog();
					// } else {
					flag = MapActivity.START_POINT;
					HasPointSet(flag);
					// setPointIntent.putExtra("Action",POIMenu.NAVIGATION)
					// ;
					// }
					break;
				case 4:// 立即前往
						// if (count <= 0) {
						// showNoPictureDialog();
					// } else {
					setPointIntent.putExtra("Action", ContextMenuOptions.NAVIGATION);
					flag = MapActivity.END_POINT;
					MapActivity.setGoImmediately(true);
					SetPointAction(flag);
					// }
					break;
				case 3:// 設定目的地
						// if (count <= 0) {
						// showNoPictureDialog();
						// } else {
					flag = MapActivity.END_POINT;
					HasPointSet(flag);
					// }
					break;
				default:
					break;
				}
				actionsheet_btn.setClickable(true);
			}
		});
	}

	public void setImage() {
		a = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 2;
		try {

			try {
				a = BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);

			} catch (OutOfMemoryError e) {

				options.inSampleSize = 8;

				a = BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);
			}

		} catch (FileNotFoundException e) {
		}

		if (a != null) {
			Log.i("Size", "Width:" + a.getWidth() + " Height:" + a.getHeight());
		}

		image = (ImageView) findViewById(R.id.image);
		image.setImageBitmap(a);
	}

	private void showNoPictureDialog() {

		UtilDialog uit = new UtilDialog(ShowPhoto.this);
		uit.showDialog_route_plan_choice("目前沒有照片可設定", null, "確定", null);
	}

	private void HasPointSet(final int flag) {
		String locInfo = MapActivity.getName(flag);
		locInfo += MapActivity.getAddress(flag) == "" ? "" : "\n" + MapActivity.getAddress(flag);
		Log.i("HasPointSet", "flag:" + flag + ", info:" + locInfo);
		// if a point has been set already
		if (!locInfo.equals("")) {
			UtilDialog uit = new UtilDialog(ShowPhoto.this) {
				@Override
				public void click_btn_1() {
					setPointIntent.putExtra("Action", ContextMenuOptions.NAVIGATION);
					SetPointAction(flag);
					super.click_btn_1();
				}
			};
			uit.showDialog_route_plan_choice("已設定:\n" + locInfo + "\n是否取代?", null, "是", "否");
		} else {
			setPointIntent.putExtra("Action", ContextMenuOptions.NAVIGATION);
			SetPointAction(flag);
		}
	}

	private void SetPointAction(int flag) {
		DecimalFormat dFormat = new DecimalFormat("#.######");
		double lon = Double.valueOf(dFormat.format(longitude));
		double lat = Double.valueOf(dFormat.format(latitude));
		GeoPoint point = new GeoPoint(lat, lon);
		MapActivity.setPosition("Lon:" + point.getLongitude() + "\n" + "Lat:" + point.getLatitude(), point, flag, "");
		setResult(JUST_FINISH, setPointIntent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (a != null) {
			a.recycle();
		}
	}

}