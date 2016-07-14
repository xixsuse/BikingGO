package com.kingwaytek.cpami.bykingTablet.app.ui.track;

import android.content.SharedPreferences;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseMapActivity;

/**
 * 軌跡錄製地圖
 *
 * @author Vincent (2016/7/14)
 */
public class UiTrackMapActivity extends BaseMapActivity {

    private FloatingActionButton trackBtn;

    @Override
    protected void onMapReady() {

    }

    @Override
    protected int getMapLayout() {
        return R.layout.activity_track_map;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_bike_track);
    }

    @Override
    protected void findViews() {
        trackBtn = (FloatingActionButton) findViewById(R.id.floatingBtn_track);
    }

    @Override
    protected void setListener() {
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onLocateMyPosition(Location location) {

    }
}
