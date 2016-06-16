package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.util.ArrayList;

/**
 * 如果使用 Google Map的同時又要使用 Google Places API的話才繼承這裡！<br>
 *
 * 這裡也包含了 AutoComplete places query for searchText.
 *
 * @author Vincent (2016/4/25).
 */
public abstract class BaseGoogleApiActivity extends BaseMapActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        TextWatcher {

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

        setSearchTextListener();
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

    private void setSearchTextListener() {
        searchText.addTextChangedListener(this);
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchLocation((String)parent.getItemAtPosition(position));
                clearSearchText();
                hideKeyboard(true);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(final Editable s) {
        if (s.length() > 0) {
            LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(
                    googleApiClient, s.toString(), latLngBounds, null);

            final ArrayList<String> nameList = new ArrayList<>();

            results.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    for (AutocompletePrediction prediction : autocompletePredictions) {
                        Log.i(TAG, String.format("PlaceId: '%s', PrimaryText: '%s', SecondaryText: '%s', Type: '%s'",
                                prediction.getPlaceId(),
                                prediction.getPrimaryText(null),
                                prediction.getSecondaryText(null),
                                prediction.getPlaceTypes()));

                        nameList.add(prediction.getPrimaryText(null).toString());
                    }
                    autocompletePredictions.release();
                    setAutoCompleteText(nameList);

                    Log.i(TAG, "TextChanged: " + s.length());
                }
            });
        }
        else {
            searchText.setAdapter(null);
            searchText.dismissDropDown();
            Log.i(TAG, "TextChanged: empty!");
        }
    }

    private void setAutoCompleteText(ArrayList<String> nameList) {
        searchText.setAdapter(getSimpleAdapter(nameList));
        searchText.setDropDownBackgroundResource(R.drawable.background_search_adapter);
        if (searchText.getText().length() > 0)
            searchText.showDropDown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchText.removeTextChangedListener(this);
    }
}
