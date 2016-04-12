package com.kingwaytek.cpami.bykingTablet.app;

import com.kingwaytek.cpami.bykingTablet.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UtilDialog {

	public Dialog dialog;
	private String rename;

	public UtilDialog(Context context) {
		// if (dialog.isShowing())
		// dialog.dismiss();
		dialog = new Dialog(context, R.style.selectorDialog);
		dialog.setCancelable(false);
	}

	public void showDialog_route_plan_choice(String title, String content, String btn1, String btn2) {

		dialog.setContentView(R.layout.dialog);

		TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView dialog_content = (TextView) dialog.findViewById(R.id.dialog_context);
		Button dialog_b1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		Button dialog_b2 = (Button) dialog.findViewById(R.id.dialog_btn2);

		// title
		if (title != null) {
			dialog_title.setText(title);
		} else {
			dialog_title.setVisibility(View.GONE);
		}
		// content
		if (content != null) {
			dialog_content.setText(content);
		} else {
			dialog_content.setVisibility(View.GONE);
		}
		// button1
		if (btn1 != null) {
			dialog_b1.setText(btn1);
			dialog_b1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					click_btn_1();
				}
			});
		} else {
			dialog_b1.setVisibility(View.GONE);
		}

		// button2
		if (btn2 != null) {

			dialog_b2.setText(btn2);
			dialog_b2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					click_btn_2();
				}
			});
		} else {
			dialog_b2.setVisibility(View.GONE);
		}
		if (dialog != null)
			dialog.show();

	}

	public void click_btn_1() {
		dialog.dismiss();
	};

	public void click_btn_2() {
		dialog.dismiss();
	};

	public void dismiss() {
		dialog.dismiss();
	}

	public void editDataName(String title, String content, String btn1, String btn2) {
		dialog.setContentView(R.layout.cursor_list_view_rename_dialog);
		TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView dialog_content = (TextView) dialog.findViewById(R.id.dialog_context);
		Button dialog_b1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		Button dialog_b2 = (Button) dialog.findViewById(R.id.dialog_btn2);
		final EditText editText = (EditText) dialog.findViewById(R.id.dialog_edittext);

		if (title != null) {
			dialog_title.setText(title);
		} else {
			dialog_title.setVisibility(View.GONE);
		}
		// content
		if (content != null) {
			dialog_content.setText(content);
		} else {
			dialog_content.setVisibility(View.GONE);
		}
		// button1
		if (btn1 != null) {
			dialog_b1.setText(btn1);
			dialog_b1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rename = editText.getText().toString().trim();
					click_btn_1();
				}
			});
		} else {
			dialog_b1.setVisibility(View.GONE);
		}

		// button2
		if (btn2 != null) {

			dialog_b2.setText(btn2);
			dialog_b2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					click_btn_2();
				}
			});
		} else {
			dialog_b2.setVisibility(View.GONE);
		}
		dialog.show();

	}

	public String getRename() {
		return this.rename;
	}

	public void progressDialog(String title, String content) {
		dialog.setContentView(R.layout.dialog_progress);
		dialog.setCancelable(false);
		TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView dialog_content = (TextView) dialog.findViewById(R.id.dialog_context);

		if (title != null) {
			dialog_title.setText(title);
		} else {
			dialog_title.setVisibility(View.GONE);
		}
		// content
		if (content != null) {
			dialog_content.setText(content);
		} else {
			dialog_content.setVisibility(View.GONE);
		}
		dialog.show();
	}

}
