package com.sonavtek.sonav;

import android.os.Message;
import android.util.Log;

/**
 * The event handler which process events for the PathFinder
 */
public class PathEventHandler extends EngineEventHandler {

    private PathFinder pathEngine;

    /**
     * @param engine
     */
    public PathEventHandler(sonav engine) {
        super(engine);
    }

    /**
     * gets the PathFinder engine.
     * 
     * @return PathFinder instance.
     */
    public PathFinder getPathEngine() {
        return pathEngine;
    }

    /**
     * sets PathFinder engine.
     * 
     * @param engine
     */
    public void setPathEngine(PathFinder engine) {
        pathEngine = engine;
    }

    /**
     * Handles events only for path finding or status received from native
     * library. The native library will call proc(int msg, int arg1, int arg2)
     * method to pass events or status.
     * 
     * @param msg
     *            instance of Message which contains information about the
     *            events or status.
     */
    @Override
    public void handleMessage(Message msg) {
        //Log.d(getClass().toString(), "handleMessage: " + msg);

        super.handleMessage(msg);

        switch (msg.what) {
            case EngineEvent.AM_ROUTE: // 25569
                Log.i("PathFinder_Handled", "handleMessage: " + msg);
                if (msg.arg2 == PathFinder.PATH_FINDING_DONE) {
                    pathEngine.loadFoundPathData();
                    pathEngine.setStatus(PathFinder.PATH_FINDING_DONE);
                }
                break;
            default: // Un_handled messages
                //Log.i("PathFinder_Unhandled", "handleMessage: " + msg);
                break;
        }
    }
}
