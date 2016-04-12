package com.kingwaytek.cpami.bykingTablet.app.poi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.kingwaytek.cpami.bykingTablet.R;

public class POiPageAdapter extends PagerAdapter implements OnClickListener {

	private ImageButton btn_img;

	private ViewFlipper flipper;

	private Activity activity;

	private ArrayList<View> objects;

	private SparseArray<Bitmap> dataSource;

	public POiPageAdapter(Activity activity, Context context,
			SparseArray<Bitmap> dataSource) {

		this.activity = activity;

		this.dataSource = dataSource;

		objects = new ArrayList<View>();

		for (int i = 0; i < dataSource.size(); i++) {

			ImageView image = new ImageView(context);

			image.setTag(dataSource.keyAt(i));

			image.setOnClickListener(this);

			objects.add(image);
		}

		btn_img = (ImageButton) activity.findViewById(R.id.ImageButton1);

		flipper = (ViewFlipper) activity.findViewById(R.id.viewFlipper1);

		btn_img.setOnClickListener(this);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(objects.get(arg1));
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {

		((ViewPager) arg0).addView(objects.get(arg1));

		ImageView image = (ImageView) objects.get(arg1);

		image.setImageBitmap(dataSource.get(dataSource.keyAt(arg1)));

		return objects.get(arg1);
	}

	@Override
	public void onClick(View arg0) {

		if (arg0 instanceof ImageButton) {
			flipper.setDisplayedChild(0);

			return;
		}

		int tag = (Integer) arg0.getTag();

		Bitmap bitmap = dataSource.get(tag);

		if (bitmap != null) {

			this.resizeImage(bitmap);

			btn_img.setImageBitmap(bitmap);

			flipper.setDisplayedChild(1);
		}
	}

	// 將圖片符合到畫面上,非填滿//
	@SuppressWarnings("deprecation")
	public void resizeImage(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int newWidth = activity.getWindowManager().getDefaultDisplay()
				.getWidth();
		int newHeight = activity.getWindowManager().getDefaultDisplay()
				.getHeight();

		float ratio = 1;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		if (scaleWidth > scaleHeight) {
			ratio = scaleHeight;
		} else {
			ratio = scaleWidth;
		}

		int frameWidth = (int) (width * ratio);

		int frameHeight = (int) (height * ratio);

		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				frameWidth, frameHeight);
		lp1.addRule(RelativeLayout.CENTER_IN_PARENT);

		btn_img.setLayoutParams(lp1);
	}
}
