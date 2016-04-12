package com.kingwaytek.cpami.bykingTablet.app.track;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class TrackListParser {

	public static ArrayList<TrackListObject> parse(String json) {

		ArrayList<TrackListObject> resultObjects = new ArrayList<TrackListObject>();

		try {

			JSONArray array = new JSONArray(json);

			for (int i = 0; i < array.length(); i++) {

				TrackListObject object = new TrackListObject();

				object.setRouteID(array.getJSONObject(i).getString("routeID"));
				object.setRouteName(array.getJSONObject(i).getString(
						"routeName"));
				object.setLength(array.getJSONObject(i).getString("length"));
				object.setTime(array.getJSONObject(i).getString("time"));

				resultObjects.add(object);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultObjects;
	}

}
