package com.kingwaytek.cpami.bykingTablet.TrafficCondition;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CreatMD5Code;
import com.kingwaytek.cpami.bykingTablet.bus.PublicTransportList;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficUpdate extends ListActivity {
	private Handler updataHandler = null;
//	private ProgressDialog WaitDialog = null;
	private UtilDialog progressDialog;

	private final int GET_WEB_FINISH = 1;
	private final int GET_WEB_FAIL = 2;
	private String[] Title;
	private String[] Zone;
	private String[] Detail;
	private String[] CityID;
	private String[] Type;
	private String[] StartTime;
	private String[] EndTime;
	private String[] Lat;
	private String[] Lon;
	private String[] Authagent;
	private String[] Authtime;
	private String[] Reviewed;
	private String[] ReviewDetail;
	private String[] Source;
	private static String result = null;
	private int number;
	Map<String, Object> Area;
	private final int FAIL = 0;
	private final int SUCESS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.roadcondition_main);

		((TextView) this.findViewById(R.id.textView1)).setText("路況瀏覽");

		// initialHandler();
		// connect();
		progressDialog = new UtilDialog(this);
		progressDialog.progressDialog("請稍候片刻", "正在取得路況資訊");

//		WaitDialog = ProgressDialog.show(TrafficUpdate.this, "請稍候片刻",
//				"正在取得路況資訊", true);

		// Intent ReportIntent = new Intent(TrafficUpdate.this,
		// TrafficDetail.class);
		//
		// startActivity(ReportIntent);
	}

	public void onResume() {
		super.onResume();
		initialHandler();
		Area = new HashMap<String, Object>();
		Area.put("1", "台北市");
		Area.put("2", "新北市");
		Area.put("3", "基隆市");
		Area.put("4", "宜蘭縣");
		Area.put("5", "桃園縣");
		Area.put("6", "新竹市");
		Area.put("7", "新竹縣");
		Area.put("8", "苗栗縣");
		connect();

	}

	public void onPause() {
		super.onPause();
		Log.i("TrafficUpdata.java", "onPause");
	}

	private void connect() {
		new Thread() {
			@Override
			public void run() {
				try {

					// Internet Connect
					Date date = new Date();
					String MD5Code = CreatMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
							* (1207 + date.getDate())) + "Kingway").getBytes());

					String TrafficAlertUploadURL = getResources().getString(
							R.string.cpamiURL)
							+ "TrafficAlertList?AlertStartTime=201601010000&AlertEndTime=201612312359&Code=" + MD5Code;

					Log.i("TrafficUpdata.java", "TrafficAlertUpdataURL=" + TrafficAlertUploadURL);

					HttpClient cliente = new DefaultHttpClient();
					HttpResponse response;
					HttpPost httpPost = new HttpPost(TrafficAlertUploadURL);
					response = cliente.execute(httpPost);
					HttpEntity entity = response.getEntity();

					if (entity != null) {
						InputStream instream = entity.getContent();
						result = PublicTransportList.convertStreamToString(instream);
						instream.close();
					}
					Log.i("TrafficUpdate.java", "TrafficAlertUpload result=" + result);
//					WaitDialog.dismiss();
					progressDialog.dismiss();
					updataHandler.sendMessage(updataHandler.obtainMessage(GET_WEB_FINISH, String.valueOf(0)));
				}
                catch (Exception e) {
					e.printStackTrace();
//					WaitDialog.dismiss();
					progressDialog.dismiss();
					updataHandler.sendMessage(updataHandler.obtainMessage(GET_WEB_FAIL, "更新路況失敗"));
				}
			}
		}.start();
	}

	private void initialHandler() {
		updataHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == GET_WEB_FINISH) {
					int r = setListArrayData(result);
					if (r == SUCESS) {
						showList();
						// Toast.makeText(TrafficUpdate.this,"下載成功", 3000).show();
					}
                    else if (r == FAIL)
						Toast.makeText(TrafficUpdate.this, "更新路況失敗", Toast.LENGTH_LONG).show();
				}
                else if (msg.what == GET_WEB_FAIL)
					Toast.makeText(TrafficUpdate.this, "更新路況失敗", Toast.LENGTH_LONG).show();
			}
		};
	}

	public int setListArrayData(String source) {
		int result = SUCESS;
		// StringBuilder stringBuilder = new StringBuilder();
		number = getNumber(source);
		Title = new String[number];
		Zone = new String[number];
		Detail = new String[number];
		CityID = new String[number];
		Type = new String[number];
		StartTime = new String[number];
		EndTime = new String[number];
		Lat = new String[number];
		Lon = new String[number];
		Authagent = new String[number];
		Authtime = new String[number];
		Reviewed = new String[number];
		ReviewDetail = new String[number];
		Source = new String[number];

		JSONArray jaRoute = null;
		try {
			jaRoute = new JSONArray(source);
		} catch (Exception e) {
			e.printStackTrace();
			result = FAIL;
		}
		for (int i = 0; i < number; i++) {
			try {
				String ObjSource = jaRoute.get(i).toString();

				JSONObject jo = new JSONObject(ObjSource);
				Title[i] = jo.getString("title");
				Detail[i] = jo.getString("detail");
				CityID[i] = jo.getString("cityid");
				Zone[i] = jo.getString("town_name");
				Type[i] = jo.getString("type");
				StartTime[i] = jo.getString("starttime");
				EndTime[i] = jo.getString("endtime");
				Lat[i] = jo.getString("lat");
				Lon[i] = jo.getString("lon");
				Authagent[i] = jo.getString("authagent");
				Authtime[i] = jo.getString("authtime");
				Reviewed[i] = jo.getString("reviewed");
				ReviewDetail[i] = jo.getString("review_detail");
				Source[i] = jo.getString("report_source");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// stringBuilder.append("ResultID[i]="+Title[i]+"ResultName[i]="+Detail[i]+"CityID"+CityID[i]+"\n");
		}

		return result;
	}

	private int getNumber(String source) {
		int rtn = 0;
		try {
			JSONArray jaRoute = new JSONArray(source);
			rtn = jaRoute.length();
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return rtn;
	}

	public void showList() {
		List<Map<String, Object>> data = null;
		data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;

		for (int i = 0; i < number; i++) {
			item = new HashMap<String, Object>();
			item.put("title", Title[i]);
			item.put("area", Area.get(CityID[i]));
			item.put("describe", Detail[i]);
			item.put("town", Zone[i]);
			data.add(item);
		}

		ListAdapter adapter = new SimpleAdapter(
                this, data, R.layout.roadconditon_list_item,
                new String[] {"title", "area", "describe", "town"},
                new int[] { R.id.title, R.id.area, R.id.trafficState_describe, R.id.zone }
        );
		this.setListAdapter(adapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		Bundle reportBundle = new Bundle();

		reportBundle.putString("title", Title[position]);
		reportBundle.putString("detial", Detail[position]);
		reportBundle.putString("cityid", CityID[position]);
		reportBundle.putString("town_name", Zone[position]);
		reportBundle.putString("type", Type[position]);
		reportBundle.putString("starttime", StartTime[position]);
		reportBundle.putString("endtime", EndTime[position]);
		reportBundle.putString("lat", Lat[position]);
		reportBundle.putString("lon", Lon[position]);
		reportBundle.putString("authagent", Authagent[position]);
		reportBundle.putString("authtime", Authtime[position]);
		reportBundle.putString("town", Zone[position]);
		reportBundle.putString("reviewed", Reviewed[position]);
		reportBundle.putString("review_detail", ReviewDetail[position]);
		reportBundle.putString("source", Source[position]);
		Intent ReportIntent = new Intent(TrafficUpdate.this, TrafficDetail.class);
		ReportIntent.putExtras(reportBundle);
		startActivity(ReportIntent);
	}

}
