package com.kingwaytek.cpami.bykingTablet.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.view.ViewConstant.ListMode;

/**
 * List Activity Adapter for General Text Only List 
 * @author yawhaw_ou (yawhaw@kingwaytek.com)
 */
public class RoadlistAdapter extends SimpleAdapter {

    private Context adpContext;
    private int adpResource;
    private String[] strName;
    private String[] strLength;
    private String[] citytownname;
    private int[] turn;

  

    public RoadlistAdapter (Context context, ArrayList<HashMap<String,Object >> 
          data, int resource, String[] from, int[] to)  {
    super(context, data, resource, from, to);
    
	adpContext = context;
	adpResource = resource;


    }




    public void putNameArray(String[] name){
    	strName = name;
    }
    
    public void putLengthArray(String[] length){
    	strLength = length;
    }
    
    public void putTurnArray(int[] trunImage){
    	turn = trunImage;
    }

    public void putCityTowNameArray(String[] cityname){
    	citytownname = cityname;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.i("RoadlistAdapter.java","position="+position);
//	if (convertView == null) {
//	    convertView = LayoutInflater.from(adpContext).inflate(adpResource,
//		    null);
//	}
    	Log.i("ListSimpleAdapter.java","getView()");

		
    	convertView = LayoutInflater.from(adpContext).inflate(adpResource,
   		    null);

	
	TextView NameTextView=(TextView)convertView.findViewById(R.id.roadlist_text1);
	TextView LengthTextView=(TextView)convertView.findViewById(R.id.roadlist_distance );
	TextView CityNameTextView=(TextView)convertView.findViewById(R.id.area);
	ImageView turnImageView=(ImageView)convertView.findViewById(R.id.turn_image );

	NameTextView.setText(strName[position]);
	LengthTextView.setText(strLength[position]+"公尺");
	CityNameTextView.setText(citytownname[position]);
	turnImageView.setImageResource(turn[position]);




	return convertView;//super.getView(position, convertView, parent);
    }
    
    
}
