package com.kingwaytek.cpami.bykingTablet.app;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.kingwaytek.cpami.bykingTablet.R;

public class DataProgressDialog extends Dialog {

	public static final int DIALOG_LOADING = 2;
	public static final int DIALOG_LOCATING = 4;
	public static final int DIALOG_QUERYING = 6;
	public static final int DIALOG_SELECTION = 1;
	public static final int DIALOG_WARNING = 3;
	public static final int DIALOG_CONFIRM = 5;

	public enum DialogType { // need reverse
		LOADING(2), LOCATING(4), QUERYING(6), SELECTION(1), WARNING(3), CONFIRM(
				5), NULL(-1);

		private static final Map<Integer, DialogType> typeMap = new HashMap<Integer, DialogType>();
		private final int value;

		static {
			for (DialogType dt : EnumSet.allOf(DialogType.class)) {
				typeMap.put(dt.getValue(), dt);
			}
		}

		DialogType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static DialogType get(int value) {
			return typeMap.get(value);
		}
	}

	public DataProgressDialog(Context context) {
		super(context, R.style.data_progress_dialog_style);
	}

	public static DataProgressDialog show(Context context, CharSequence title,
			CharSequence message) {
		return show(context, title, message, false);
	}

	public static DataProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static DataProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static DataProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		DataProgressDialog dialog = new DataProgressDialog(context);
		dialog.setTitle(title);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);

		/* The next line will add the ProgressBar to the dialog. */
		dialog.addContentView(new ProgressBar(context), new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();

		return dialog;
	}

	public static DataProgressDialog create(Context context,
			CharSequence title, CharSequence message) {
		return create(context, title, message, false);
	}

	public static DataProgressDialog create(Context context,
			CharSequence title, CharSequence message, boolean indeterminate) {
		return create(context, title, message, indeterminate, false, null);
	}

	public static DataProgressDialog create(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		return create(context, title, message, indeterminate, cancelable, null);
	}

	public static DataProgressDialog create(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable, OnCancelListener cancelListener) {
		DataProgressDialog dialog = new DataProgressDialog(context);
		dialog.setTitle(title);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);

		/* The next line will add the ProgressBar to the dialog. */
		dialog.addContentView(new ProgressBar(context), new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return dialog;
	}
}
