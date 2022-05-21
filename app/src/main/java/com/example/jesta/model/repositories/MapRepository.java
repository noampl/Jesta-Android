package com.example.jesta.model.repositories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.MyApplication;
import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.model.services.GpsHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final MutableLiveData<LatLng> _myLocation;
    private final LocationManager _locationManager;
    private HashMap<Marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> _markerToJesta;
    private Geocoder _geoCoder;
    private ExecutorService _executorService;
    private MutableLiveData<Double> radiusInKm;
    private Circle _circle;

    // endregion

    // region Singleton

    private static MapRepository instance = null;

    public static MapRepository getInstance() {
        if (instance == null)
            instance = new MapRepository();
        return instance;
    }

    private MapRepository() {
        _myLocation = new MutableLiveData<>(new LatLng(ShardPreferencesHelper.readLat(), ShardPreferencesHelper.readLng()));
        _locationManager = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // NOTE: Permission is handled in startup.
            return;
        }
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, new GpsHelper(_myLocation));
        _geoCoder = new Geocoder(MyApplication.getAppContext(), Locale.forLanguageTag("he"));
        _executorService = Executors.newFixedThreadPool(2);
        _markerToJesta = new HashMap<>();
        radiusInKm = new MutableLiveData<>(ShardPreferencesHelper.readRadius());
    }

    // endregion

    // region Properties

    public Circle get_circle() {
        return _circle;
    }

    public void set_circle(Circle _circle) {
        this._circle = _circle;
    }

    public MutableLiveData<Double> getRadiusInKm() {
        return radiusInKm;
    }

    public GoogleMap getGoogleMap() {
        return _googleMap;
    }

    public void setGoogleMap(GoogleMap _googleMap) {
        this._googleMap = _googleMap;
    }

    public MutableLiveData<LatLng> getMyLocation() {
        return _myLocation;
    }


    public HashMap<Marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> getMarkerTOJesta() {
        return _markerToJesta;
    }

    // endregion

    // region Public Method

    public void getAddressByName(String address) {
        _executorService.execute(() -> {
            try {
                List<Address> addressList = _geoCoder.getFromLocationName(address, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO publish addresses
        });
    }

    public Address setAddressByCurrentLocation() {
        return getAddressByLocation(_myLocation.getValue());
    }

    public Address getAddressByLocation(LatLng latLng) {
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = _geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return addressList.get(0);
    }

    public void saveLocation(double latitude, double longitude) {
        _executorService.execute(() -> {
            ShardPreferencesHelper.writeLat(latitude);
            ShardPreferencesHelper.writeLng(longitude);
        });
    }

    public void saveRadius(double radiusInKm) {
        _executorService.execute(() -> {
            ShardPreferencesHelper.writeRadius(radiusInKm);
        });
    }
}
