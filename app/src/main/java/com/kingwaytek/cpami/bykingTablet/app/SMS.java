package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity; 
/*必需引用PendingIntent類別才能使用getBrocast()*/
import android.app.PendingIntent; 
import android.content.Context;
import android.content.Intent; 
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle; 
import android.provider.Contacts.People; 
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
/*必需引用telephony.gsm.SmsManager類別才能使用sendTextMessage()*/

import android.view.View; 
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button; 
import android.widget.EditText; 
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.PrivateCredentialPermission;

import com.kingwaytek.cpami.bykingTablet.R;

@SuppressWarnings("deprecation")
public class SMS extends Activity  
{ 
  /*宣告變數一個Button與兩個EditText*/
  private Button send_Button; 
  private Button end_Button; 
  private Button selectContact;
  private EditText phoneText; 
  private TextView name; 
  private TextView lon; 
  private TextView lat; 
  private Intent intent;
  private String message;
  private String strMessage;
  private Button gohome;
  private String phoneNumber;

  
  private static final int PICK_CONTACT_SUBACTIVITY = 2; 
   
  /** Called when the activity is first created. */ 
  @Override 
  public void onCreate(Bundle savedInstanceState) 
  { 
    super.onCreate(savedInstanceState); 
    final Double Lon;
    final Double Lat;
    //final String LocalTel = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        
    /*取的Intent中的bundld物件*/
    //("SMS_Action",0) 從主頁面menu來的分享目前的位置
    //("SMS_Action",1) 從主頁面menu來的查詢朋友的位置
    //("SMS_Action",2) 從GPS相片來的位置分享
    //("SMS_Action",3) 回覆查詢的分享目前位置
      intent = this.getIntent();
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

      if (intent.getIntExtra("SMS_Action", 9) == 1) {

          setContentView(R.layout.sms_location_request);
          getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

          name = (TextView) findViewById(R.id.sms_name);
          //關鍵字:查詢
//       message = "我請求查詢你的位置"+"\n"+"給你的訊息 "+"\n"
//       +"Lon: "+"121.522232"+" ,lat: "+"25.026799";
          //關鍵字:GC,01:代表一般位置分享
          message = "GC02";
      }
      else if (intent.getIntExtra("SMS_Action", 9) == 0 || intent.getIntExtra("SMS_Action", 9) == 2
              || intent.getIntExtra("SMS_Action", 9) == 3) {
          setContentView(R.layout.sms);
          getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

          Lon = intent.getDoubleExtra("Photo_Lon", 121.522232);
          Lat = intent.getDoubleExtra("Photo_Lat", 25.026799);
          name = (TextView) findViewById(R.id.sms_name);
          lon = (TextView) findViewById(R.id.sms_lon);
          lat = (TextView) findViewById(R.id.sms_lat);
          lon.setText(String.valueOf(Lon));
          lat.setText(String.valueOf(Lat));
          //關鍵字:朋友
//       message = "你朋友"+name.getText().toString()+"\n"+"給你的訊息 "+name.getText().toString()+"\n"
//          +"Lon: "+lon.getText().toString()+" ,lat: "+lat.getText().toString();
          //關鍵字:GC,01:代表一般位置分享
          message = "GC08;" + lat.getText().toString() + ";" + lon.getText().toString();
      }
    
    /*透過findViewById建構子來建構EditText1,EditText2與Button物件*/ 
    phoneText = (EditText) findViewById(R.id.Phone_text); 
    selectContact = (Button) findViewById(R.id.sms_selectContact); 
    send_Button = (Button) findViewById(R.id.Send_Button);
    end_Button = (Button) findViewById(R.id.End_Button);

    
    /*將預設文字載入EditText中*/
    if (intent.getIntExtra("SMS_Action",9)==3){
    	phoneText.setText(intent.getStringExtra("Phone"));
    	phoneText.setInputType(0);
    	selectContact.setVisibility(8);
    }else{
    	//phoneText.setText("請輸入電話號碼");
    	phoneText.setInputType(3);
    }
   
    
    /*設定onClickListener 讓使用者點選EditText時做出反應*/   
    phoneText.setOnClickListener(new EditText.OnClickListener()
    {
      public void onClick(View v)
      {
        /*點選EditText時清空內文*/
    	  if (intent.getIntExtra("SMS_Action",9)==3){
    		  //do_nothing;
    	  }
    	  else{
    		  phoneText.setText("");
    		  }
      }
    }
    );
    /*選擇連絡人Button OnClickListener*/
    selectContact.setOnClickListener(new EditText.OnClickListener()
    {
      public void onClick(View v)
      {
    	  /*點選EditText時清空內文*/
    	  Uri uri = Uri.parse("content://contacts/people"); 
          Intent intent = new Intent(Intent.ACTION_PICK, uri);
          /*去擷取mTextView3裡的內容*/
          strMessage = "position";//mTextView3.getText().toString();          
          startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY); 
      }
    }
    );
    
