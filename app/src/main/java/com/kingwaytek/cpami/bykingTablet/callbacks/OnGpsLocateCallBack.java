package com.kingwaytek.cpami.bykingTablet.callbacks;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vincent.chang on 2016/7/14.
 */
public interface OnGpsLocateCallBack {

    void onGpsLocated();
    void onGpsLocating();
    void onLocationWritten(LatLng latLng);

}
