package com.kingwaytek.cpami.bykingTablet.bus;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.actionsheet.ActionSheet;
import com.example.actionsheet.ActionSheet.ActionSheetButtonClickListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CreateMD5Code;
import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.app.MyFavorite;
import com.kingwaytek.cpami.bykingTablet.app.MyHistory;
import com.kingwaytek.cpami.bykingTablet.app.address.CitySelection;
import com.kingwaytek.cpami.bykingTablet.app.poi.POIMethodSelection;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ActivityCaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class PublicTransport extends Activity {
	private static TextView TextStartPoint;
	private static TextView TextEndtPoint;
	private double StartPointLon, StartPointLat;
	private double EndPointLon, EndPointLat;
	private int StartLonhttp, StartLathttp, EndLonhttp, EndLathttp;
	private final int NoStartPointFlag = 1;
	private final int NoEndPointFlag = 2;
	private final int NoStartEndPointFlag = 3;
	private final String NoStartPointMsg = "請設定起點";
	private final String NoEndPointMsg = "請設定終點";
	private final String NoStartEndPointMsg = "請設定起點,終點";
	private String StartPointName;
	private String EndPointName;
	private Button gohome;
	// private ProgressDialog InternetProgressDialog = null;
	private UtilDialog progressDialog;
	private Handler InternetMsgHandler;
	private final int GET_INFOMATION_FINISH = 0;
	private final int PROGRESS_DIALOG = 1;
	private final int DOWNLOAD_FINISH = 3;
	private final int NO_DATA = 4;
	private final int INTERNET_ERROR = 5;
	private StringBuilder URLstrbuilder;
	private String result;
	private boolean isFinish = false;

	private ActionSheet actionSheet;
	private ImageView actionsheet_btn;
	private int[][] sub_view;
	private int WhichButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_transport);
		setRoutePointButtonListner();
		setActionSheet();

		progressDialog = new UtilDialog(this);
		/*
		 * TextView textView =(TextView)findViewById(R.id.transport_text);
		 * 
		 * JSONArray jsonArray;// =new JSONArray(); JSONArray jsonArray2;
		 * JSONArray jsonArray3; String result=""; String resultUTF8=""; byte[]
		 * bytesGBK=null; try { response = cliente.execute(httpget);
		 * 
		 * HttpEntity entity = response.getEntity(); if (entity != null) {
		 * 
		 * // A Simple JSON Response Read
		 * 
		 * InputStream instream = entity.getContent(); result=
		 * convertStreamToString(instream); //
		 * resultUTF8=URLEncoder.encode(result,"UTF-8"); //
		 * Log.i("PublicTransport.java","result="+result);
		 * 
		 * 
		 * // jsonArray =new JSONArray(
		 * "[{\"result\": 2,\"start_points\": [{\"name\": \"捷運古亭站\",\"type\":\"[捷運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站\",\"type\":\"[客運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站\",\"type\":\"[公車站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321}],\"end_points\": [{\"name\": \"捷運古亭站 back\",\"type\":\"[捷運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站 back\",\"type\":\"[客運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站 back\",\"type\":\"[公車站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321}]}]"
		 * );
		 * 
		 * // // Log.i("PublicTransport.java", "pub region: " +
		 * obj.getString("region")); // for(int i=0;i==count;i++){ // JSONObject
		 * obj = jsonArray.getJSONObject(i); // // Log.i("PublicTransport.java",
		 * "pub name: " + obj.getString("name")); //
		 * Log.i("PublicTransport.java", "pub type: " + obj.getString("type"));
		 * // Log.i("PublicTransport.java", "pub region: " +
		 * obj.getString("region")); // // } instream.close();
		 * 
		 * }
		 * 
		 * 
		 * } catch (Exception e) { // TODO: handle exception
		 * e.printStackTrace(); }
		 * 
		 * try {
		 * 
		 * bytesGBK = result.getBytes("GBK"); } catch
		 * (java.io.UnsupportedEncodingException e)
		 * 
		 * { e.printStackTrace(); }
		 * 
		 * try{
		 * 
		 * 
		 * //resultUTF8=URLEncoder.encode(result,"UTF-8");
		 * //Log.i("PublicTransport.java","result="+result); String strings =
		 * new String(bytesGBK ,"UTF-8").substring(1);
		 * Log.i("PublicTransport.java","resultUGBK="+strings);
		 * 
		 * // jsonArray =new JSONArray(new String(bytesGBK ,"GBK").toString());
		 * 
		 * textView.setText(result); //textView.setText(result);
		 * 
		 * 
		 * //jsonArray =new JSONArray(
		 * "[{\"result\": 2,\"start_points\": [{\"name\": \"捷運古亭站\",\"type\":\"[捷運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站\",\"type\":\"[客運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站\",\"type\":\"[公車站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321}],\"end_points\": [{\"name\": \"捷運古亭站 back\",\"type\":\"[捷運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站 back\",\"type\":\"[客運站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321},{\"name\": \"捷運古亭站 back\",\"type\":\"[公車站]\",\"region\": \"台北市/大安區\",\"lat\":25.123456,\"lon\":122.654321}]}]"
		 * );
		 * 
		 * // String str =jsonArray.toString() ; //
		 * Log.i("PublicTransportaa.java","str="+str); // int count =
		 * jsonArray.length(); // Log.i("PublicTransport.java","count=="+count);
		 * // JSONObject obj = jsonArray.getJSONObject(0); //
		 * Log.i("PublicTransport.java", "pub name: " ); //
		 * Log.i("PublicTransport.java", "pub result: " +
		 * obj.getString("result")); // Log.i("PublicTransport.java",
		 * "pub routes:" + obj.getString("routes")); // jsonArray2 =new
		 * JSONArray(obj.getString("routes")); // JSONObject obj2 =
		 * jsonArray2.getJSONObject(0); // Log.i("PublicTransport.java",
		 * "pub lines: " + obj2.getString("lines")); //
		 * Log.i("PublicTransport.java", "pub id:" + obj2.getString("id"));
		 * //Log.i("PublicTransport.java", "pub name:" +
		 * obj2.getString("name")); // jsonArray3 =new
		 * JSONArray(obj2.getString("lines")); // JSONObject obj3 =
		 * jsonArray3.getJSONObject(0); // Log.i("PublicTransport.java",
		 * "pub schedule: " + obj3.getString("schedule")); //
		 * Log.i("PublicTransport.java", "pub id:" + obj2.getString("id")); //
		 * Log.i("PublicTransport.java", "pub name:" + obj3.getString("name"));
		 * //textView.setText(URLEncoder.encode(obj3.getString("name"),"big5"));
		 * }catch (Exception e) { // TODO: handle exception e.printStackTrace();
		 * }
		 */

	}

	public void setActionSheet() {
		sub_view = new int[6][2];
		sub_view[0][0] = R.id.actionsheet_trans01;
		sub_view[1][0] = R.id.actionsheet_trans02;
		sub_view[2][0] = R.id.actionsheet_trans03;
		sub_view[3][0] = R.id.actionsheet_trans04;
		sub_view[4][0] = R.id.actionsheet_trans05;
		sub_view[5][0] = R.id.actionsheet_trans06;
		actionSheet = (ActionSheet) findViewById(R.id.actionSheet_trans);
		actionSheet.setContext(PublicTransport.this);
		actionSheet.setActionSheetLayout(R.layout.action_sheet_trans, sub_view);
		actionSheet.setOnActionSheetButtonClickListener(new ActionSheetButtonClickListener() {

			@Override
			public void onButtonClick(ActionSheet actionsheet, int index, int id) {
				selectSetPositiontWay(index, WhichButton);
			}
		});
	}

	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		StartPointName = savedState.getString("StartPoint");
		EndPointName = savedState.getString("EndPoint");
		TextStartPoint.setText(savedState.getString("StartPoint"));
		TextEndtPoint.setText(savedState.getString("EndPoint"));
		StartPointLon = savedState.getDouble("Start_Lon");
		StartPointLat = savedState.getDouble("Start_Lat");
		EndPointLon = savedState.getDouble("End_Lon");
		EndPointLat = savedState.getDouble("End_Lat");
		StartLonhttp = savedState.getInt("Start_Lon_hppt");
		StartLathttp = savedState.getInt("Start_Lat_hppt");
		EndLonhttp = savedState.getInt("End_Lon_hppt");
		EndLathttp = savedState.getInt("End_Lat_hppt");
		Log.i("PublicTransport.java", "EndLathttp in Restore=" + EndLathttp);
	}

	protected void onSaveInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		savedState.putString("StartPoint", StartPointName);
		savedState.putString("EndPoint", EndPointName);
		savedState.putDouble("Start_Lon", StartPointLon);
		savedState.putDouble("Start_Lat", StartPointLat);
		savedState.putDouble("End_Lon", EndPointLon);
		savedState.putDouble("End_Lat", EndPointLat);
		savedState.putInt("Start_Lon_hppt", StartLonhttp);
		savedState.putInt("Start_Lat_hppt", StartLathttp);
		savedState.putInt("End_Lon_hppt", EndLonhttp);
		savedState.putInt("End_Lat_hppt", EndLathttp);
	}

	private void setRoutePointButtonListner() {
		TextStartPoint = (TextView) findViewById(R.id.start_point_transit_textView);
		TextEndtPoint = (TextView) findViewById(R.id.end_point_transit_textview);
		Button StartButtton = (Button) findViewById(R.id.start_point_transit_button);
		Button EndButtton = (Button) findViewById(R.id.end_point_transit_botton);
		Button StartTransitPlan = (Button) findViewById(R.id.start_transit_botton);
		// Button CancelButton = (Button)
		// findViewById(R.id.cancel_transit_Button);

		StartButtton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.i("MapActivity.java","startButtton onClick");
				// setLocationDialog(MapActivity.START_Button);
				WhichButton = MapActivity.START_Button;
				actionSheet.show();
				return;
			}
		});

		EndButtton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// setLocationDialog(MapActivity.END_Button);
				WhichButton = MapActivity.END_Button;
				actionSheet.show();
				return;
			}
		});

		StartTransitPlan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String strMsg = "";
				if (checkPointSetting() != 0) {
					switch (checkPointSetting()) {
					case NoStartPointFlag:
						strMsg = NoStartPointMsg;
						break;
					case NoEndPointFlag:
						strMsg = NoEndPointMsg;
						break;
					case NoStartEndPointFlag:
						strMsg = NoStartEndPointMsg;
						break;

					default:
						break;
					}

					UtilDialog uit = new UtilDialog(PublicTransport.this);
					uit.showDialog_route_plan_choice(strMsg, null, "確定", null);
				} else {
					URLstrbuilder = new StringBuilder();
					String TrackPointString = null;
					Date date = new Date();
					String MD5Code = CreateMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
							* (1103 + date.getDate())) + "Kingway").getBytes());
					// URLstrbuilder.append("http://192.168.1.186:8080/BikeGo/RoutePath?startp=");
					// URLstrbuilder.append("http://59.120.150.54:8081/BikeGo/RoutePath?startp=");
					URLstrbuilder.append("http://biking.cpami.gov.tw/Service/RoutePath?startp=");
					URLstrbuilder.append(StartLathttp);
					URLstrbuilder.append(",");
					URLstrbuilder.append(StartLonhttp);
					URLstrbuilder.append("&endp=");
					URLstrbuilder.append(EndLathttp);
					URLstrbuilder.append(",");
					URLstrbuilder.append(EndLonhttp);
					URLstrbuilder.append("&");
					// URLstrbuilder.append("code=182F58B1688B3D166E78A448B7A5CA3E");
					URLstrbuilder.append("code=" + MD5Code);
					Log.i("PublicTransport.java", "URL=" + URLstrbuilder.toString());
					showProgressDialog();
					getInfoThread();
					while (isFinish == false) {
						// do nothing and wait
					}
					isFinish = false;
					TrackPointString = result;// connectWeb(URLstrbuilder.toString());
					Log.i("PublicTransport.java", "TrackPointString=" + TrackPointString);
					if ((TrackPointString == null || TrackPointString.equalsIgnoreCase("null"))
							&& TrackPointString != "interner error") {

						UtilDialog uit = new UtilDialog(PublicTransport.this);
						uit.showDialog_route_plan_choice("無法取得資料", null, "確定", null);

					} else if (TrackPointString == "interner error") {

						UtilDialog uit = new UtilDialog(PublicTransport.this);
						uit.showDialog_route_plan_choice(getString(R.string.dialog_web_message3), null,
								getString(R.string.dialog_ok_button_text), null);

					} else {
						int RouteSize = PublicTransportList.getRouteSize(TrackPointString);
						// Intent intent = new Intent(PublicTransport.this,
						// PublicTransportList.class);
						Intent intent = new Intent(PublicTransport.this, PublicTransportTable.class);
						// Log.i("PublicTransit.java","StartLonhttp="+StartLonhttp);
						// Log.i("PublicTransit.java","strbuilder.toString()="+strbuilder.toString());
						// intent.putExtra("TransitURL",strbuilder.toString());
						intent.putExtra("TrackPointString", TrackPointString);
						intent.putExtra("RouteSize", RouteSize);
						startActivity(intent);
					}
				}
			}
		});

	}

	private void selectSetPositiontWay(int which, int whichButtonSrc) {
		String[] getWitchButton = { "startButton", "ess1Button", "ess2Button", "endButton" };

		switch (which) {
		case 0:
			Intent AddressIntent = new Intent(PublicTransport.this, CitySelection.class);
			AddressIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
			startActivityForResult(AddressIntent, ActivityCaller.ADDRESS.getValue());
			break;
		case 1:
			Intent POIIntent = new Intent(PublicTransport.this, POIMethodSelection.class);
			POIIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
			startActivityForResult(POIIntent, ActivityCaller.POI.getValue());
			break;
		case 2:
			Intent POIViewIntent = new Intent(PublicTransport.this, MyHistory.class);
			POIViewIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
			startActivityForResult(POIViewIntent, ActivityCaller.HISTORY.getValue());
			break;
		case 3:
			Intent FavoriteViewIntent = new Intent(PublicTransport.this, MyFavorite.class);
			FavoriteViewIntent.putExtra("setpoint", getWitchButton[whichButtonSrc]);
			startActivityForResult(FavoriteViewIntent, ActivityCaller.FAVORITE.getValue());
			break;
		case 4: // 取消設定
			switch (whichButtonSrc) {
			case MapActivity.START_POINT:
				TextStartPoint.setText("請選擇起點");
				StartPointLon = 0.0;
				StartPointLat = 0.0;
				break;
			case MapActivity.END_POINT:
				TextEndtPoint.setText("請選擇終點");
				EndPointLon = 0.0;
				EndPointLat = 0.0;
				break;
			default:
				break;
			}
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			int SateFlag = MapActivity.CheckFlagForPublicTransit();
			// ActivityCaller Handler
			switch (ActivityCaller.get(requestCode)) {
			case ADDRESS:
			case POI:
			case SPOI:
			case FAVORITE:
			case HISTORY:
				setPoint(SateFlag);
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		}
	}

	private void setPoint(int SateFlag) {
		Log.i("PublicTransit.java", "SateFlag=" + SateFlag);
		if (SateFlag == MapActivity.START_POINT) {
			Log.i("PublicTransit.java", "gSateFlag  == MapActivity.START_POINT");
			StartPointLon = MapActivity.getStartArray()[0];
			StartPointLat = MapActivity.getStartArray()[1];
			// Log.i("PublicTransit.java","gStartPointLon="+StartPointLon);
			// Log.i("PublicTransit.java","EndPointLon="+StartPointLat);
			StartLonhttp = FloatConvertPositive(StartPointLon);
			StartLathttp = FloatConvertPositive(StartPointLat);
			// TextStartPoint.setText("Lon:"+FloatConvertPositive(StartPointLon)+",Lat:"+FloatConvertPositive(StartPointLat));
			StartPointName = MapActivity.getName(SateFlag);
			TextStartPoint.setText(StartPointName);
		} else if (SateFlag == MapActivity.END_POINT) {
			Log.i("PublicTransit.java", "gSateFlag  == MapActivity.END_POINT");
			EndPointLon = MapActivity.getEndArray()[0];
			EndPointLat = MapActivity.getEndArray()[1];
			EndLonhttp = FloatConvertPositive(EndPointLon);
			EndLathttp = FloatConvertPositive(EndPointLat);
			// TextEndtPoint.setText("Lon:"+EndLonhttp+",Lat:"+EndLathttp);
			EndPointName = MapActivity.getName(SateFlag);
			TextEndtPoint.setText(EndPointName);
		}
	}

	public int FloatConvertPositive(double dou) {
		String a = String.valueOf(dou);
		String[] str = a.split("\\.");
		int X = str[1].length();
		Log.i("PublicTransit.java", "X=" + X);
		int positive = (int) (dou * getMultiple(X));
		if (X < 6) {
			positive = positive * getMultiple(6 - X);
		}
		return positive;
	}

	public int getMultiple(int FloatLength) {
		int Multiple = 0;
		switch (FloatLength) {
		case 0:
			Multiple = 0;
			break;
		case 1:
			Multiple = 10;
			break;
		case 2:
			Multiple = 100;
			break;
		case 3:
			Multiple = 1000;
			break;
		case 4:
			Multiple = 10000;
			break;
		case 5:
			Multiple = 100000;
			break;
		case 6:
			Multiple = 1000000;
			break;
		case 7:
			Multiple = 10000000;
			break;
		case 8:
			Multiple = 100000000;
			break;
		case 9:
			Multiple = 1000000000;
			break;
		default:
			Multiple = 100000000;
			break;
		}
		;
		return Multiple;
	}

	private String connectWeb(String URL) {
		HttpClient cliente = new DefaultHttpClient();
		HttpResponse response;
		HttpGet httpget = new HttpGet(URL);
		String result = null;
		try {
			response = cliente.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					result = convertStreamToString(instream);

					instream.close();
//					InternetProgressDialog.dismiss();
					progressDialog.dismiss();
				}
			} else {
//				InternetProgressDialog.dismiss();
				progressDialog.dismiss();
				result = "interner error";
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
//			InternetProgressDialog.dismiss();
			progressDialog.dismiss();
			result = "interner error";
		} catch (IOException e) {
			e.printStackTrace();
//			InternetProgressDialog.dismiss();
			progressDialog.dismiss();
			result = "interner error";
		} catch (Exception e) {
			e.printStackTrace();
//			InternetProgressDialog.dismiss();
			progressDialog.dismiss();
			result = "interner error";
		}
		return result;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				// sb.append(line + "\n");
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private String arrayToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buff.append(bytes[i] + " ");
		}
		return buff.toString();
	}

	private int checkPointSetting() {
		int flag = 0;
		if (StartPointLon == 0.0 && StartPointLat == 0.0) {
			if (EndPointLon == 0.0 && EndPointLat == 0.0) {
				flag = NoStartEndPointFlag;
			} else {
				flag = NoStartPointFlag;
			}
		} else if (EndPointLon == 0.0 && EndPointLat == 0.0) {
			flag = NoEndPointFlag;
		}
		return flag;
	}

	@Override
	protected void onPause() {
		Log.i("PublicTransport.java", "onPause");
		super.onPause();
	}

	private void getInfoThread() {
		new Thread() {
			public void run() {
				result = connectWeb(URLstrbuilder.toString());
				isFinish = true;
			}
		}.start();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			Log.i("TrackDownLoad.java", "onCreateDialog");
			return ProgressDialog.show(this, "", "Loading. Please wait...", true);
		default:
			return null;
		}
	}

	private void showProgressDialog() {
//		InternetProgressDialog = ProgressDialog.show(PublicTransport.this, "請稍候片刻", "正在取得資訊", true);
		progressDialog.progressDialog("請稍候片刻", "正在取得資訊");
	}

}
