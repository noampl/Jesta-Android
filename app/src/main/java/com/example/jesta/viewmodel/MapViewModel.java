package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.common.Consts;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.repositories.GrahpqlRepository;
import com.example.jesta.model.repositories.JestaRepository;
import com.example.jesta.model.repositories.MapRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapViewModel extends ViewModel {

    // region Members

    private GoogleMap _googleMap;
    private MutableLiveData<LatLng> _myLocation;
    private MutableLiveData<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>> _jestas;
    private Double radiusInKm = 100D;
    private HashMap<Marker,GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable > _markerToJesta;
    private INavigationHelper _navigationHelper;

    // endregion

    // region C'tor

    public MapViewModel() {
        _googleMap = MapRepository.getInstance().getGoogleMap();
        _myLocation = MapRepository.getInstance().getMyLocation();
        _jestas = JestaRepository.getInstance().get_jestas();
        _markerToJesta = MapRepository.getInstance().getMarkerTOJesta();
    }

    // endregion

    // region Properties

    public INavigationHelper get_navigationHelper() {
        return _navigationHelper;
    }

    public void set_navigationHelper(INavigationHelper _navigationHelper) {
        this._navigationHelper = _navigationHelper;
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

    public void setMyLocation(LatLng _myLocation) {
        this._myLocation.setValue(_myLocation);
    }

    public MutableLiveData<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>> get_jestas() {
        return _jestas;
    }

    public void set_jestas(List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> _jestas) {
        this._jestas.setValue(_jestas);
    }

    public HashMap<Marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable > get_markerToJesta() {
        return _markerToJesta;
    }

    public void addMarkerAndJesta(Marker marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable  jesta){
        _markerToJesta.put(marker, jesta);
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

    public void getRemoteJestas(){
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(_myLocation.getValue().latitude);
        coordinates.add(_myLocation.getValue().longitude);
        GrahpqlRepository.getInstance().GetRemoteJestas(new Optional.Present<>(coordinates),new Optional.Present<Double>(radiusInKm));
    }

    public void markerClicked(String jestaId){
        _navigationHelper.navigate(jestaId);
    }

    // endregion

}
