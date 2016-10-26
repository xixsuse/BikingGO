package com.kingwaytek.cpami.biking.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Brack on 2016/1/10.
 */
public class PolyHelper {

    public static ArrayList<LatLng> decodePolyLine(String polyLine) {
        ArrayList<LatLng> poly = new ArrayList<>();

        int index = 0, len = polyLine.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dLat;

            shift = 0;
            result = 0;
            do {
                b = polyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dLng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));

            poly.add(p);
        }
        return poly;
    }
}
