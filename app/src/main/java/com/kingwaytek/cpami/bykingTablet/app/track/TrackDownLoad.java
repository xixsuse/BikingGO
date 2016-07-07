package com.kingwaytek.cpami.bykingTablet.app.track;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CreateMD5Code;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.Infomation.CommunicationBaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;
import com.kingwaytek.cpami.bykingTablet.view.TrackDownLaodAdaper;

public class TrackDownLoad extends CommunicationBaseActivity implements OnItemClickListener, OnClickListener,
		TextWatcher {

	private static final String api_list = "http://biking.cpami.gov.tw/Service/GetRoutes?keyword=";
	private static final String api_download = "http://biking.cpami.gov.tw/Service/GetRoutes?code=";

	private EditText text_keyword;
	private Button btn_search;
	private ListView listView;
	private double[] pointLonArray;
	private double[] pointLatArray;
	private String[] strArray;
	private String Info;

	private ArrayList<TrackListObject> array_listResult;

	private TrackDownLaodAdaper adpater_original;

	private TrackDownLaodAdaper currentAdpater;

	private String currentSelectName;

	// private ProgressDialog dialog = null;
	private UtilDialog progressDialog;
	private boolean isChanged;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_download);
		progressDialog = new UtilDialog(this);
		text_keyword = (EditText) this.findViewById(R.id.editText1);

		btn_search = (Button) this.findViewById(R.id.button1);

		listView = (ListView) this.findViewById(R.id.listView1);

		listView.setOnItemClickListener(this);

		Bundle extra = this.getIntent().getExtras();

		if (extra != null) {
			String type = extra.getString("type");

			String request = this.contructListCommand(type);
			progressDialog.progressDialog("請稍候片刻", "正在取得軌跡資訊");
			this.startHttpGet(request, false, null, null, null, null);
		}
	}

	@SuppressWarnings("deprecation")
	private String contructListCommand(String type) {
		Date date = new Date();

		String MD5Code = CreateMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
				* (1104 + date.getDate())) + "Kingway").getBytes());

		return api_list + "&code=" + MD5Code + "&type=" + type;
	}

	@SuppressWarnings("deprecation")
	private String contructDownloadCommand(String routeID) {
		Date date = new Date();

		String MD5Code = CreateMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
				* (1105 + date.getDate())) + "Kingway").getBytes());

		return api_download + MD5Code + "&routeid=" + routeID;
	}

	@Override
	public void didFinishWithGetRequest(String requestString, String resultString, Header[] respondHeaders) {
		super.didFinishWithGetRequest(requestString, resultString, respondHeaders);

		if (requestString.contains(api_list)) {

			array_listResult = TrackListParser.parse(resultString);

			adpater_original = new TrackDownLaodAdaper(this, array_listResult);

			currentAdpater = adpater_original;

			listView.setAdapter(currentAdpater);

			btn_search.setOnClickListener(this);

			text_keyword.addTextChangedListener(this);
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
		if (requestString.contains(api_download)) {
			final String result = resultString;

			new Thread() {
				public void run() {
					setPointArrayData(result);
				}
			}.start();
		}

	}

	@Override
	public void didFailWithGetRequest(String requestString, String resultString) {
		super.didFailWithGetRequest(requestString, resultString);

		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		if (requestString.contains(api_download)) {
			UtilDialog uit = new UtilDialog(TrackDownLoad.this);
			uit.showDialog_route_plan_choice("下載失敗，請重新下載", null, "確定", null);

		} else {
			UtilDialog uit = new UtilDialog(TrackDownLoad.this);
			uit.showDialog_route_plan_choice("無法連線，請稍後在試", null, "確定", null);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		TrackListObject object = (TrackListObject) currentAdpater.getItem(arg2);
		//
		// if (object.getRouteID().contains(cs)) {// 已下載過
		//
		// UtilDialog uit = new UtilDialog(TrackDownLoad.this);
		// uit.showDialog_route_plan_choice("軌跡已下載過!", null, "確定", null);
		//
		//
		// } else {
		currentSelectName = object.getRouteName();

		String RouteID = object.getRouteID();

		String request = this.contructDownloadCommand(RouteID);

		// dialog = ProgressDialog.show(this, "正在取得軌跡資訊", "請稍候片刻");
		progressDialog.progressDialog("請稍候片刻", "正在取得軌跡資訊");

		this.startHttpGet(request, false, null, null, null, null);
		// }
	}

	public void setPointArrayData(String source) {
		String ID = "", Name = "", Point = "", Length = "", Time = "";
		JSONArray jaRoute = null;
		// StringBuilder stringBuilder = new StringBuilder();
		try {
			jaRoute = new JSONArray(source);

			String ObjSource = jaRoute.get(0).toString();
			JSONObject jo = new JSONObject(ObjSource);
			Length = jo.getString("length");
			Time = jo.getString("time");
			ID = jo.getString("routeID");
			Name = jo.getString("routeName");
			Point = jo.getString("routeXY");
			// Info = jo.getString("detail");
			strArray = Point.split(";");
			int length = strArray.length;

			// if (length >= 100) {// 如果POINT數量太多會導致OOM,下面瘦身
			// pointLonArray = new double[50];// 只取50個點，50buf
			// pointLatArray = new double[50];
			//
			// int dis = length / 50;
			// int count = 0;
			// for (int i = 0; i < 50; i += dis) {
			// pointLonArray[count] =
			// Double.parseDouble(strArray[i].split(",")[0]);
			// pointLatArray[count] =
			// Double.parseDouble(strArray[i].split(",")[1]);
			//
			// stringBuilder.append("ID=" + ID + "Name=" + Name + "point=" +
			// Point + "\n");
			// count++;
			// Log.i("cuber", "count=" + count);
			// }
			//
			// writeToTrackDataBase(currentSelectName, pointLonArray,
			// pointLatArray);
			// } else {

			pointLonArray = new double[length];
			pointLatArray = new double[length];
			for (int i = 0; i < length; i++) {
				pointLonArray[i] = Double.parseDouble(strArray[i].split(",")[0]);
				pointLatArray[i] = Double.parseDouble(strArray[i].split(",")[1]);

				// stringBuilder.append("ID=" + ID + "Name=" + Name + "point=" +
				// Point + "\n");
			}

			writeToTrackDataBase(currentSelectName, pointLonArray, pointLatArray);
			// this.dismissDownloadDialog();
			// }
		} catch (JSONException e) {
			e.printStackTrace();

			this.dismissDownloadDialog();
			UtilDialog uit = new UtilDialog(TrackDownLoad.this);
			uit.showDialog_route_plan_choice("下載失敗，請重新下載", null, "確定", null);
		} catch (Exception e) {
			e.printStackTrace();

			this.dismissDownloadDialog();
			UtilDialog uit = new UtilDialog(TrackDownLoad.this);
			uit.showDialog_route_plan_choice("下載失敗，請重新下載", null, "確定", null);
		}
		// try {
		// String ObjSource = jaRoute.get(0).toString();
		// JSONObject jo = new JSONObject(ObjSource);
		// ID = jo.getString("routeID");
		// Name = jo.getString("routeName");
		// Point = jo.getString("routeXY");
		// // Info = jo.getString("detail");
		// strArray = Point.split(";");
		// int length = strArray.length;
		// pointLonArray = new double[length];
		// pointLatArray = new double[length];
		// for (int i = 0; i < length; i++) {
		// pointLonArray[i] = Double
		// .parseDouble(strArray[i].split(",")[0]);
		// pointLatArray[i] = Double
		// .parseDouble(strArray[i].split(",")[1]);
		// }
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// stringBuilder.append("ID=" + ID + "Name=" + Name + "point=" + Point
		// + "\n");
	}

	private void dismissDownloadDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	private void writeToTrackDataBase(String name, double[] lon, double[] lat) {

		Date date = Calendar.getInstance().getTime();

		// double[] pointLonArray={121.52305,121.52305,121.52378};
		// double[] pointLatArray={25.05964,25.05964,25.05964};
		double[] pointLonArray = lon;
		double[] pointLatArray = lat;
		Track NewTrack = new Track(this);
		NewTrack.setName(name);
		NewTrack.setDescription("困難度:1顆星," + "\n" + Info);
		NewTrack.setStartTime(new Date());
		NewTrack.setRecordingStatus(TrackRecordingStatus.IMPORTING);
		int id = (int) NewTrack.Record();
		TrackPoint trackpoint = new TrackPoint(this);
		for (int i = 0; i < pointLonArray.length; i++) {
			date.setTime(date.getTime() + 1000L);
			trackpoint.setID(id);
			trackpoint.setLongitude(pointLonArray[i]);
			trackpoint.setLatitude(pointLatArray[i]);
			trackpoint.setDate(date);
			trackpoint.setType(2);
			trackpoint.Pin();
		}

		NewTrack.setRecordingStatus(TrackRecordingStatus.IMPORTED);
		NewTrack.setEndTime(trackpoint.getDate());
		NewTrack.setCreateTime();
		NewTrack.Update();

		this.runOnUiThread(runnable);
	}

	private Runnable runnable = new Runnable() {
		public void run() {

			progressDialog.dismiss();
			UtilDialog uita = new UtilDialog(TrackDownLoad.this);
			uita.showDialog_route_plan_choice("軌跡下載成功", null, "確定", null);
		}
	};

	@Override
	public void onClick(View arg0) {
		String keyword = text_keyword.getText().toString();

		ArrayList<TrackListObject> tempArray = new ArrayList<TrackListObject>();

		for (TrackListObject object : array_listResult) {

			if (object.getRouteName().contains(keyword)) {

				tempArray.add(object);
			}
		}

		currentAdpater = new TrackDownLaodAdaper(this, tempArray);

		listView.setAdapter(currentAdpater);

		isChanged = true;

		// currentAdpater.notifyDataSetChanged();
	}

	@Override
	public void afterTextChanged(Editable arg0) {

		if (isChanged) {

			int length = arg0.toString().length();

			if (length == 0) {
				listView.setAdapter(adpater_original);

				currentAdpater = adpater_original;
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}
}