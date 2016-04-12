package com.kingwaytek.api.login.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.kingwyatek.api.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MemberUtils {

	public static boolean isPhoneNumberFormat(String phone) {
		return phone.trim().matches("09.{8}");
	}

	// ap不可包含空白，實際上server沒檔
	public static boolean isPasswordFormat(String password) {
		return password.matches("[a-zA-Z\\d]{6,20}");
	}

	public static boolean isEmailFormat(Context context, String email) {
		final String EMAIL_FORMAT = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		return email.matches(EMAIL_FORMAT);
	}

	// 比對密碼是否相同
	public static boolean isSamePassword(Context context, String password1, String password2) {

		return password1.equals(password2);
	}

	// 開啟 EditText 的 keyboard
	public static void setSoftInputVisible(Context context, EditText edittext) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * 會員資料更新的部份使用callback實作
	 */
	public static interface BirthdayUpdateCallback {
		/**
		 * @return 使用者是否有更新資料
		 */
		public boolean isEdited(String year, String month, String day);

		/**
		 * 更新server及client的資料
		 */
		public void update(String year, String month, String day);

		/**
		 * 生日日期不可為未來時間
		 */
		public void timeIncorrect();
	}

	/**
	 * 顯示生日選擇dialog，
	 * 
	 * @param context
	 * @param currentBirthday
	 *            當前生日日期
	 * @param callback
	 *            實作callback
	 */
	public static void showBirthdayDialog(Context context, String currentBirthday, BirthdayUpdateCallback callback) {
		int year;
		int month;
		int day;
		if (currentBirthday.isEmpty()) {
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		} else {
			String[] birthday = currentBirthday.split("/");
			year = Integer.valueOf(birthday[0]);
			month = Integer.valueOf(birthday[1]) - 1;
			day = Integer.valueOf(birthday[2]);
		}

		DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DateSetListener(callback), year, month, day);
		datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
		datePickerDialog.show();
	}

	private static class DateSetListener implements DatePickerDialog.OnDateSetListener {
		private boolean mFlag;
		private BirthdayUpdateCallback mCallback;

		public DateSetListener(BirthdayUpdateCallback callback) {
			this.mCallback = callback;
			this.mFlag = false;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			if (mFlag == false) {
				mFlag = true;
				String selectYear = String.valueOf(year);
				String selectMonth = String.valueOf(monthOfYear + 1);
				String selectDay = String.valueOf(dayOfMonth);
				String birthday = String.format("%s/%s/%s", selectYear, selectMonth, selectDay);
				if (MemberUtils.isTimeCorrect(birthday)) {
					if (mCallback.isEdited(selectYear, selectMonth, selectDay)) {
						mCallback.update(selectYear, selectMonth, selectDay);
					}
				} else {
					mCallback.timeIncorrect();
				}
			} else {
				mFlag = false;
			}
		}
	}

	/**
	 * 檢查輸入是否是未來時間
	 * 
	 * @param data
	 *            欲查詢的日期
	 * @return true:當天或昨天以前 false:明天或未來時間
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isTimeCorrect(String data) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date d1 = sdf.parse(data);
			Date d2 = new Date(System.currentTimeMillis());
			int day = daysBetween(d1, d2);
			if (day >= 0) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			return false;
			// 告訴user，這個日期不是一個正確的日期"
		}
	}

	/**
	 * 計算兩個日期差距的天數
	 * 
	 * @param smdate
	 *            較早日期
	 * @param bdate
	 *            較晚日期
	 * @return 大於0:當天或昨天以前 負數:明天或未來時間
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		try {
			return Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 會員資料更新的部份使用callback實作
	 */
	public static interface SexUpdateCallback {
		/**
		 * @return 使用者是否有更新資料
		 */
		public boolean isEdited(String selectSex);

		/**
		 * 更新server及client的資料
		 */
		public void update(String selectSex);
	}

	public static class SexItemSelectedListener implements OnItemSelectedListener {
		// isFirstTime == true: spinner.setOnItemSelectedListener(this);
		// Will call "onItemSelected" Listener.
		boolean isFirstTime;
		Context mContext;
		SexUpdateCallback mCallback;

		public SexItemSelectedListener(Context context, SexUpdateCallback callback) {
			this.mContext = context;
			this.mCallback = callback;
			this.isFirstTime = true;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (isFirstTime) {
				isFirstTime = false;
			} else {
				String selectItem = parent.getSelectedItem().toString();
				if (selectItem.equals(mContext.getString(R.string.sex_male))) {
					selectItem = mContext.getString(R.string.sex_male_eng);
				} else if (selectItem.equals(mContext.getString(R.string.sex_female))) {
					selectItem = mContext.getString(R.string.sex_female_eng);
				} else {
					selectItem = mContext.getString(R.string.sex_other_eng);
				}

				if (mCallback.isEdited(selectItem)) {
					mCallback.update(selectItem);
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

}
