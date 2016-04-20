package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.kingwaytek.cpami.bykingTablet.R;
import com.sonavtek.sonav.MapView;
import com.sonavtek.sonav.sonav;

public class ShowDialog extends Activity

{

	// Bundle mBundle01 =this.getIntent().getExtras();
	String strParam1 = "";
	private String GEO_SMS_PREFIX = "GeoSMS/";
	private String GEOSms_Lat = "", GEOSms_Lon = "";
	private String[] SaveSplitMessage = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.startup);
		
		sonav engine = sonav.getInstance();
		int enginState=-1;
		if (engine.getState() == sonav.STATE_UNINIT){
			enginState=0;
		}else{
			int mapmode=engine.getMapEventHandler().getMapView().getControlMode();
			switch (mapmode){
			case MapView.STATE_NAVI:
			case MapView.STATE_NAVI_PAUSE:
			case MapView.STATE_EMU:
			case MapView.STATE_EMU_PAUSE:
				enginState=2;
				this.finish();
				break;
			case MapView.STATE_MAP:
			default:
				enginState=1;
			}
		}			
		
		String str = "";

		/* 建立Bundle物件，判斷是否有傳入封裝參數 */
		final Bundle mBundle01 = this.getIntent().getExtras();
		strParam1 = mBundle01.getString("STR_PARAM01");
//		Pattern pat = Pattern.compile("[<>\n\u0020]");// 以'<','>',換行,空白,做切分
//		final String[] SaveMessage = pat.split(strParam1);
		final String[] SaveMessage = strParam1.split(HippoCustomIntentReceiver.strDelimiter1);
	
		if(SaveMessage[1].startsWith(GEO_SMS_PREFIX)){
			handleOpenGeoSMS(SaveMessage[1]);
			SaveSplitMessage[1]=GEOSms_Lat;
			SaveSplitMessage[2]=GEOSms_Lon;
		}else{
			SaveSplitMessage = SaveMessage[1].split(";");
		}

		if (mBundle01.getInt("request_location") == 1) {
			str = "收到"+SaveMessage[0]+"的簡訊想查詢你的位置,是否開啟單車ing";
		} else if (mBundle01.getInt("share_location") == 1) {
			str = "收到來自朋友"+SaveMessage[0]+"分享的位置,是否開啟單車ing";
		}

		if (enginState==0) {
			
			// 軟體還未開啟
			AlertDialog.Builder builder = new AlertDialog.Builder(ShowDialog.this);
			// builder.setMessage("Do you want to open? ");
			builder.setMessage(str);
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(ShowDialog.this, StartupActivity.class);

							if (SaveMessage.length > 0) {
								/* 重新封裝參數（SMS訊息）回傳 */
								// DialogIntent.putExtra("STR_PARAM01",strParam1
								// );
								if (mBundle01.getInt("request_location") == 1) {
									intent.putExtra("SMS_Action", 3);
                                }
                                else {
//								intent.putExtra("SMS", 1);
//								intent.putExtra("Phone", SaveMessage[0]);
//								intent.putExtra("name", SaveMessage[4]
//										.toString().trim());
//								intent.putExtra("Lon", Double
//										.parseDouble(SaveMessage[6]));
//								intent.putExtra("Lat", Double
//										.parseDouble(SaveMessage[8]));
							   							
								intent.putExtra("Lon", Double
										.parseDouble(SaveSplitMessage[2]));
								intent.putExtra("Lat", Double
										.parseDouble(SaveSplitMessage[1]));
								if(SaveSplitMessage.length > 3){
								      intent.putExtra("name", SaveSplitMessage[3]
															.toString().trim());
								      }
													 
								}	
								if (SaveMessage[0].startsWith("+886"))
									SaveMessage[0] = "0"+ SaveMessage[0].substring(4);
								Log.i("showDialog.java","SaveMessage[0]="+SaveMessage[0] );
								
								intent.putExtra("Phone", SaveMessage[0]);
								intent.putExtra("SMS", 1);
							}	
	
							startActivity(intent);
							finish();

						}
					});

			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();

						}
					});

			AlertDialog alert = builder.create();
			alert.show();

		} else if (enginState==1 || enginState==2)
			{
			Log.i("ShowDialog.java","");
			// 軟體已開啟
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ShowDialog.this);
				// builder.setMessage("Do you want to open? ");
				builder.setMessage(str);
				builder.setCancelable(false);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent();
								Bundle bundle = new Bundle();
								intent.putExtras(bundle);
								if (SaveMessage.length > 0) {
									/* 重新封裝參數（SMS訊息）回傳 */
						
									if (SaveMessage[0].startsWith("+886"))
										SaveMessage[0] = "0" + SaveMessage[0].substring(4);
									
									intent.putExtra("Phone", SaveMessage[0]);
									
								   if (mBundle01.getInt("request_location") != 1) {
									  intent.putExtra("Lon", Double.parseDouble(SaveSplitMessage[2]));
									  intent.putExtra("Lat", Double.parseDouble(SaveSplitMessage[1]));

									  if (SaveSplitMessage.length > 3)
									      intent.putExtra("name", SaveSplitMessage[3].trim());
									}

								}
								if (mBundle01.getInt("request_location") == 1) {
									intent.putExtra("SMS_Action", 3);
									intent.setClass(ShowDialog.this, SMS.class);
								}
                                else if (mBundle01.getInt("share_location") == 1) {
									intent.setClass(ShowDialog.this, SMSMapContent.class);
								}								
								startActivity(intent);
								finish();
							}
						});
				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
	}	
	
	 /*判斷是否是GeoSms格式的簡訊 */
	  private void handleOpenGeoSMS(String data) {
			String poiName, poiTel = "", poiAddr = "";

			// Parse strings
			String[] substr = data.split(";");

			if (substr.length < 4)
				return;

			if (substr[3].matches("A") || substr[3].matches("E")) {
				poiName = "";
				GEOSms_Lat = String.valueOf(WGS84ToCoordinate(substr[1]));
				GEOSms_Lon = String.valueOf(WGS84ToCoordinate(substr[2]));
			} else if (substr[3].matches("P")) {
				if (substr.length < 5)
					return;

				GEOSms_Lat = String.valueOf(WGS84ToCoordinate(substr[1]));
				GEOSms_Lon = String.valueOf(WGS84ToCoordinate(substr[2]));

				String[] substr2 = substr[4].split("/");

				poiName = substr2[0];

				if (substr2.length >= 2) {
					poiTel = substr2[1];
					poiTel = poiTel.replaceAll("-", "");
				}

				if (substr2.length >= 3) {
					poiAddr = substr2[2];
				}
			} else {
				return;
			}

			float lat, lon;
			try {
				lat = Float.parseFloat(GEOSms_Lat);
			} catch (NumberFormatException e) {
				lat = 0.0f;
			}

			try {
				lon = Float.parseFloat(GEOSms_Lon);
			} catch (NumberFormatException e) {
				lon = 0.0f;
			}


		}
		
		private float WGS84ToCoordinate(String LocStr) {
			int pchr = LocStr.indexOf(',');

			if (pchr == -1)
				return 0;

			String Direct = LocStr.substring(pchr + 1);
			String Loc = LocStr.substring(0, pchr);
			float WGS84 = Float.parseFloat(Loc);
			int D = (int) (WGS84 / 100);
			float M = WGS84 - D * 100;

			float res = D + (float) M / 60;

			if (Direct.matches("S") || Direct.matches("W"))
				res = 0 - res;

			return res;
		}
}
