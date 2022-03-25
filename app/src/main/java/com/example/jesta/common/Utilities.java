package com.example.jesta.common;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Utilities {

    /**
     * REturn the distance between to coordinates in Meters
     *
     * @param p1 First point represent as List
     * @param p2 Second point represent as Latlng
     *
     * @return The distance beteen p1 -p2 in Meters
     */
    public static double calcCoordinateDistance(List<Double> p1, LatLng p2) {
        double R = 6371000; // Radius of the earth in k
        double dLat = deg2rad(p1.get(0)- p2.latitude);  // deg2rad below
        double dLon = deg2rad(p1.get(1)- p2.longitude);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(p2.latitude)) * Math.cos(deg2rad(p1.get(0))) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in m
        return d;
    }

    private static double deg2rad(double deg){
        return deg * (Math.PI/180);
    }
}
