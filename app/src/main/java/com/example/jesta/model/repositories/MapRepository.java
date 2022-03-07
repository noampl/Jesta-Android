package com.example.jesta.model.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.MyApplication;
import com.example.jesta.model.services.GpsHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Repository handling google maps stuff
 */
public class MapRepository {

    // region Members

    private GoogleMap _googleMap;
    private MutableLiveData<LatLng> _myLocation;
    private final LocationManager _locationManager;

    // endregion

    // region Singleton

    private static MapRepository instance = null;

    public static MapRepository getInstance() {
        if (instance == null)
            instance = new MapRepository();
        return instance;
    }

    private MapRepository() {
        _myLocation = new MutableLiveData<>(new LatLng(32, 35));
        _locationManager = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3, new GpsHelper(_myLocation));
    }

    // endregion

    // region Properties

    public GoogleMap getGoogleMap() {
        return _googleMap;
    }

    public void setGoogleMap(GoogleMap _googleMap) {
        this._googleMap = _googleMap;
    }

    public MutableLiveData<LatLng> getMyLocation(){
        return _myLocation;
    }


    // endregion


}
