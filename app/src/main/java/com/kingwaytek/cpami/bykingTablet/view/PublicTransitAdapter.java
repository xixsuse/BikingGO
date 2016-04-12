package com.kingwaytek.cpami.bykingTablet.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

public class PublicTransitAdapter extends SimpleAdapter{
    private Context adpContext;
    private int adpResource;
    private String[] strName;
    private String[] strLength;
    private String[] citytownname;
    private int[] turn;

  

    public PublicTransitAdapter (Context context, ArrayList<HashMap<String,Object >> 
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
    	
//	if (convertView == null) {
//	    convertView = LayoutInflater.from(adpContext).inflate(adpResource,
//		    null);
//	}
 

		
    convertView = LayoutInflater.from(adpContext).inflate(adpResource,
   		    null);
    	
	
	TextView NameTextView=(TextView)convertView.findViewById(R.id.roadlist_text1);
	TextView LengthTextView=(TextView)convertView.findViewById(R.id.roadlist_time );
	TextView CityNameTextView=(TextView)convertView.findViewById(R.id.area);
	ImageView turnImageView=(ImageView)convertView.findViewById(R.id.turn_image );
    
	NameTextView.setText(strName[position]);
	if(position!=0 && strLength[position]!= null){
	 LengthTextView.setText(strLength[position]+"分鐘");

	}
	CityNameTextView.setText(citytownname[position]);
	turnImageView.setImageResource(turn[position]);




	return convertView;//super.getView(position, convertView, parent);
    }
}
