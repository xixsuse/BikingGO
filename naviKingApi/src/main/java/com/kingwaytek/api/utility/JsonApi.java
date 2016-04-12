package com.kingwaytek.api.utility;

import org.json.JSONObject;

public class JsonApi {

	public static boolean checkObjNotEmpty(JSONObject obj, String key) {
		if (obj.has(key) && !obj.isNull(key)) {
			return true;
		} else {
			return false;
		}
	}
}