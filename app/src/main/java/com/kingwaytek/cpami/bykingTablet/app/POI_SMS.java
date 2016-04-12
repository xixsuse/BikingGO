package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.PeopleColumns;
import android.provider.Contacts.PhonesColumns;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class POI_SMS extends Activity {

    private Button send_Button;
    private Button end_Button;
    private Button selsctContact;
    private EditText phoneText;
    private TextView poi_name;
    private TextView poi_lon;
    private TextView poi_lat;
    public String strMessage;
    public Bundle poi_share_bundle;
    public Intent intent;
    private String message;
    private static final int PICK_CONTACT_SUBACTIVITY = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.poi_sms);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.title_bar);
        // setTitle(getString(R.string.title_default));

		/* 取的Intent中的bundld物件 */
        intent = this.getIntent();
        poi_share_bundle = intent.getExtras();

		/* 取的bundle中物件的資料 */
        String POI_Name = poi_share_bundle.getString("POI_Name");
        String POI_Lon = poi_share_bundle.getString("POI_Lon");
        String POI_Lat = poi_share_bundle.getString("POI_Lat");

		/* 透過findViewById建構子來建構EditText,TextView與Button物件 */
        phoneText = (EditText) findViewById(R.id.Phone_Text);
        selsctContact = (Button) findViewById(R.id.sms_selectContact);
        poi_name = (TextView) findViewById(R.id.poi_sms_name);
        poi_lon = (TextView) findViewById(R.id.poi_sms_lon);
        poi_lat = (TextView) findViewById(R.id.poi_sms_lat);
        send_Button = (Button) findViewById(R.id.send_Button);
        end_Button = (Button) findViewById(R.id.End_Button);

		/* 將預設文字載入EditText中 */
        // phoneText.setText("請輸入電話號碼");
        poi_name.setText(POI_Name);
        poi_lon.setText(POI_Lon);
        poi_lat.setText(POI_Lat);
        // message = "你朋友"/* +name.getText().toString() */+ "\n" + "給你的訊息 "
        // + poi_name.getText().toString() + "\n" + "Lon: "
        // + poi_lon.getText().toString() + " ,lat: "
        // + poi_lat.getText().toString();
        message = "GC01;" + poi_lat.getText().toString() + ";" + poi_lon.getText().toString() + ";"
                + poi_name.getText().toString();

		/* 設定onClickListener 讓使用者點選EditText時做出反應 */

        phoneText.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
				/* 點選EditText時清空內文 */
                phoneText.setText("");
            }
        });
		/* 選擇連絡人Button OnClickListener */
        selsctContact.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
				/* 點選EditText時清空內文 */

                Uri uri = Uri.parse("content://contacts/people");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
				/* 去擷取mTextView3裡的內容 */
                // strMessage = "position";//mTextView3.getText().toString();

                startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY);
            }
        });

		/* 設定"送出Button"的onClickListener 讓使用者點選Button時做出反應 */
        send_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyManager tm = (TelephonyManager) POI_SMS.this.getSystemService(TELEPHONY_SERVICE);// 取得相?系?服?
                StringBuffer sb = new StringBuffer();
                if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                    UtilDialog uit = new UtilDialog(POI_SMS.this) {
                        @Override
                        public void click_btn_1() {
                            super.click_btn_1();
                        }
                    };
                    uit.showDialog_route_plan_choice("您未安裝SIM卡!", null, "確定", null);
                } else {
					/* 由EditText1取得簡訊收件人電話 */
                    String strDestAddress = phoneText.getText().toString();
					/* 由EditText2取得簡訊文字內容 */
                    String strMessage = message;
					/* 建構一取得default instance的 SmsManager物件 */
                    SmsManager smsManager = SmsManager.getDefault();

                    // TODO Auto-generated method stub
					/* 檢查收件人電話格式與簡訊字數是否超過70字元 */
                    if (isPhoneNumberValid(strDestAddress) == true && iswithin70(strMessage) == true) {
                        try {
							/*
							 * 兩個條件都檢查通過的情況下,發送簡訊
							 * 先建構一PendingIntent物件並使用getBroadcast()方法進行Broadcast
							 * 將PendingIntent
							 * ,電話,簡訊文字等參數傳入sendTextMessage()方法發送簡訊
							 */
                            PendingIntent mPI = PendingIntent.getBroadcast(POI_SMS.this, 0, new Intent(), 0);
                            smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
						/* 回傳RESULT_OK給前一個Activity */
                        POI_SMS.this.setResult(RESULT_OK, intent);

                        Toast.makeText(POI_SMS.this, "送出成功!!", Toast.LENGTH_SHORT).show();
                        phoneText.setText("");
                        // poi_name.setText("");

                    }
					/* 電話格式與簡訊文字不符合條件時,使用Toast告知使用者檢查 */
                    else { /* 電話格式不符 */
                        if (isPhoneNumberValid(strDestAddress) == false) { /* 且字數超過70字元 */
                            if (iswithin70(strMessage) == false) {
                                Toast.makeText(POI_SMS.this, "電話號碼格式錯誤+簡訊內容超過70字,請檢查!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(POI_SMS.this, "電話號碼格式錯誤,請檢查!!", Toast.LENGTH_SHORT).show();
                            }
                        }
						/* 字數超過70字元 */
                        else if (iswithin70(strMessage) == false) {
                            Toast.makeText(POI_SMS.this, "簡訊內容超過70字,請刪除部分內容!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        end_Button.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    // @Override
    // public void setTitle(CharSequence title) {
    // ((TextView) findViewById(R.id.title_text)).setText(title);
    // }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (!(resultCode == RESULT_CANCELED)) {
            switch (requestCode) {
                case PICK_CONTACT_SUBACTIVITY:
                    final Uri uriRet = data.getData();
                    if (uriRet != null) {
                        try {
						/* 必須要有android.permission.READ_CONTACTS權限 */
                            Cursor c = managedQuery(uriRet, null, null, null, null);
                            c.moveToFirst();
						/* 抓取通訊錄的姓名 */
                            String strName = c.getString(c.getColumnIndexOrThrow(PeopleColumns.NAME));
						/* 抓取通訊錄的電話 */
                            String strPhone = c.getString(c.getColumnIndexOrThrow(PhonesColumns.NUMBER));
						/* 把選擇到的連絡人電話顯示在mEditText1 */
                            phoneText.setText(strPhone);

						/* 設定要寄給通訊錄裡的電話 */
                            // String strDestAddress = strPhone;
                            // System.out.println(strMessage);
                            // SmsManager smsManager = SmsManager.getDefault();

                            // PendingIntent mPI = PendingIntent.getBroadcast(SMS.
                            // this, 0, new Intent(), 0);
						/* 寄出簡訊 */
                            // smsManager.sendTextMessage(strDestAddress, null,
                            // strMessage, mPI, null);
						/* 用Toast顯示傳送中 */
                            // Toast.makeText(SMS.this, getString(R.string.str_msg)+
                            // strName,Toast.LENGTH_SHORT).show();

                            // mTextView01.setText(strName+":"+strPhone);
                        } catch (Exception e) {
                            // mTextView01.setText(e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
        // super.onActivityResult(requestCode, resultCode, data);

    }

    /* 檢查字串是否為電話號碼的方法,並回傳true or false的判斷值 */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
		/*
		 * 可接受的電話格式有: ^\\(? : 可以使用 "(" 作為開頭 (\\d{3}): 緊接著三個數字 \\)? : 可以使用")"接續
		 * [- ]? : 在上述格式後可以使用具選擇性的 "-". (\\d{3}) : 再緊接著三個數字 [- ]? : 可以使用具選擇性的
		 * "-" 接續. (\\d{4})$: 以四個數字結束. 可以比對下列數字格式: (123)456-7890, 123-456-7890,
		 * 1234567890, (123)-456-7890
		 */
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		/*
		 * 可接受的電話格式有: ^\\(? : 可以使用 "(" 作為開頭 (\\d{2}): 緊接著兩個數字 \\)? : 可以使用")"接續
		 * [- ]? : 在上述格式後可以使用具選擇性的 "-". (\\d{4}) : 再緊接著四個數字 [- ]? : 可以使用具選擇性的
		 * "-" 接續. (\\d{4})$: 以四個數字結束. 可以比對下列數字格式: (123)456-7890, 123-456-7890,
		 * 1234567890, (123)-456-7890
		 */
        String expression2 = "^\\(?(\\d{2})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
		/* 建立Pattern */
        Pattern pattern = Pattern.compile(expression);
		/* 將Pattern 以參數傳入Matcher作Regular expression */
        Matcher matcher = pattern.matcher(inputStr);
		/* 建立Pattern2 */
        Pattern pattern2 = Pattern.compile(expression2);
		/* 將Pattern2 以參數傳入Matcher2作Regular expression */
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean iswithin70(String text) {
        if (text.length() <= 70)
            return true;
        else
            return false;
    }
}