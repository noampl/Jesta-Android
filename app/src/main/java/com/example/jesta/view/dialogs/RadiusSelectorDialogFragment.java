package com.example.jesta.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.DialogMyRaitingBinding;
import com.example.jesta.databinding.FragmentRadiusSelectorDialogBinding;
import com.example.jesta.viewmodel.MapViewModel;
import com.example.jesta.viewmodel.NotificationViewModel;

public class RadiusSelectorDialogFragment extends DialogFragment {

    // region Members

    private AlertDialog _dialog;
    private FragmentRadiusSelectorDialogBinding _binding;
    private MapViewModel _mapViewModel;

    // endregion

    // region LifeCycle

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        _dialog = builder.create();
        return _dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_radius_selector_dialog, container, false);
        _dialog.setView(_binding.getRoot());
        _mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        init();
        return _binding.getRoot();

    }

    private void init() {
        initBtn();
        intListeners();
    }

    private void initBtn(){
        double radius = _mapViewModel.getRadiusInKm().getValue();

        if (radius == 0.5){
            _binding.first.setCardBackgroundColor(Color.BLACK);
            _binding.firstVal.setTextColor(Color.WHITE);
        }
        else if (radius == 1){
            _binding.second.setCardBackgroundColor(Color.BLACK);
            _binding.secoandVal.setTextColor(Color.WHITE);
        }
        else if (radius == 10){
            _binding.third.setCardBackgroundColor(Color.BLACK);
            _binding.thirdVal.setTextColor(Color.WHITE);
        }
        else if(radius == 50){
            _binding.fourth.setCardBackgroundColor(Color.BLACK);
            _binding.fourthVal.setTextColor(Color.WHITE);
        }
        else{
            Log.e("FragmentRadiusSelectorDialogBinding", "Unrecognized radius");
        }
    }

    private void intListeners() {
        _binding.first.setOnClickListener(v -> {
            selectRadius(0.5);

        });
        _binding.second.setOnClickListener(v -> {
            selectRadius(1);

        });
        _binding.third.setOnClickListener(v -> {
            selectRadius(10);

        });
        _binding.fourth.setOnClickListener(v -> {
            selectRadius(50);
        });

    }

    private void selectRadius(double radius){
        _mapViewModel.submitRadiusChange(radius);
        dismiss();
    }
}