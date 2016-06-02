package com.kingwaytek.cpami.bykingTablet.callbacks;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by vincent.chang on 2016/6/2.
 */
public interface OnLocationSelectedCallBack extends Serializable {

    void onLocationSelected(String title, LatLng latLng);

}
