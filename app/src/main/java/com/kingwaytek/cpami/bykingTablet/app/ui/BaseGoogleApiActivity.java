package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * 如果使用 Google Map的同時又要使用 Google Places API的話才繼承這裡！
 *
 * @author Vincent (2016/4/25).
 */
public abstract class BaseGoogleApiActivity extends BaseMapActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected abstract void onApiReady();

    protected GoogleApiClient googleApiClient;

    private boolean isMapReady;

    @Override
    protected void init() {
        Log.i(TAG, "BaseGoogleApiActivity Init!!!");
    }

    @Override
    protected void onMapReady() {
        isMapReady = true;
        googleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMapReady)
            googleApiClient.connect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        Log.i(TAG, "GoogleApi: disconnect!");
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApi: onConnected!");
        onApiReady();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApi: onConnectionSuspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Utility.toastLong(getString(R.string.google_api_places_unavailable));
            Log.i(TAG, "onConnectionFailed: API UNAVAILABLE!");
        }
        Log.i(TAG, "GoogleApi: onConnectionFailed!");
    }
}
