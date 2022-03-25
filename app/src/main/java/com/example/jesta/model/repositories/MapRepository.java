package com.example.jesta.model.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.MyApplication;
import com.example.jesta.model.services.GpsHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository handling google maps stuff
 */
public class MapRepository {

    // region Members

    private GoogleMap _googleMap;
    private MutableLiveData<LatLng> _myLocation;
    private final LocationManager _locationManager;
    private Geocoder _geoCoder;
    private ExecutorService _executorService;

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
        _geoCoder = new Geocoder(MyApplication.getAppContext(), Locale.forLanguageTag("he"));
        _executorService = Executors.newFixedThreadPool(2);
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

    // region Public Method

    public void getAddressByName(String address) {
        _executorService.execute(()->{
            try {
               List<Address> addressList = _geoCoder.getFromLocationName(address, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO publish addresses
        });
    }

    public Address setAddressByCurrentLocation(){
        return getAddressByLocation(_myLocation.getValue());
    }

    public Address getAddressByLocation(LatLng latLng) {
        List<Address> addressList = new ArrayList<>();
        try {
             addressList = _geoCoder.getFromLocation(latLng.latitude, latLng.longitude,10);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return addressList.get(0);
    }

}
