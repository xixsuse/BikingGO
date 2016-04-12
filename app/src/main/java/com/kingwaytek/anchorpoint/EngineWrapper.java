package com.kingwaytek.anchorpoint;

import com.sonavtek.sonav.sonav;


/**
 * Wrapper of engine for native library of anchorpoint. The subclass of wrapper will
 * provide a subset of methods to interact with the native library according to
 * its functionality.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public abstract class EngineWrapper {
    
    protected sonav engine;

    /**
     * Create new instance of EngineWrapper.
     */
    public EngineWrapper() {
    }
    
    /**
     * Create new instance of EngineWrapper.
     * @param engine instance of engine.
     */
    public EngineWrapper(sonav engine) {
	this.engine = engine;
    }

    /**
     * Returns the engine to interact with.
     * @return the engine
     */
    public sonav getEngine() {
        return engine;
    }

    /**
     * Set the engine to interact with.
     * @param engine the engine to set
     */
    public void setEngine(sonav engine) {
        this.engine = engine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "EngineWrapper [engine=" + engine + "]";
    }
}
