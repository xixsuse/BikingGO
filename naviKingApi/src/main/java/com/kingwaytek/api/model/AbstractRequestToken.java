package com.kingwaytek.api.model;

import org.json.JSONException;
import org.json.JSONStringer;

public abstract class AbstractRequestToken extends WebPostImpl {

	String mToken;

	public AbstractRequestToken(String token) {
		mToken = token;

	}

	@Override
	public String getJSONResult() {
		String requestJSONData = "";
		try {
			JSONStringer jsonText = new JSONStringer();
			jsonText.object();
			{
				jsonText.key("input_code").value(mPassCode);
				jsonText.key("input_datas");
				jsonText.array();
				{
					jsonText.object();
					jsonText.key("token").value(mToken);
					jsonText.endObject();
				}
				jsonText.endArray();

			}
			jsonText.endObject();
			requestJSONData = jsonText.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJSONData;
	}
}