package com.example.jesta.view.fragments.jestas.addition;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentWhereBinding;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


public class WhereFragment extends Fragment {

    // region

    private FragmentWhereBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_where, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);

        init();
        return _binding.getRoot();
    }

    private void init(){
        initBinding();
        initAutoComplete();
    }

    private void initBinding(){
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initAutoComplete(){
        // src auto complete
        AutocompleteSupportFragment autoCompleteSrcAddr = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoComplete_fragment_src);
        autoCompleteSrcAddr.setHint(getString(R.string.src_address));
        if (_createJestaViewModel.get_source().getValue() != null){
            autoCompleteSrcAddr.setText(_createJestaViewModel.get_source().getValue().getAddress());
        }
        autoCompleteSrcAddr.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autoCompleteSrcAddr.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                System.out.println("peleg - error " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println("peleg - places selected " + place.getName());
                _createJestaViewModel.set_source(place);
                // TODO use the places
            }
        });
        // dst auto complete
        AutocompleteSupportFragment autoCompleteDstAddr = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoComplete_fragment_dst);
        autoCompleteDstAddr.setHint(getString(R.string.dst_address));
        if (_createJestaViewModel.get_destention().getValue() != null){
            autoCompleteSrcAddr.setText(_createJestaViewModel.get_destention().getValue().getAddress());
        }
        autoCompleteDstAddr.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autoCompleteDstAddr.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                System.out.println("peleg - error " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println("peleg - places selected " + place.getName());
                _createJestaViewModel.set_destention(place);
                // TODO use the places
            }
        });
    }
}