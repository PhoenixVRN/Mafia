package com.fenix.app.util;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

public final class LocationUtil {

    /**
     * Calculate distance in meters between two points
     *
     * @param pointA - Point A
     * @param pointB - Point B
     * @return - Distance in meters
     */
    public static float distance(LatLng pointA, LatLng pointB) {
        double lat_a = pointA.latitude;
        double lng_a = pointA.longitude;
        double lat_b = pointB.latitude;
        double lng_b = pointB.longitude;

        double earthRadius = 3958.75;

        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (float) (distance * meterConversion);
    }

    /**
     * Calculate map square number
     *
     * @param point - location
     * @param grain - square width in magic units
     * @return
     */
    public static String calcMapNumber(LatLng point, int grain) {

        int X = (int) (point.latitude * 1000 / grain);
        int Y = (int) (point.longitude * 1000 / grain);
        return X + "|" + Y;
    }
}
