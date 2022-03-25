package com.example.jesta.model.services;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.repositories.MapRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GpsHelper implements LocationListener {

    // region Members

    private MutableLiveData<LatLng> _myLocation;

    // endregion

    // region C'tor

    public GpsHelper(MutableLiveData<LatLng> _myLocation) {
        this._myLocation = _myLocation;
    }

    // endregion

    // region LocationListener

    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println("peleg - onLocationChanged " + " " + location.toString());
        _myLocation.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        System.out.println("peleg - onLocationsssChanged " + Thread.currentThread().getName());
//        _myLocation.setValue(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()));
    }

    @Override
    public void onFlushComplete(int requestCode) {
        System.out.println("peleg - onFlushComplete " + requestCode +" " + Thread.currentThread().getName());

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        System.out.println("peleg - onProviderEnabled " + provider + " " + Thread.currentThread().getName());

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        System.out.println("peleg - onProviderDisabled " + provider +" " + Thread.currentThread().getName());

    }

    // endregion
}
