package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.repositories.MapRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewModel extends ViewModel {

    // region Members

    private GoogleMap _googleMap;
    private MutableLiveData<LatLng> _myLocation;

    // endregion

    // region C'tor

    public MapViewModel() {
        _googleMap = MapRepository.getInstance().getGoogleMap();
        _myLocation = MapRepository.getInstance().getMyLocation();
    }

    // endregion

    // region Properties

    public GoogleMap getGoogleMap() {
        return _googleMap;
    }

    public void setGoogleMap(GoogleMap _googleMap) {
        this._googleMap = _googleMap;
    }

    public MutableLiveData<LatLng> getMyLocation() {
        return _myLocation;
    }

    public void setMyLocation(LatLng _myLocation) {
        this._myLocation.setValue(_myLocation);
    }

    // endregion

    // region Public Methods

    /**
     * Moves the google maps camera to the position
     * @param latLng The new position
     */
    public void moveCamera(LatLng latLng){
        _googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Moves the google maps camera to position with a specific zoom
     *
     * @param latLng The Position
     * @param zoom The Zoom
     */
    public void moveCamera(LatLng latLng, int zoom){
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Add Marker to the map
     *
     * @param position The position of the marker
     * @param title The title of the marker
     */
    public void AddMarker(LatLng position, String title){
        _googleMap.addMarker(new MarkerOptions().position(position).title(title));
    }

    // endregion

}
