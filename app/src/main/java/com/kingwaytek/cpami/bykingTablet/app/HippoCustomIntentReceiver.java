package com.kingwaytek.cpami.bykingTablet.app;



import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import com.kingwaytek.model.SmsFileData;
//import com.kingwaytek.sms.SMSFileDBAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;



/* 自訂繼承自BroadcastReceiver類別，聆聽自訂系統服務廣播的訊息 */
public class HippoCustomIntentReceiver extends BroadcastReceiver
{
  /* 自訂欲作為Intent Filter的ACTION訊息 */
  public static final String HIPPO_SERVICE_IDENTIFIER = "HIPPO_ON_SERVICE_001";
  private static final String HIPPO_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
  public static String strDelimiter1="<delimiter1>";
  //private static String strSecretWord="IRDC";
//  private static String strSecretWord1="朋友";
//  private static String strSecretWord2="查詢";
  private String strSecretWord1="GC08";//一般位置分享
  private String strSecretWord2="GC02";//查詢位置
  private String strSecretWord3="GC01";//poi分享
  private String GEO_SMS_PREFIX = "GeoSMS/";
  private String[] aryTemp01 = null;

  @Override
  public void onReceive(Context context, Intent intent)
  {
   
    
    
    // TODO Auto-generated method stub
    if(intent.getAction().toString().equals(HIPPO_SMS_ACTION ))
    {
       	
    	/* 以Bundle物件解開傳來的參數 */
      StringBuilder sb = new StringBuilder();
      Bundle mBundle01 = intent.getExtras();
      String strParam1="";
      
      /* 若Bundle物件不為空值，取出參數 */
      if (mBundle01 != null)
      {
    	  /* 將取出的STR_PARAM01參數，存放於strParam1字串中 */
         strParam1 = mBundle01.getString("STR_PARAM01");
         /* 拆解與識別SMS簡訊 */
        Object[] myOBJpdus = (Object[]) mBundle01.get("pdus");
        SmsMessage[] messages = new SmsMessage[myOBJpdus.length]; 
        for (int i = 0; i<myOBJpdus.length; i++)
        { 
          messages[i] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[i]); 
        }
            
        /* 將送來的簡訊合併自訂訊息於StringBuilder當中 */ 
        for (SmsMessage currentMessage : messages)
        {
          sb.append(currentMessage.getDisplayOriginatingAddress());
          /* 在電話與SMS簡訊BODY之間，加上分隔TAG */
          sb.append(strDelimiter1);
          sb.append(currentMessage.getDisplayMessageBody());
          strParam1=sb.toString();
          Log.i("HippoCustomIntentRececiver.java"," strParam1="+ strParam1);
        }
      
        //檢查是否有自訂的TAG
        if(eregi(strDelimiter1,strParam1))
        {
          aryTemp01 = strParam1.split(strDelimiter1);
        //檢查是否有關鍵字
          if((eregi(strSecretWord1,aryTemp01[1])||eregi(strSecretWord3,aryTemp01[1])||eregi(GEO_SMS_PREFIX,aryTemp01[1]))&& aryTemp01.length==2)
          { 
            Intent mRunPackageIntent = new Intent(context, ShowDialog.class); 
            mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(strParam1!="")
            {
            	/* 重新封裝參數（SMS訊息）回傳 */
              mRunPackageIntent.putExtra("share_location",1 );
              mRunPackageIntent.putExtra("STR_PARAM01",strParam1 );
            }
//            else
//            {
//              mRunPackageIntent.putExtra("STR_PARAM01", "From Service notification...");
//            }
            context.startActivity(mRunPackageIntent);
            
            
          }else if(eregi(strSecretWord2,aryTemp01[1]) && aryTemp01.length==2) {
        	  Intent mRunPackageIntent = new Intent(context, ShowDialog.class); 
              mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              if(strParam1!="")
              {
              	/* 重新封裝參數（SMS訊息）回傳 */
      
            	  mRunPackageIntent.putExtra("request_location",1 );
                mRunPackageIntent.putExtra("STR_PARAM01",strParam1 );
              }
              context.startActivity(mRunPackageIntent);
        	  
           }else if(eregi(GEO_SMS_PREFIX,aryTemp01[1])&& aryTemp01.length==2){
        	  
           }
         }
       }
     }
  } 
  
  /* 判斷接收的簡訊是否為有關鍵字的簡訊 */
  public static boolean isCommandSMS(String strPat, String strSMS)
  {
    String strPattern = "(?i)"+strPat;
    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strSMS);
    return m.find();
  }
  
  /* 自訂正規表達式，無分大小寫比對字串 */
  public static boolean eregi(String strPat, String strUnknow)
  {
	  /* 方法一 */
    String strPattern = "(?i)"+strPat;
    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strUnknow);
    return m.find();
    
    /* 方法二 */
    /*
    if(strUnknow.toLowerCase().indexOf(strPat.toLowerCase())>=0)
    {
      return true;
    }
    else
    {
      return false;
    }
    */
  }
  
  /* 判斷簡訊發送者的來電，是否為台灣行動電話格式 */
  public static boolean isTWCellPhone(String strUnknow)
  {
    /*
     * (0935)456-789, 0935-456-789, 1234567890, (0935)-456-789
     * */
    String strPattern = "^\\(?(\\d{4})\\)?[-]?(\\d{3})[-]?(\\d{3})$";
    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strUnknow);
    return m.matches();
  }
  
  /* 判斷簡訊發送者的來電，是否為美國行動電話格式 */
  public static boolean isUSCellPhone(String strUnknow)
  {
    /*
     * (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
     * */
    String strPattern = "^\\(?(\\d{3})\\)?[-]?(\\d{3})[-]?(\\d{4})$";
    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strUnknow);
    return m.matches();
  }
 

}
