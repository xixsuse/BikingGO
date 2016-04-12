package com.example.actionsheet;

import com.kingwaytek.cpami.bykingTablet.R;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;

public class ActionSheet extends LinearLayout {

	private Context context;
	private View actionsheetView;
	private ActionSheetButtonClickListener aslistener;
	private int actionSheet_layout;
	private int[][] sub_layout; // id,visible
	private View layout;
	private boolean isShowfinish = false;

	public ActionSheet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ActionSheet(Context context) {
		super(context);
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setActionSheetLayout(int actionSheetLayout, int[][] sub_layout) {
		this.actionSheet_layout = actionSheetLayout;
		this.sub_layout = sub_layout;
		init();
	}

	public void setOnActionSheetButtonClickListener(
			ActionSheetButtonClickListener listener) {
		this.aslistener = listener;
	}

	public void init() {
		this.setBackgroundColor(0x7f000000);
		// this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(actionSheet_layout, null);
		View sub;
		int position;
		for (int i = 0; i < sub_layout.length; i++) {
			sub = (View) layout.findViewById(sub_layout[i][0]);
			sub.setTag(i);
			if (sub_layout[i][1] == 1) {
				sub.setVisibility(View.GONE);
			}
			sub.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					int index = Integer.valueOf(view.getTag().toString());
					aslistener.onButtonClick(ActionSheet.this, index,
							sub_layout[index][0]);
					hide();
				}
			});

		}
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// layout.setBackgroundColor(0x7f000000);
		layout.setPadding(20, 20, 20, 20);
		this.addView(layout);
		this.setVisibility(View.INVISIBLE);
	}

	public void show() {

		this.setVisibility(View.VISIBLE);
		Animation animIn = AnimationUtils.loadAnimation(this.context,
				R.anim.push_up_in);
		animIn.setDuration(300);
		animIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Activity parent = (Activity) ActionSheet.this.context;
				parent.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
				parent.getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
						WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			}
		});
		this.layout.startAnimation(animIn);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Handler handler = new Handler();
				if (isShowfinish) {
					View sub;
					for (int i = 0; i < sub_layout.length; i++) {
						sub = (View) layout.findViewById(sub_layout[i][0]);
						sub.setClickable(true);
					}
				} else {
					handler.postDelayed(this, 300);
					isShowfinish = true;
				}
			}
		};
		runnable.run();

	}

	public void hide() {
		View sub;
		for (int i = 0; i < sub_layout.length; i++) {
			sub = (View) layout.findViewById(sub_layout[i][0]);
			sub.setClickable(false);
			isShowfinish = false;
		}

		Animation animOut = AnimationUtils.loadAnimation(this.context,
				R.anim.push_down_out);
		animOut.setDuration(300);
		animOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ActionSheet.this.removeView(ActionSheet.this.actionsheetView);
				ActionSheet.this.setVisibility(View.INVISIBLE);
			}
		});
		this.layout.startAnimation(animOut);
	}

	public interface ActionSheetButtonClickListener {
		public void onButtonClick(ActionSheet actionsheet, int index, int id);

	}
}
