package com.example.jesta.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MyApplication;
import com.example.jesta.R;
import com.example.jesta.databinding.FragmentMapBinding;
import com.example.jesta.viewmodel.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment {

    // region Members

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = _mapViewModel.getMyLocation().getValue();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            _mapViewModel.setGoogleMap(googleMap);
            if (ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
    };

    private MapViewModel _mapViewModel;
    private FragmentMapBinding _binding;

    // endregion

    // region LifeCycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        init();
    }

    // endregion

    private void init(){
        initObservers();
        initListeners();
    }

    private void initObservers(){
        _mapViewModel.getMyLocation().observe(getViewLifecycleOwner(), (ltlg)->{
            if (_mapViewModel.getGoogleMap() != null) {
                _mapViewModel.moveCamera(ltlg,10);
            }
        });
    }

    private void initListeners(){
        _binding.plusBtn.setOnClickListener((v)->
                Navigation.findNavController(requireActivity(), R.id.main_container).navigate(R.id.action_nav_map_to_addJestaFragment));
    }
}