package com.kingwaytek.cpami.biking.callbacks;

/**
 * Created by vincent.chang on 2016/4/14.
 */
public interface OnEngineReadyCallBack {

    void onEngineInitializing();

    void onEngineReady();

    void onEngineInitFailed();
}
