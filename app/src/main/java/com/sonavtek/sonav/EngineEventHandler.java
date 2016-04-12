package com.sonavtek.sonav;

import android.os.Handler;


public abstract class EngineEventHandler extends Handler {

    /** The engine which the events from. */
    protected sonav engine;

    /**
     * Create new instance of EngineEventHandler.
     */
    public EngineEventHandler() {
    }

    /**
     * Create new instance of EngineEventHandler.
     * 
     * @param engine
     *            the engine for this instance to listen
     */
    public EngineEventHandler(sonav engine) {
	this.engine = engine;
    }

    /**
     * @return the engine for this instance to listen
     */
    public sonav getEngine() {
	return engine;
    }

    /**
     * @param engine
     *            the engine for this instance to listen to set
     */
    public void setEngine(sonav engine) {
	this.engine = engine;
    }
}
