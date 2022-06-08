package com.example.jesta.viewmodel;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.JestaRepository;
import com.example.jesta.model.repositories.MapRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapViewModel extends ViewModel {

    // region Members

    private GoogleMap _googleMap;
    private final MutableLiveData<LatLng> _myLocation;
    private final MutableLiveData<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>> _jestas;
    private final MutableLiveData<Double> radiusInKm;
    private final HashMap<Marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> _markerToJesta;
    private INavigationHelper _navigationHelper;
    private Circle _radius;
    private final MutableLiveData<Boolean> _mapFinish;

    // endregion

    // region C'tor

    public MapViewModel() {
        _mapFinish = new MutableLiveData<>(false);
        radiusInKm = MapRepository.getInstance().getRadiusInKm();
        _googleMap = MapRepository.getInstance().getGoogleMap();
        _myLocation = MapRepository.getInstance().getMyLocation();
        _jestas = JestaRepository.getInstance().get_jestas();
        _markerToJesta = MapRepository.getInstance().getMarkerTOJesta();
        _radius = MapRepository.getInstance().get_circle();
    }

    // endregion

    // region Properties

    public MutableLiveData<Boolean> get_mapFinish() {
        return _mapFinish;
    }

    public void postMapFinish(boolean mapFinish) {
        this._mapFinish.postValue(mapFinish);
    }

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

    public HashMap<Marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> get_markerToJesta() {
        return _markerToJesta;
    }

    public void addMarkerAndJesta(Marker marker, GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable jesta) {
        if (_markerToJesta != null) {
            _markerToJesta.put(marker, jesta);
        }
    }

    public MutableLiveData<Double> getRadiusInKm() {
        return radiusInKm;
    }

    public void setRadiusInKm(double radiusInKm) {
        this.radiusInKm.setValue(radiusInKm);
        MapRepository.getInstance().saveRadius(radiusInKm);
    }

    public Circle get_radius() {
        return _radius;
    }

    public void set_radius(Circle _radius) {
        this._radius = _radius;
    }

    // endregion

    // region Public Methods

    /**
     * Moves the google maps camera to the position
     *
     * @param latLng The new position
     */
    public void moveCamera(LatLng latLng) {
        _googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Moves the google maps camera to position with a specific zoom
     *
     * @param latLng The Position
     * @param zoom   The Zoom
     */
    public void moveCamera(LatLng latLng, int zoom) {
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Add Marker to the map
     *
     * @param position The position of the marker
     * @param title    The title of the marker
     */
    public void AddMarker(LatLng position, String title) {
        _googleMap.addMarker(new MarkerOptions().position(position).title(title));
    }

    public void getRemoteJestas() {
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(_myLocation.getValue().latitude);
        coordinates.add(_myLocation.getValue().longitude);
        double radius = 50;
        if (radiusInKm != null){
            radius = radiusInKm.getValue();
        }
        GraphqlRepository.getInstance().GetRemoteJestas(new Optional.Present<>(coordinates), radius);

    }

    public void markerClicked(String jestaId, String transactionId) {
        String[] args = {jestaId, transactionId};
        _navigationHelper.navigate(args);
    }

    public void submitRadiusChange(double radius) {
        setRadiusInKm(radius);
        List<Double> center = new ArrayList<>();
        center.add(MapRepository.getInstance().getMyLocation().getValue().latitude);
        center.add(MapRepository.getInstance().getMyLocation().getValue().longitude);
        GraphqlRepository.getInstance().GetRemoteJestas(new Optional.Present<>(center), radius);
    }

    // endregion

}
