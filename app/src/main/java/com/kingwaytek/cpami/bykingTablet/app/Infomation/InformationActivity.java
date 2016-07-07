package com.kingwaytek.cpami.bykingTablet.app.Infomation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.CreatMD5Code;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

public class InformationActivity extends CommunicationBaseActivity implements OnItemClickListener {

	/* API query command */
	private static final String requestCommand = "http://biking.cpami.gov.tw/Service/ListActivity?code=";

	/* Web service number */
	private static final int webServiceNumber = 1303;

	/* Widgets */
	private ListView listView;

	/* Adapter */
	private InfomationListAdapter adapter;

	/* Data source */
	ArrayList<InfomationObject> dataSource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_infomation);

		listView = (ListView) this.findViewById(R.id.listView1);

		final String requestCommand = this.constructRequestCommand();

		if (!SettingManager.isInternetConfirmEnabled()) {
			this.startHttpGet(requestCommand, true, "資料讀取中", "請稍候", null, null);
        }
        else {

			UtilDialog uit = new UtilDialog(InformationActivity.this) {
				@Override
				public void click_btn_1() {
					InformationActivity.this.startHttpGet(requestCommand, true, "資料讀取中", "請稍候", null, null);
					super.click_btn_1();
				}

				@Override
				public void click_btn_2() {
					InformationActivity.this.finish();
					super.click_btn_2();
				}
			};
			uit.showDialog_route_plan_choice(this.getResources().getString(R.string.dialog_web_message), null, "確定",
					"取消");
		}
	}

	/* Generate MD5 code */
	@SuppressWarnings({ "deprecation" })
	private String constructRequestCommand() {
		Date date = new Date();

		String md5 = CreatMD5Code.getMD5((String.valueOf(((date.getMonth() + 1) + date.getHours())
				* (webServiceNumber + date.getDate())) + "Kingway").getBytes());

		return requestCommand + md5;
	}

	/* HTTP callback */
	@Override
	public void didFinishWithGetRequest(String requestString, String resultString, Header[] respondHeaders) {
		super.didFinishWithGetRequest(requestString, resultString, respondHeaders);

		dataSource = this.parser(resultString);

		if (!dataSource.isEmpty()) {

			adapter = new InfomationListAdapter(this, dataSource);

			listView.setAdapter(adapter);

			listView.setOnItemClickListener(this);
		}
	}

	@Override
	public void didFailWithGetRequest(String requestString, String resultString) {
		super.didFailWithGetRequest(requestString, resultString);

		this.showAlert();
	}

	/* Item on ListView click */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		InfomationObject object = dataSource.get(arg2);

		Intent intent = new Intent(this, DetailActivity.class);

		intent.putExtra("Info", object);

		this.startActivity(intent);
	}

	/* Alert */
	private void showAlert() {
		new AlertDialog.Builder(this).setTitle("查詢失敗").setMessage("請檢查您的網路狀態並稍後再試")
				.setNeutralButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						InformationActivity.this.finish();
					}
				}).show();
	}

	/* Parser */
	private ArrayList<InfomationObject> parser(String result) {

		ArrayList<InfomationObject> objects = new ArrayList<InfomationObject>();

		try {

			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {

				InfomationObject object = new InfomationObject();

				object.setId(array.getJSONObject(i).getString("id"));
				object.setName(array.getJSONObject(i).getString("name"));
				object.setDescription(array.getJSONObject(i).getString("description"));
				object.setParticpation(array.getJSONObject(i).getString("particpation"));
				object.setLocation(array.getJSONObject(i).getString("location"));
				object.setAdd(array.getJSONObject(i).getString("add"));
				object.setTel(array.getJSONObject(i).getString("tel"));
				object.setOrg(array.getJSONObject(i).getString("org"));
				object.setStart(array.getJSONObject(i).getString("start"));
				object.setEnd(array.getJSONObject(i).getString("end"));
				object.setCycle(array.getJSONObject(i).getString("cycle"));
				object.setNoncycle(array.getJSONObject(i).getString("noncycle"));
				object.setWebsite(array.getJSONObject(i).getString("website"));
				object.setPicture1(array.getJSONObject(i).getString("picture1"));
				object.setPicdescribe1(array.getJSONObject(i).getString("picdescribe1"));
				object.setPicture2(array.getJSONObject(i).getString("picture2"));
				object.setPicdescribe2(array.getJSONObject(i).getString("picdescribe2"));
				object.setPicture3(array.getJSONObject(i).getString("picture3"));
				object.setPicdescribe3(array.getJSONObject(i).getString("picdescribe3"));
				object.setPx(array.getJSONObject(i).getString("px"));
				object.setPy(array.getJSONObject(i).getString("py"));
				object.setClass1(array.getJSONObject(i).getString("class1"));
				object.setClass2(array.getJSONObject(i).getString("class2"));
				object.setActivityClass(array.getJSONObject(i).getString("activityClass"));
				object.setMap(array.getJSONObject(i).getString("map"));
				object.setTravellinginfo(array.getJSONObject(i).getString("travellinginfo"));
				object.setParkinginfo(array.getJSONObject(i).getString("parkinginfo"));
				object.setCharge(array.getJSONObject(i).getString("charge"));
				object.setRemarks(array.getJSONObject(i).getString("remarks"));
				//object.setRegion(array.getJSONObject(i).getString("region"));
				//object.setTown(array.getJSONObject(i).getString("town"));

				objects.add(object);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return objects;
	}
	// for GCM
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	//
	// Intent intent = new Intent();
	// intent.setClass(this, MainActivity.class);
	// startActivity(intent);
	// finish();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
}
