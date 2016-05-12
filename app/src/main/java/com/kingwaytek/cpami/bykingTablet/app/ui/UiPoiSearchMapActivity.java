package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 地址查詢、關鍵字查詢、周邊景點 and blah blah blah~<br>
 * 都可以在這裡做！
 *
 * @author Vincent (2016/4/15).
 */
public class UiPoiSearchMapActivity extends BaseGoogleApiActivity implements TextWatcher, GoogleMap.OnMapLongClickListener {

    private static final int PLACE_PICKER_REQUEST = 1;

    private Marker lastMarker;

    @Override
    protected void onApiReady() {
        showSwitchButton(true);
        map.setOnMapLongClickListener(this);
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.poi_search_location);
    }

    @Override
    protected void setListener() {
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
    public void onDestroy() {
        super.onDestroy();
        searchText.removeTextChangedListener(this);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (notNull(marker.getSnippet())) {
            View view = getLayoutInflater().inflate(R.layout.inflate_marker_poi_info_window, null);

            TextView markerTitle = (TextView) view.findViewById(R.id.marker_title);
            TextView markerSnippet = (TextView) view.findViewById(R.id.marker_snippet);

            markerTitle.setText(marker.getTitle());
            markerSnippet.setText(marker.getSnippet());

            return view;
        }
        else
            return super.getInfoWindow(marker);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (notNull(marker.getSnippet())) {
            View view = PopWindowHelper.getPoiEditWindowView();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //PopWindowHelper.dismissPopWindow();
                }
            });
        }
    }

    @Override
    protected void onSwitchButtonClick() {
        try {
            PlacePicker.IntentBuilder pickerBuilder = new PlacePicker.IntentBuilder();
            startActivityForResult(pickerBuilder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    putMarkerAndMoveCamera(place);
                    break;
            }
        }
    }

    private void putMarkerAndMoveCamera(Place place) {
        Utility.toastShort(place.getName().toString());
        moveCameraAndZoom(place.getLatLng(), 17);

        if (notNull(lastMarker))
            lastMarker.remove();

        MarkerOptions marker = new MarkerOptions();

        marker.position(place.getLatLng());
        marker.title(place.getName().toString());
        marker.snippet(place.getAddress().toString());

        InputStream is = getResources().openRawResource(+R.drawable.ic_end);
        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        lastMarker = map.addMarker(marker);

        if (notNull(lastMarker))
            lastMarker.showInfoWindow();

        closeInputStream(is);
    }

    @Override
    protected void onLocateMyPosition(Location location) {
        try {
            PendingResult<PlaceLikelihoodBuffer> results = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

            results.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    placeLikelihoods.release();
                }
            });
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
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
    public void onMapLongClick(LatLng latLng) {
        putMarker(latLng);
    }

    private void putMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title("選擇點位：");
        marker.snippet(String.valueOf(latLng.latitude + "\n" + latLng.longitude));

        InputStream is = getResources().openRawResource(+R.drawable.ic_end);

        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

        map.addMarker(marker).showInfoWindow();
        moveCameraAndZoom(latLng, 16);

        closeInputStream(is);
    }
}
