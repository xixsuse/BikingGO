package com.kingwaytek.cpami.bykingTablet.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * Provides methods to create and show alert dialogs.
 */
public class AlertDialogUtil {

	public enum ToggleSwitch {
		SHOW(1), HIDE(3), CANCEL(7), DISMISS(11);

		private int value;

		ToggleSwitch(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * Shows message dialog.
	 * 
	 * @author Harvey Cheng (harvey@kingwaytek.com)
	 */
	@SuppressLint("NewApi")
	public static void showMessage(Context ctx, CharSequence text, int iconId) {
		new AlertDialog.Builder(ctx, R.style.selectorDialog).setIcon(iconId).setMessage(text).setCancelable(true).show();
	}

	/**
	 * Shows confirm dialog.
	 * 
	 * @author Harvey Cheng (harvey@kingwaytek.com)
	 */
	public static void showConfirmDialog(Context ctx, CharSequence title,
			CharSequence text, DialogInterface.OnClickListener listener) {
		
		new AlertDialog.Builder(ctx).setIcon(android.R.drawable.ic_menu_more)
				.setTitle(title).setMessage(text).setPositiveButton(
						R.string.confirm_ok, listener).setNegativeButton(
						R.string.confirm_cancel, listener).setCancelable(false)
				.show();
	}

	/**
	 * show quick popup box and confirm button to close
	 * 
	 * @param context
	 *            context that the alert box resides.
	 * @param message
	 *            message to show.
	 * @param confirmText
	 *            confirm button text.
	 * 
	 * @author Andy Chiao (andy.chiao@kingwaytek.com)
	 */
	public static AlertDialog createMsgWithConfirm(Context context,
			String message, String confirmText) {
		AlertDialog.Builder alertMsgBuilder = new AlertDialog.Builder(context);
		alertMsgBuilder
				.setMessage(message == null || message.length() <= 0 ? ""
						: message);
		alertMsgBuilder.setNeutralButton(confirmText == null
				|| confirmText.length() <= 0 ? context
				.getString(R.string.dialog_close_button_text) : confirmText,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						return;
					}
				});
		return alertMsgBuilder.create();
	}

	public static AlertDialog showMsgWithConfirm(Context context,
			String message, String confirmText) {
		AlertDialog dialog = createMsgWithConfirm(context, message, confirmText);
		dialog.show();
		return dialog;
	}

	/**
	 * Context Menu for Selection.
	 * 
	 * @param context
	 *            context that the menu resides.
	 * @param options
	 *            selection options.
	 * @param dlgListener
	 *            Dialog listener.
	 * 
	 * @author Andy Chiao (andy.chiao@kingwaytek.com)
	 */
	public static AlertDialog createContextSelection(Context context,
			String title, String[] options,
			DialogInterface.OnClickListener dlgListener) {
		// final String[] options = new String[] { "CSV", "GPX", "KML" };

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

		alertBuilder.setTitle(title);
		alertBuilder.setItems(options, dlgListener);
		alertBuilder.setNegativeButton(context
				.getString(R.string.dialog_cancel_button_text),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		return alertBuilder.create();
	}

	public static AlertDialog showContextSelection(Context context,
			String title, String[] options,
			DialogInterface.OnClickListener dlgListener) {
		AlertDialog dialog = createContextSelection(context, title, options,
				dlgListener);
		dialog.show();
		return dialog;
	}

	public static void toggleDialogAsync(final Context context,
			final Dialog dialog, final ToggleSwitch which) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						switch (which) {
						case SHOW:
							dialog.show();
							break;
						case CANCEL:
							dialog.cancel();
						case HIDE:
							dialog.hide();
						case DISMISS:
							dialog.dismiss();
						default:
							break;
						}
					} catch (Exception e) {
						Log.w("AlertDialogUtil", "Could not toggle dialog to "
								+ which.toString() + " - " + e);
					}

				}
			});
		}
	}

	/**
	 * Show a waiting Dialog asynchronize
	 * 
	 * @param context
	 *            context that the dialog resides.
	 * @param id
	 *            the dialog ID that the instance must be create in
	 *            onCreateDialog within an Activity
	 * 
	 * @author Andy Chiao (andy.chiao@kingwaytek.com)
	 */
	public static void showDialogAsync(final Context context, final int id) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					try {
						((Activity) context).showDialog(id);
					} catch (IllegalStateException e) {
						Log.w("AlertDialogUtil",
								"Could not display dialog with id " + id, e);
					}
				}
			});
		}
	}

	/**
	 * Hide a waiting Dialog asynchronize
	 * 
	 * @param context
	 *            context that the dialog resides.
	 * @param dialog
	 *            the dialog to hide
	 */
	public static void hideDialog(final Context context, final Dialog dialog) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					try {
						dialog.hide();
					} catch (Exception e) {
						Log.w("AlertDialogUtil", "Could not hide dialog." + e);
					}
				}
			});
		}
	}

	/**
	 * Dismiss a waiting Dialog asynchronize
	 * 
	 * @param context
	 *            context that the dialog resides.
	 * @param id
	 *            the dialog ID that the instance must be create in
	 *            onCreateDialog within an Activity
	 * 
	 * @author Andy Chiao (andy.chiao@kingwaytek.com)
	 */
	public static void dismissDialogAsync(final Context context, final int id) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					try {
						((Activity) context).dismissDialog(id);
					} catch (IllegalArgumentException e) {
						Log.w("AlertDialogUtil",
								"loadingDialog not showing. - " + e);
					}
				}
			});
		}
	}
}