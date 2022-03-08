package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentWhenBindingImpl;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


public class WhenFragment extends Fragment {

    // region Members

    private FragmentWhenBindingImpl _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_when,container,false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);

        init();
        return _binding.getRoot();
    }

    private void init(){
        initBinding();
        initListeners();
    }

    private void initBinding(){
        _binding.setViewModel(_createJestaViewModel);
    }

    private void initListeners(){
        _binding.startDay.setOnClickListener(v->{MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().
                setTitleText(R.string.starting_day).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    _binding.startDay.setText(_createJestaViewModel.convertDateSelection(selection));
                    _createJestaViewModel.setStartDate(selection);
                });
                datePicker.show(getParentFragmentManager(),getString(R.string.starting_day));
        });

        _binding.endDay.setOnClickListener(v->{MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().
                setTitleText(R.string.end_date).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                _binding.endDay.setText(_createJestaViewModel.convertDateSelection(selection));
                _createJestaViewModel.setEndDate(selection);
            });
            datePicker.show(getParentFragmentManager(),getString(R.string.starting_day));
        });

        _binding.startTime.setOnClickListener(v->{
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                                                              .setTimeFormat(TimeFormat.CLOCK_24H)
                                                              .setHour(12)
                                                              .setMinute(0)
                                                              .setTitleText(getString(R.string.start_time))
                                                              .build();
            picker.addOnPositiveButtonClickListener(view -> {
               _binding.startTime.setText(_createJestaViewModel.convertTimeToText(picker.getHour(), picker.getMinute()));
            });
            picker.show(getParentFragmentManager(), getString(R.string.start_time));
        });
        _binding.endTime.setOnClickListener(v->{
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText(getString(R.string.end_time))
                    .build();
            picker.addOnPositiveButtonClickListener(view -> {
                _binding.endTime.setText(_createJestaViewModel.convertTimeToText(picker.getHour(), picker.getMinute()));
            });
            picker.show(getParentFragmentManager(), getString(R.string.end_time));
        });
    }

}