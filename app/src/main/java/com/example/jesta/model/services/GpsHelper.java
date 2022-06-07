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

    private final MutableLiveData<LatLng> _myLocation;

    // endregion

    // region C'tor

    public GpsHelper(MutableLiveData<LatLng> _myLocation) {
        this._myLocation = _myLocation;
    }

    // endregion

    // region LocationListener

    @Override
    public void onLocationChanged(@NonNull Location location) {
        _myLocation.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
        MapRepository.getInstance().saveLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        _myLocation.setValue(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()));
        MapRepository.getInstance().saveLocation(locations.get(0).getLatitude(), locations.get(0).getLongitude());
    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    // endregion


}
