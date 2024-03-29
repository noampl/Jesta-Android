package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.databinding.FragmentSummaryBinding;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.google.android.material.snackbar.Snackbar;

public class SummaryFragment extends Fragment {

    // region Members

    private FragmentSummaryBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (_createJestaViewModel != null) {
            _createJestaViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
        }
    }

    private void init() {
        _createJestaViewModel.validSummaryDetails();
        initBinding();
        initListeners();
        initObservers();
    }

    private void initBinding() {
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners() {
        _binding.finish.setOnClickListener(view -> {
            if (_createJestaViewModel.getImage1().getValue() != null) {
                _createJestaViewModel.initUploadImage(_createJestaViewModel.getImage1().getValue().second);
            }
            if (!_createJestaViewModel.validDate()){
                Snackbar.make(_binding.getRoot(), R.string.valid_date, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!_createJestaViewModel.createJesta()) {
                Snackbar.make(_binding.getRoot(), R.string.please_choose_category, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initObservers() {
        _createJestaViewModel.get_startDate().observe(getViewLifecycleOwner(), date -> {
            _binding.setSrcDate(date);
        });
        _createJestaViewModel.get_endDate().observe(getViewLifecycleOwner(), date -> {
            _binding.setDstDate(date);
        });
        _createJestaViewModel.get_serverInteractionResult().observe(getViewLifecycleOwner(), msg -> {
            if (msg.equals(Consts.INVALID_STRING))
                return;
            if (msg.equals(Consts.SUCCESS)) {
                _createJestaViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
                _createJestaViewModel.clearData();
                Snackbar.make(_binding.getRoot(), R.string.jesta_created_successfully, Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
            } else {
                Snackbar.make(_binding.getRoot(), R.string.error_occurred, Snackbar.LENGTH_SHORT).show();
                _createJestaViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
            }
        });
    }
}