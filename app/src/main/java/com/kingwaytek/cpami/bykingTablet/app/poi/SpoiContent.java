package com.kingwaytek.cpami.bykingTablet.app.poi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ImageUtil;
import com.kingwaytek.cpami.bykingTablet.sql.History;
import com.kingwaytek.cpami.bykingTablet.sql.Spoi;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;

/**
 * POI Query SPOI Content
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class SpoiContent extends Activity implements OnClickListener,
		OnPageChangeListener {

	private Intent itenCaller;
	private ActivityCaller myCaller;
	private int itemId;
	private Spoi thisSpoi;

	private ViewPager viewPager;

	private ViewFlipper flipper;

	private RadioGroup radioGroup;
	private RadioButton radio01;
	private RadioButton radio02;
	private RadioButton radio03;

	private SparseArray<RadioButton> pagesRadio;

	private SparseArray<Bitmap> dataSource;

	private int[] numberOfPages;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		itenCaller = getIntent();
		setContentView(R.layout.spoi_content);

		myCaller = (ActivityCaller) itenCaller
				.getSerializableExtra("Spoi_Caller");
		itemId = (int) itenCaller.getLongExtra("Spoi_ID", -1);

		Log.i("SpoiContent", "myCaller :" + myCaller.toString()
				+ ", item id from caller = " + itemId);

		// getLastNonConfigurationInstance要在onCreate裡作,將onResume裡的code搬來此

		// fetch spoi here.
		thisSpoi = new Spoi(this, itemId);
		if (thisSpoi.getID() <= 0) {
			Log.e("SpoiContent", "spoi item is null.");
			return;
		}

		Log.i("Spoi", "this spoi id : " + thisSpoi.getID() + ", poi id : "
				+ thisSpoi.getPoiId());

		SetSpoiUI();

		Log.d("NativeHeapSize", Long.toString(Debug.getNativeHeapSize()));

	}

	@Override
	public void onResume() {
		super.onResume();

		if (itemId <= 0)
			return;
		/*
		 * // fetch spoi here. thisSpoi = new Spoi(this, itemId); if
		 * (thisSpoi.getID() <= 0) { Log.e("SpoiContent", "spoi item is null.");
		 * return; }
		 * 
		 * Log.i("Spoi", "this spoi id : " + thisSpoi.getID() + ", poi id : " +
		 * thisSpoi.getPoiId()); SetSpoiUI();
		 */
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (bm1 != null) {
			bm1.recycle();
		}

		if (bm2 != null) {
			bm2.recycle();
		}

		if (bm3 != null) {
			bm3.recycle();
		}
	}

	@Override
	public void onClick(View v) {
		System.gc();
//		switch (v.getId()) {
//		case R.id.spoi_content_address:
//			// TODO call POIContent intent
//			Intent itenContent = new Intent(this, POIMapContent.class);
//			itenContent.putExtra("POI_Caller", ActivityCaller.SPOI);
//			itenContent.putExtra("POI_ID", (long) thisSpoi.getPoiId());
//			itenContent.putExtra("setpoint",
//					itenCaller.getStringExtra("setpoint"));
//			startActivityForResult(itenContent, ActivityCaller.SPOI.getValue());
//
//			PutHistory(thisSpoi.getName()
//					+ ((thisSpoi.getSubBranch().trim().length() == 0) ? ""
//							: "(" + thisSpoi.getSubBranch() + ")"),
//					thisSpoi.getPoiId());
//			break;
		// case R.id.spoi_content_layout_photo1:
		// // TODO call POIContent intent
		// ImageButton imgbtn1 = new ImageButton(this);
		// imgbtn1.setBackgroundColor(000000);
		// this.setContentView(imgbtn1);
		// imgbtn1.setImageBitmap(resizeImage(bm1));
		// imgbtn1.setOnClickListener(backToList);
		// isImageShowing = true;
		// break;
		// case R.id.spoi_content_layout_photo2:
		// // TODO call POIContent intent
		// ImageButton imgbtn2 = new ImageButton(this);
		// imgbtn2.setBackgroundColor(000000);
		// this.setContentView(imgbtn2);
		// imgbtn2.setImageBitmap(resizeImage(bm2));
		// imgbtn2.setOnClickListener(backToList);
		// isImageShowing = true;
		// break;
		// case R.id.spoi_content_layout_photo3:
		// // TODO call POIContent intent
		// ImageButton imgbtn3 = new ImageButton(this);
		// imgbtn3.setBackgroundColor(000000);
		// this.setContentView(imgbtn3);
		// imgbtn3.setImageBitmap(resizeImage(bm3));
		// imgbtn3.setOnClickListener(backToList);
		// isImageShowing = true;
		// break;
//		default:
//			break;
//		}
	}

	// imagebutton圖片的onclick動作//

	// Button.OnClickListener backToList = new Button.OnClickListener() {
	// @Override
	// public void onClick(View view) {
	// setContentView(R.layout.spoi_content);
	// SpoiContent.this.SetSpoiUI();
	// isImageShowing = false;
	// }
	// };

	public void PutHistory(String name, long id) {
		History history = new History(this);
		history.setName(name);
		history.setItemID((int) id);
		history.setType(ContentType.POI.getValue());
		history.Put();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK
				&& requestCode == ActivityCaller.SPOI.getValue()) {
			itenCaller.putExtra("POI_Action",
					data.getSerializableExtra("POI_Action"));
			// itenCaller.putExtra("POI_Name", data.getStringExtra("POI_Name"));
			// itenCaller.putExtra("POI_Location", data
			// .getParcelableExtra("POI_Location"));
			// itenCaller.putExtra("POI_Others", data
			// .getStringArrayExtra("POI_Others"));

			setResult(RESULT_OK, itenCaller);
			finish();
		}
	}

	private String photoBlob1 = null;
	private String photoBlob2 = null;
	private String photoBlob3 = null;
	private Bitmap bm1;
	private Bitmap bm2;
	private Bitmap bm3;

	private void SetSpoiUI() {
		// TextView tvTheme = (TextView) findViewById(R.id.spoi_content_theme);
		TextView tvName = (TextView) findViewById(R.id.spoi_content_name);
		TextView tvAddress = (TextView) findViewById(R.id.spoi_content_address);
		TextView tvDescription = (TextView) findViewById(R.id.spoi_content_description);
		TextView tvTel = (TextView) findViewById(R.id.spoi_content_tel);
		// Button btnCall = (Button) findViewById(R.id.ic_call);
		// ImageView ivPhoto1 = (ImageView)
		// findViewById(R.id.spoi_content_photo1);
		// ImageView ivPhoto2 = (ImageView)
		// findViewById(R.id.spoi_content_photo2);
		// ImageView ivPhoto3 = (ImageView)
		// findViewById(R.id.spoi_content_photo3);
		// LinearLayout lyPhoto1 = (LinearLayout)
		// findViewById(R.id.spoi_content_layout_photo1);
		// LinearLayout lyPhoto2 = (LinearLayout)
		// findViewById(R.id.spoi_content_layout_photo2);
		// LinearLayout lyPhoto3 = (LinearLayout)
		// findViewById(R.id.spoi_content_layout_photo3);

		tvAddress.setOnClickListener(this);
		// lyPhoto1.setOnClickListener(this);
		// lyPhoto2.setOnClickListener(this);
		// lyPhoto3.setOnClickListener(this);

		// tvTheme.setText(thisSpoi.getTheme());
		tvName.setText(thisSpoi.getName());
		tvAddress.setText(thisSpoi.getAddress());
		tvDescription.setText(thisSpoi.getDescription());
		tvTel.setText(thisSpoi.getTelNumber());
		// btnCall.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (thisSpoi.getTelNumber() == null
		// || thisSpoi.getTelNumber().equals("")) {
		// return;
		// }
		// startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
		// + thisSpoi.getTelNumber())));
		// }
		// });

		photoBlob1 = thisSpoi.getPhoto1Blob();
		photoBlob2 = thisSpoi.getPhoto2Blob();
		photoBlob3 = thisSpoi.getPhoto3Blob();

		// Activity onCreate()時將旋轉時保存起來的物件取出
		// 參考http://developer.android.com/resources/articles/faster-screen-orientation-change.html
		// final HashMap<String, Bitmap> data = (HashMap<String, Bitmap>)
		// getLastNonConfigurationInstance();
		// if (data == null) {
		// // Log.d("onRetainConfig",
		// // "getLastNonConfigurationInstance() is null");
		bm1 = ImageUtil.PathToBitmap(photoBlob1, 2);
		bm2 = ImageUtil.PathToBitmap(photoBlob2, 2);
		bm3 = ImageUtil.PathToBitmap(photoBlob3, 2);

		dataSource = new SparseArray<Bitmap>();

		int count = 0;
		if (bm1 != null) {
			dataSource.put(0, bm1);
			count++;
		}
		if (bm2 != null) {
			dataSource.put(1, bm2);
			count++;
		}
		if (bm3 != null) {
			dataSource.put(2, bm3);
			count++;
		}

		numberOfPages = new int[count];

		for (int i = 0; i < count; i++) {
			numberOfPages[i] = i;
		}

		// } else {
		// bm1 = data.get("bm1");
		// bm2 = data.get("bm2");
		// bm3 = data.get("bm3");
		// }
		// ivPhoto1.setImageBitmap(bm1);
		// ivPhoto2.setImageBitmap(bm2);
		// ivPhoto3.setImageBitmap(bm3);
		// lyPhoto1.setVisibility((bm1 != null && photoBlob1.trim() != "") ?
		// View.VISIBLE
		// : View.GONE);
		// lyPhoto2.setVisibility((bm2 != null && photoBlob2.trim() != "") ?
		// View.VISIBLE
		// : View.GONE);
		// lyPhoto3.setVisibility((bm3 != null && photoBlob3.trim() != "") ?
		// View.VISIBLE
		// : View.GONE);

		/*
		 * ivPhoto1.setImageBitmap(bm1=ImageUtil
		 * .BlobToBitmap(thisSpoi.getPhoto1Blob(),100));
		 * ivPhoto2.setImageBitmap(bm2=ImageUtil
		 * .BlobToBitmap(thisSpoi.getPhoto2Blob(),100));
		 * ivPhoto3.setImageBitmap(bm3=ImageUtil
		 * .BlobToBitmap(thisSpoi.getPhoto3Blob(),100));
		 * lyPhoto1.setVisibility((thisSpoi.getPhoto1Blob() != null && thisSpoi
		 * .getPhoto1Blob().length > 0) ? View.VISIBLE : View.GONE);
		 * lyPhoto2.setVisibility((thisSpoi.getPhoto2Blob() != null && thisSpoi
		 * .getPhoto2Blob().length > 0) ? View.VISIBLE : View.GONE);
		 * lyPhoto3.setVisibility((thisSpoi.getPhoto3Blob() != null && thisSpoi
		 * .getPhoto3Blob().length > 0) ? View.VISIBLE : View.GONE);
		 */

		flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper1);

		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup1);

		radio01 = (RadioButton) this.findViewById(R.id.radio01);
		radio02 = (RadioButton) this.findViewById(R.id.radio02);
		radio03 = (RadioButton) this.findViewById(R.id.radio03);

		pagesRadio = new SparseArray<RadioButton>();

		pagesRadio.put(0, radio01);
		pagesRadio.put(1, radio02);
		pagesRadio.put(2, radio03);

		int totalImageCount = numberOfPages.length;

		switch (totalImageCount) {

		case 0:
		case 1:
			radioGroup.setVisibility(View.GONE);
			break;

		case 2:
			radio03.setVisibility(View.GONE);
			break;
		}

		viewPager = (ViewPager) this.findViewById(R.id.viewPager);

		POiPageAdapter adpater = new POiPageAdapter(this, this, dataSource);

		viewPager.setAdapter(adpater);

		viewPager.setOnPageChangeListener(this);

		viewPager.setCurrentItem(0);
		Button btn_loc = (Button) findViewById(R.id.btn_book_loc);
		btn_loc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent itenContent = new Intent(SpoiContent.this, POIMapContent.class);
				itenContent.putExtra("POI_Caller", ActivityCaller.SPOI);
				itenContent.putExtra("POI_ID", (long) thisSpoi.getPoiId());
				itenContent.putExtra("setpoint",
						itenCaller.getStringExtra("setpoint"));
				startActivityForResult(itenContent, ActivityCaller.SPOI.getValue());

				PutHistory(thisSpoi.getName()
						+ ((thisSpoi.getSubBranch().trim().length() == 0) ? ""
								: "(" + thisSpoi.getSubBranch() + ")"),
						thisSpoi.getPoiId());
			}
		});
		
		Button btn_call = (Button) findViewById(R.id.btn_book_call);
		if (thisSpoi.getTelNumber() == null
				|| thisSpoi.getTelNumber().equals("")) {
			btn_call.setVisibility(View.GONE);;
		}
		btn_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (thisSpoi.getTelNumber() == null
						|| thisSpoi.getTelNumber().equals("")) {
					return;
				}
				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ thisSpoi.getTelNumber())));
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

		RadioButton radio = pagesRadio.get(arg0);

		radio.setChecked(true);
	}

	@Override
	public void onBackPressed() {
		int current = flipper.indexOfChild(flipper.getCurrentView());

		if (current == 1) {
			flipper.setDisplayedChild(0);

		} else {
			this.finish();
		}
	}

	// // 旋轉時將圖片用物件保存起來重複利用
	// @Override
	// public Object onRetainNonConfigurationInstance() {
	// final HashMap<String, Bitmap> obj = new HashMap<String, Bitmap>();
	// obj.put("bm1", bm1);
	// obj.put("bm2", bm2);
	// obj.put("bm3", bm3);
	// return obj;
	// }

}
