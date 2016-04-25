package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.InputStream;

/**
 * Created by vincent.chang on 2016/4/15.
 */
public class UiPoiSearchMapActivity extends BaseGoogleApiActivity {

    private static final int PLACE_PICKER_REQUEST = 1;

    private Marker lastMarker;

    @Override
    protected void onApiReady() {
        showListButton(true);
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.poi_search_location);
    }

    @Override
    protected void setListener() {

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

    }

    @Override
    protected void onListButtonClick() {
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
}
