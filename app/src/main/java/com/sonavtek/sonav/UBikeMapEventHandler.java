package com.sonavtek.sonav;

import android.os.Message;
import android.util.Log;

/**
 * @author yawhaw 2013/11/27
 * The event handler which process events for the UBike
 */
public class UBikeMapEventHandler extends EngineEventHandler {

    protected sonav mEngine;
    protected UBikeMapEventListen mEventListen;

    /**
     * @param engine
     *
     **/
    public UBikeMapEventHandler(sonav engine) {
        super(engine);
        this.mEngine = engine;
        setEngine(engine);
    }
    
    public void setOnUBikeMapEventListen(UBikeMapEventListen eventListen ) {
    	mEventListen = eventListen;
	}

    /**
     * Handles events or status received from native library. The native library
     * will call proc(int msg, int arg1, int arg2) method to pass events or
     * status.
     * 
     * @param msg
     *            instance of Message which contains information about the
     *            events or status.
     */
    @Override
    public void handleMessage(Message msg) {
   

        super.handleMessage(msg);

        switch (msg.what) {
        
            case EngineEvent.AM_NEWTIP://25586
                Log.i("UBikeMapEventHandler","EngineEvent.AM_NEWTIP");

           	    String msg2 = mEngine.getcallbackstr(msg.arg2);
             	mEventListen.OnUBkieMapEventListen(msg2);
             	//Log.i("UBikeMapEventHandler",msg2);
                  
                break;
        }
    }
  

}