    /*設定onClickListener 讓使用者點選EditText時做出反應*/
    name.setOnClickListener(new EditText.OnClickListener()
    {
      public void onClick(View v)
      {
        /*點選EditText時清空內文*/
       // mEditText2.setText("");
      }
    }
    );
     
    /*設定"送出Button" onClickListener 讓使用者點選Button時做出反應*/
    send_Button.setOnClickListener(new Button.OnClickListener() 
    { 
      @Override 
      public void onClick(View v) 
      { 
        /*由EditText1取得簡訊收件人電話*/
        String strDestAddress = phoneText.getText().toString(); 
        /*由EditText2取得簡訊文字內容*/
        //String strMessage ="irdc "+name.getText().toString()+"\n"+"Lon: "+lon.getText().toString()+" ,lat: "+lat.getText().toString();
        //String strMessage ="朋友 "+name.getText().toString()+"\n"+"Lon: "+lon.getText().toString()+" ,lat: "+lat.getText().toString();
        String strMessage = message;
        /*建構一取得default instance的 SmsManager物件 */
        SmsManager smsManager = SmsManager.getDefault(); 
      
        // TODO Auto-generated method stub 
        /*檢查收件人電話格式與簡訊字數是否超過70字元*/
        if(isPhoneNumberValid(strDestAddress)==true && iswithin70(strMessage)==true)
        {
        try 
          { 
          /*兩個條件都檢查通過的情況下,發送簡訊
           * 先建構一PendingIntent物件並使用getBroadcast()方法進行Broadcast
           * 將PendingIntent,電話,簡訊文字等參數傳入sendTextMessage()方法發送簡訊*/
             PendingIntent mPI = PendingIntent.getBroadcast(SMS.this, 0, new Intent(), 0); 
             smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null); 
          } 
        catch(Exception e) 
          { 
          e.printStackTrace(); 
          } 
          Toast.makeText(SMS.this,
            "送出成功!!" , 
            Toast.LENGTH_SHORT).show();
          phoneText.setText("請輸入電話號碼");
          //name.setText("");
        
        }
        /*電話格式與簡訊文字不符合條件時,使用Toast告知使用者檢查*/
        else 
        { /*電話格式不符*/
          if (isPhoneNumberValid(strDestAddress)==false)
          { /*且字數超過70字元*/
            if(iswithin70(strMessage)==false)
            {
              Toast.makeText(SMS.this, 
                "電話號碼格式錯誤+簡訊內容超過70字,請檢查!!", 
                  Toast.LENGTH_SHORT).show();
            }
            else
            {
            Toast.makeText(SMS.this,
                "電話號碼格式錯誤,請檢查!!" , 
                Toast.LENGTH_SHORT).show();
            }
          }
          /*字數超過70字元*/
          else if (iswithin70(strMessage)==false)
          {
            Toast.makeText(SMS.this, 
                "簡訊內容超過70字,請刪除部分內容!!", 
                Toast.LENGTH_SHORT).show();
          }
        }
      }
    }); 
    
    end_Button.setOnClickListener(new EditText.OnClickListener()
    {
      public void onClick(View v)
      {
         finish();
      }
    }
    );
    

	gohome = (Button)findViewById(R.id.go_home);
	gohome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_FIRST_USER);
				finish();
				return;
    		}
		});
     
  }
  
  @SuppressWarnings("deprecation")
  @Override 
  protected void onActivityResult 
  (int requestCode, int resultCode, Intent data) 
  {  // TODO Auto-generated method stub 
    /*如果沒加resultCode==RESULT_CANCELED,
	 *按back鍵會造成199行有ExceptionNullPoint
	 */  
   if(!(resultCode==RESULT_CANCELED)){
   
    switch (requestCode) 
    {  
      case PICK_CONTACT_SUBACTIVITY: 
        final Uri uriRet = data.getData(); 
        if(uriRet != null) 
        { 
          try 
          { 
            /* 必須要有android.permission.READ_CONTACTS權限 */ 
            Cursor c = managedQuery(uriRet, null, null, null, null); 
            c.moveToFirst(); 
            /*抓取通訊錄的姓名*/
            String strName =  
            c.getString(c.getColumnIndexOrThrow(People.NAME)); 
            /*抓取通訊錄的電話*/
            String strPhone =  
            c.getString(c.getColumnIndexOrThrow(People.NUMBER));
            phoneText.setText(strPhone);
            
            /*設定要寄給通訊錄裡的電話*/
           // String strDestAddress = strPhone; 
           // System.out.println(strMessage);
           // SmsManager smsManager = SmsManager.getDefault();
            
           // PendingIntent mPI = PendingIntent.getBroadcast(SMS.
           //     this, 0, new Intent(), 0); 
            /*寄出簡訊*/
           // smsManager.sendTextMessage(strDestAddress, null, 
            //    strMessage, mPI, null); 
            /*用Toast顯示傳送中*/
           // Toast.makeText(SMS.this, getString(R.string.str_msg)+
           //     strName,Toast.LENGTH_SHORT).show();
             
          // mTextView01.setText(strName+":"+strPhone); 
          } 
          catch(Exception e) 
          {             
            //mTextView01.setText(e.toString()); 
            e.printStackTrace(); 
          } 
        } 
        break; 
     }   
    } 
   super.onActivityResult(requestCode, resultCode, data);    
  } 
  
  /*檢查字串是否為電話號碼的方法,並回傳true or false的判斷值*/
  public static boolean isPhoneNumberValid(String phoneNumber)
  {
     boolean isValid = false;
     /* 可接受的電話格式有:
      * ^\\(? : 可以使用 "(" 作為開頭
      * (\\d{3}): 緊接著三個數字
      * \\)? : 可以使用")"接續
      * [- ]? : 在上述格式後可以使用具選擇性的 "-".
      * (\\d{3}) : 再緊接著三個數字
      * [- ]? : 可以使用具選擇性的 "-" 接續.
      * (\\d{4})$: 以四個數字結束.
      * 可以比對下列數字格式:
      * (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890  
     */
     String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
     /* 可接受的電話格式有:
      * ^\\(? : 可以使用 "(" 作為開頭
      * (\\d{2}): 緊接著兩個數字
      * \\)? : 可以使用")"接續
      * [- ]? : 在上述格式後可以使用具選擇性的 "-".
      * (\\d{4}) : 再緊接著四個數字
      * [- ]? : 可以使用具選擇性的 "-" 接續.
      * (\\d{4})$: 以四個數字結束.
      * 可以比對下列數字格式:
      * (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890  
     */
     String expression2 ="^\\(?(\\d{2})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
     CharSequence inputStr = phoneNumber;
     /*建立Pattern*/
     Pattern pattern = Pattern.compile(expression);
     /*將Pattern 以參數傳入Matcher作Regular expression*/ 
     Matcher matcher = pattern.matcher(inputStr);
     /*建立Pattern2*/
     Pattern pattern2 =Pattern.compile(expression2);
     /*將Pattern2 以參數傳入Matcher2作Regular expression*/ 
     Matcher matcher2= pattern2.matcher(inputStr);
     if(matcher.matches()||matcher2.matches())
     {
     isValid = true;
     }
     return isValid; 
   }
  public static boolean iswithin70(String text)
  {
    if (text.length()<= 70)
      return true;
    else
      return false;
  }
}
