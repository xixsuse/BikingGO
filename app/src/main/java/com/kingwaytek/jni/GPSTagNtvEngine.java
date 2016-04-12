package com.kingwaytek.jni;

import android.util.Log;


public class GPSTagNtvEngine {
	
	
	
	static public native int WriteGPSInfo(String fileSource, String fileDest, LocationInfo Info);
	static public native PhotoAttribute GetPhotoAttribute(String SampleFrom, boolean GetLocation);

	static {
        try
        {   Log.i("whichcalass", "GPSTagNtvEngine.java");    	
        	Log.i("JNI", "loading GeoBotNtvEngine");
            System.loadLibrary("GeoBotNtvEngine");        	
        }
        catch(UnsatisfiedLinkError ule)
        {
        	Log.e("JNI", "Error: can't load libGeoBotNtvEngine.so");
        }
    }
}
