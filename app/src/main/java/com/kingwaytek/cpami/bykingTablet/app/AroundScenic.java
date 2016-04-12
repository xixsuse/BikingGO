package com.kingwaytek.cpami.bykingTablet.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.sonavtek.sonav.PPDATA;
import com.sonavtek.sonav.sonav;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.bus.RLineInfo;
import com.kingwaytek.cpami.bykingTablet.bus.RPointInfo;
import com.kingwaytek.cpami.bykingTablet.hardware.GPSListener;
import com.kingwaytek.cpami.bykingTablet.view.AroundSceniclistAdapter;
import com.kingwaytek.cpami.bykingTablet.view.RoadlistAdapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AroundScenic extends Activity {
       private sonav engine ;
       private int mRoadCount;
       private int mCurRoadIndex;
       private TextView testView;
       private ListView ScenicSpotsListView;
       private AroundSceniclistAdapter listitemAdapter ;
       StringBuilder sBuild = new StringBuilder(); 
       private Button gohome;
       
      // private ArrayList<ListItem> mRoadArray;
       private ArrayList<PPDATA> mPOIArray;
       @Override
       protected void onCreate(Bundle savedInstanceState) {
         	// TODO Auto-generated method stub
//    	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
          	super.onCreate(savedInstanceState);
//          	setContentView(R.layout.around_scenic_spots);
//          	testView = (TextView)findViewById(R.id.testView);
          	
          
    		
    		
    		
            
          	engine = sonav.getInstance();
          	int mapstyle = Integer.valueOf(PreferenceActivity.getMapStyle(this));
    		if (mapstyle < 6) {
    			engine.setmapstyle(0, mapstyle, 1);
    		}else{
    			mapstyle-=5;
    			engine.setmapstyle(1, 0, mapstyle);
    		}
    		engine.savenaviparameter();
          	setScenicSpotsEngine();
        	setContentView(R.layout.aroundsceniclist);
        	
//        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
//    		setTitle(getString(R.string.byking_function_AroundScenic_title) );
//    		((TextView) findViewById(R.id.title_text2)).setText("");
//    		
//    		gohome = (Button)findViewById(R.id.go_home);
//            gohome.setOnClickListener(new OnClickListener() {
//    			
//    			@Override
//    			public void onClick(View v) {
//    				setResult(RESULT_CANCELED);
//    				finish();
//    			    return;
//    				
//    			}
//    		});
//    		
        	ScenicSpotsListView=(ListView)findViewById(R.id.RoadlistItem);
          	setRoadListData();
          
          	
 
         }
   	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.title_text)).setText(title);
		((TextView) findViewById(R.id.title_text2)).setText("");
	}
       
        private void setScenicSpotsEngine(){
            mPOIArray = new ArrayList<PPDATA>();
           		// Retrieve route POI list
        		if (engine.initsppoi() <= 0) {
        			return;
        		}
        		
        		mRoadCount = engine.getroadlistnum();
        		if (mRoadCount == 0) {
  
        			return;
        		}
        		mPOIArray.clear();
        		mCurRoadIndex = engine.getnowroadidx();
        		
        		
        		for (int i = mCurRoadIndex; i < mRoadCount; i++) {		
        			String pIdList; 
        			
        			if (i == 0)
        				pIdList = engine.getsppoitxt(i, 90);
        			else
        				pIdList = engine.getsppoitxt(i, 360);
        			
        			if (pIdList == null) {
        				continue;
        			}
        			Log.i("AroundSecnicSpot.java","pIdList="+pIdList);
        			//sBuild.append(pIdList); 
        			//testView.setText(sBuild.toString());
        			StringTokenizer st = new StringTokenizer(pIdList, ",");
        			
        			while (st.hasMoreTokens() == true)
        			{	
        				PPDATA ppData = new PPDATA();
        				int poiid = Integer.parseInt(st.nextToken());
        				ppData = engine.getptproperty(ppData, poiid);
        				mPOIArray.add(new PPDATA(ppData));
        			}
        		}
        		
        		engine.closesppoi();
        		mPOIArray.trimToSize();		
        		mRoadCount = mPOIArray.size();
        		
        		// Sort by ascending distance
        		Comparator<PPDATA> comparator = new Comparator<PPDATA>() {
        			public int compare(PPDATA object1, PPDATA object2) {
        				double dist1, dist2;
        				
        				dist1 = getDistance(object1.getLongitude(), object1.getLatitude(), GPSListener.lon, GPSListener.lat);
        				dist2 = getDistance(object2.getLongitude(), object2.getLatitude(), GPSListener.lon, GPSListener.lat);
        				
        	    		if(dist1 == dist2)
        	    			return 0;    	
        	    		else if(dist1 > dist2)
        	    			return 1;    	
        	    		else    		
        	    			return -1;
        			}
        		};
        		Collections.sort(mPOIArray, comparator);

//        		for (int i = 0; i < mRoadCount; i++) {	
//        			PPDATA ppData = mPOIArray.get(i);
//        			double dist;
//        			
//        			String location = engine.showcitytownname(ppData.getLongitude(), ppData.getLatitude());
//        			location = location.replace(",", "/");
//
//        			dist = getDistance(ppData.getLongitude(), ppData.getLatitude(), GPSListener.lon, GPSListener.lat);
//        			
//        			//mRoadArray.add(new ListItem(bmp, ppData.name, location, getDistanceStr(dist), "KM"));
//        			Log.i("AroundScenic.java","ppData.getName="+ppData.getName()+"location"+location+" distance="+getDistanceStr(dist)+"KM");
//        			sBuild.append("ppData.getName="+ppData.getName()+"location"+location+" distance="+getDistanceStr(dist)+"KM"+"\n"); 
//        			testView.setText(sBuild.toString());
//        		}	
        }	
        
       
       
     	private double getDistance(double Lat1, double Lon1, double Lat2, double Lon2) {
		double dst = (Math.abs(Lat1 - Lat2) + Math.abs(Lon1 - Lon2)) * 111.12f;
		return dst;
	    }
     	
     	private String getDistanceStr(double distance)
    	{
    		double km = distance;
    		String dst = Double.toString(km);
    		dst = dst.trim();
    		if (km > 100) {
    			dst = dst.substring(0, Math.min(dst.indexOf('.')+2, dst.length()));
    		} else {
    			dst = dst.substring(0, Math.min(dst.indexOf('.')+3, dst.length()));
    		}
    		return dst;
    	}
     	
    	private void setRoadListData(){
    		String[] name = new String[mRoadCount]; 
//    		int[] turn = new int[mRoadCount]; 
    		String[] strdistance = new String[mRoadCount];
     		String[] citytownname = new String[mRoadCount];
    		ArrayList<HashMap<String, Object>> listitem = new ArrayList<HashMap<String, Object>>();
  
     		for (int i=0;i < mRoadCount ;i++){
      			PPDATA ppData = mPOIArray.get(i);
    			double dist;
    			String location = engine.showcitytownname(ppData.getLongitude(), ppData.getLatitude());
    			location = location.replace(",", "/");
    			dist = getDistance(ppData.getLongitude(), ppData.getLatitude(), GPSListener.lon, GPSListener.lat);
  				name[i] =ppData.getName();             
  			    citytownname[i] = location;
    			strdistance[i] = getDistanceStr(dist);		
  			    sBuild.append("ppData.getName="+ppData.getName()+"location"+location+" distance="+getDistanceStr(dist)+"KM"+"\n"); 
  			    Log.i("AroundScenic.java","sBuild.append="+sBuild.toString());
    		}
    		

    		
    		for (int i = 0; i <mRoadCount ; i++) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("name", name[i]);
    			map.put("citytownname", citytownname[i]);
    			map.put("strdistance", strdistance[i]);
    			listitem.add(map);
    		}

    		listitemAdapter = new AroundSceniclistAdapter(this,
    				 listitem, 
    			     R.layout.roadlistitem, 
    				 new String[] { "name", "strlength" ,"citytownname"},  
    				 new int[] { R.id.roadlist_text1, R.id.roadlist_distance ,R.id.area}
    			);
    		listitemAdapter.putNameArray(name);
    		listitemAdapter.putLengthArray(strdistance);
    		//listitemAdapter.putTurnArray(turn);
    		listitemAdapter.putCityTowNameArray(citytownname);

    		ScenicSpotsListView.setAdapter(listitemAdapter );
    		 
    	}

}
