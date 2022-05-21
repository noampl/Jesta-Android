package com.example.jesta.view.fragments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.FragmentMapBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Address;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.view.adapters.JestaAdapter;
import com.example.jesta.viewmodel.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, INavigationHelper {

    // region Members

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = _mapViewModel.getMyLocation().getValue();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
            _mapViewModel.setGoogleMap(googleMap);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // NOTE: Permission is handled in startup.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            _mapViewModel.postMapFinish(true);
        }
    };

    private MapViewModel _mapViewModel;
    private FragmentMapBinding _binding;
    private GoogleMap.OnMarkerClickListener _markerClickListener;
    private Snackbar _snackbarLocationRequest;

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            this.permissionSafeInit();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            this.permissionSafeInit();
                        } else {
                            // No location access granted.
                            _snackbarLocationRequest = Snackbar.make(_binding.getRoot(), R.string.location_permission_is_needed, Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.red))
                                    .setAction(R.string.allow, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Takes the user to the application settings screen:
                                            Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getString(R.string.app_package), null);
                                            appSettingsIntent.setData(uri);
                                            startActivity(appSettingsIntent);

                                            initRequestPermission();
                                        }
                                    });
                            _snackbarLocationRequest.show();
                        }
                    }
            );

    // endregion

    // region LifeCycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request permissions:
        this.initRequestPermission();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.initRequestPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        _mapViewModel.set_radius(null);
        _mapViewModel.getGoogleMap().clear();
    }

        if (_mapViewModel != null)
            _mapViewModel.set_radius(null);
    }

    // endregion

    // region Private Methods

    private void initRequestPermission() {
        // Checks the user has location permission:
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        } else {
            permissionSafeInit();
        }
    }

    private void permissionSafeInit() {
        if (_snackbarLocationRequest != null && _snackbarLocationRequest.isShown()) {
            _snackbarLocationRequest.dismiss();
        }

        _markerClickListener = this;
        _mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        _mapViewModel.getRemoteJestas();
        _mapViewModel.set_navigationHelper(this);

        initCircle();
        initObservers();
        initListeners();
    }

    private void initObservers() {
        JestaAdapter adapter = new JestaAdapter(getViewLifecycleOwner(), _mapViewModel);
        _mapViewModel.getMyLocation().observe(getViewLifecycleOwner(), (ltlg) -> {
            if (_mapViewModel.getGoogleMap() != null) {
                System.out.println("peleg - location change");
                addMapRadius(ltlg, _mapViewModel.getRadiusInKm().getValue());
            }
        });
        _mapViewModel.get_jestas().observe(getViewLifecycleOwner(), new Observer<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> jestas) {
                List<Jesta> jestaList = new ArrayList<>();
                jestas.forEach(j -> jestaList.add(new Jesta(j._id, j.status, j.ownerId,
                        new Address(j.sourceAddress.fullAddress, j.sourceAddress.location.coordinates),
                        j.numOfPeopleNeeded, j.dateToExecute != null ? j.dateToExecute.toString() : null
                        , j.dateToFinishExecute != null ? j.dateToFinishExecute.toString() : null)));
                adapter.submitList(jestaList);
                adapter.notifyDataSetChanged();
                if (_mapViewModel.getGoogleMap() != null) {
                    _mapViewModel.get_markerToJesta().forEach((marker, v) -> marker.remove());
                    if (_mapViewModel.get_markerToJesta() != null) {
                        _mapViewModel.get_markerToJesta().clear();
                    }
                    jestas.forEach(j -> {
                        Marker marker = _mapViewModel.getGoogleMap().addMarker(
                                new MarkerOptions()
                                        .position(new LatLng(j.sourceAddress.location.coordinates.get(0),
                                                j.sourceAddress.location.coordinates.get(1)))
                        );
                        _mapViewModel.addMarkerAndJesta(marker, j);
                    });
                }
            }
        });
        _binding.jestaLst.setAdapter(adapter);


        // TODO: noam check, without this I get null reference exception in startup
        if (_mapViewModel.getRadiusInKm() != null) {
            _mapViewModel.getRadiusInKm().observe(getViewLifecycleOwner(), r -> {
                if (_mapViewModel.getGoogleMap() != null) {
                    System.out.println("peleg - radius change " + r);
                    addMapRadius(_mapViewModel.getMyLocation().getValue(), r);
                }
            });
        }
    }

    private void initListeners() {
        _binding.plusBtn.setOnClickListener((v) ->
                Navigation.findNavController(requireActivity(), R.id.main_container).navigate(R.id.action_nav_map_to_addJestaFragment));

        _binding.filter.setOnClickListener((v) ->
                Navigation.findNavController(requireActivity(), R.id.main_container).navigate(R.id.action_nav_map_to_radiusSelectorDialogFragment));

    }

    // endregion

    // region OnMarkerClickListener

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        openJestaDetails(_mapViewModel.get_markerToJesta().get(marker)._id);
        return true;
    }

    // endregion

    // region NavigationHelper

    @Override
    public void navigate(String[] arg) {
        openJestaDetails(arg[0]);
    }

    // endregion

    // region Private Methods

    private void openJestaDetails(String id) {
        MapFragmentDirections.ActionNavMapToJestaDetailsFragment action =
                MapFragmentDirections.actionNavMapToJestaDetailsFragment(id);
        Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
    }

    private void initCircle() {
        _mapViewModel.get_mapFinish().observe(getViewLifecycleOwner(), b -> {
            if (b) {
                createCircleRadius(_mapViewModel.getMyLocation().getValue(),
                        _mapViewModel.getRadiusInKm().getValue());
            }
        });
        if (_mapViewModel.get_mapFinish().getValue()) {
            createCircleRadius(_mapViewModel.getMyLocation().getValue(),
                    _mapViewModel.getRadiusInKm().getValue());
        }
    }

    private void addMapRadius(LatLng center, double radius) {
        if (_mapViewModel.get_radius() != null) {
            _mapViewModel.get_radius().setCenter(center);
            _mapViewModel.get_radius().setRadius(radius * 1000);
        }
    }

    private void createCircleRadius(LatLng center, double radius) {
        if (_mapViewModel.get_radius() == null)
            _mapViewModel.set_radius(_mapViewModel.getGoogleMap().addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius * 1000)
                    .strokeWidth(3f)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(70, 50, 50, 120))));
    }

    // endregion
}