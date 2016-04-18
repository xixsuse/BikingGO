package com.kingwaytek.cpami.bykingTablet.app.ui;

import com.google.android.gms.maps.model.Marker;
import com.kingwaytek.cpami.bykingTablet.R;

/**
 * Created by vincent.chang on 2016/4/15.
 */
public class UiPoiSearchMapActivity extends BaseMapActivity {



    @Override
    protected void init() {

    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.poi_search_location);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}
