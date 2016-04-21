package com.kingwaytek.cpami.bykingTablet.utilities;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜尋地點的方法：<h1>
 * 1. 會先使用 Geocoder，查無地點的話再使用 Google geocoder api。<br>
 * 2. 搭配 OnLocationFoundCallBack使用。<br>
 * 3. 只需呼叫 searchLocation(input, callBack)即可！
 *
 * @author Vincent (2016/1/27)
 */
public class LocationSearchHelper {

    private static final String TAG = "LocationSearchHelper";

    private static List<Address> addrList;
    private static ArrayList<String> locationNameList;

    public interface OnLocationFoundCallBack {
        void onLocationFound(ArrayList<ItemsSearchResult> searchResults, ArrayList<String> nameList, boolean isSearchByGeocoder);
        void onNothingFound();
    }

    public static void searchLocation(String input, OnLocationFoundCallBack locationFound) {
        input = input.trim();

        if (input.isEmpty()) {
            Utility.toastShort(AppController.getInstance().getAppContext().getString(R.string.search_any_location));
            return;
        }
        PopWindowHelper.getLoadingPopView();

        Geocoder geocoder = new Geocoder(AppController.getInstance().getAppContext());

        try {
            addrList = null;
            addrList = geocoder.getFromLocationName(input, 10);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            Utility.toastLong("Geocoder: " + e.getMessage());
        }

        if (addrList == null || addrList.isEmpty()) {
            Utility.toastShort("Search by Google API");
            searchByGoogleApi(input, locationFound);
        }
        else {
            locationNameList = new ArrayList<>();
            ArrayList<ItemsSearchResult> resultList = new ArrayList<>();

            String locationName;
            String geoName;

            for (Address addr : addrList) {
                locationName = getCombinedName(addr);
                geoName = locationName + " - " + addr.getAdminArea() + addr.getLocality() + " (" + addr.getCountryName() + ")";
                geoName = geoName.replace("null", " ");

                Log.i(TAG, "Locality: " + addr.getLocality() + " SubAdminArea: " + addr.getSubAdminArea() + " PostalCode: " +
                        addr.getPostalCode() + " Thoroughfare: " + addr.getThoroughfare() + " SubThoroughfare: " + addr.getSubThoroughfare());

                locationNameList.add(geoName);

                resultList.add(new ItemsSearchResult(locationName,
                        addr.getAdminArea(), addr.getCountryName(), addr.getLatitude(), addr.getLongitude()));
            }
            locationFound.onLocationFound(resultList, locationNameList, true);
            PopWindowHelper.dismissPopWindow();
        }
    }

    private static void searchByGoogleApi(String input, final OnLocationFoundCallBack locationFound) {
        DataArray.getLocationSearchResult(input, new DataArray.OnDataGetCallBack() {
            @Override
            public void onDataGet() {
                locationNameList = new ArrayList<>();
                for (ItemsSearchResult resultItem : DataArray.list_searchResult.get()) {
                    locationNameList.add(resultItem.NAME);
                }

                if (!locationNameList.isEmpty())
                    locationFound.onLocationFound(DataArray.list_searchResult.get(), locationNameList, false);
                else
                    locationFound.onNothingFound();

                PopWindowHelper.dismissPopWindow();
            }
        });
    }

    private static String getCombinedName(Address addr) {
        String locationName = "";

        if (addr.getThoroughfare() != null)
            locationName += addr.getThoroughfare();
        if (addr.getSubThoroughfare() != null) {
            if (addr.getSubThoroughfare().contains("號"))
                locationName += addr.getSubThoroughfare() + " ";
            else
                locationName += addr.getSubThoroughfare() + "號 ";
        }
        if (!addr.getFeatureName().equals(addr.getThoroughfare()) && !addr.getFeatureName().equals(addr.getSubThoroughfare()))
            locationName += addr.getFeatureName();

        return locationName;
    }
}