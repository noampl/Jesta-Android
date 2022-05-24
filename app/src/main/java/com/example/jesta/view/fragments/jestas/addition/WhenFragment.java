package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.FragmentWhenBinding;
import com.example.jesta.databinding.FragmentWhenBindingImpl;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Date;


public class WhenFragment extends Fragment {

    // region Members

    private FragmentWhenBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_when, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);

        init();
        return _binding.getRoot();
    }

    private void init() {
        initBinding();
        initListeners();
    }

    private void initBinding() {
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());

    }

    private void initListeners() {
        _binding.startDay.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().
                    setTitleText(R.string.starting_day).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build())
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                _binding.startDay.setText(_createJestaViewModel.convertDateSelection(selection));
                _createJestaViewModel.set_startDate(selection);
                _createJestaViewModel.set_endDate(null);
            });
            datePicker.show(getParentFragmentManager(), getString(R.string.starting_day));
        });

        _binding.endDay.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker;
            if (_createJestaViewModel.get_startDate().getValue() != null) {
                datePicker = MaterialDatePicker.Builder.datePicker().
                        setTitleText(R.string.end_date).setSelection(_createJestaViewModel.get_startDate().getValue().getTime())
                        .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(
                                _createJestaViewModel.get_startDate().getValue().getTime())).build())
                        .build();
            } else {
                datePicker = MaterialDatePicker.Builder.datePicker().
                        setTitleText(R.string.end_date).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build())
                        .build();
            }

            datePicker.addOnPositiveButtonClickListener(selection -> {
                _binding.endDay.setText(_createJestaViewModel.convertDateSelection(selection));
                _createJestaViewModel.set_endDate(selection);
            });
            datePicker.show(getParentFragmentManager(), getString(R.string.starting_day));
        });

        _binding.startTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                    .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                    .setTitleText(getString(R.string.start_time))
                    .build();
            picker.addOnPositiveButtonClickListener(view -> {
                _binding.startTime.setText(_createJestaViewModel.convertTimeToText(picker.getHour(), picker.getMinute()));
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.HOUR, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                _createJestaViewModel.set_startTime(calendar.getTimeInMillis());
            });
            picker.show(getParentFragmentManager(), getString(R.string.start_time));
        });
        _binding.endTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                    .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                    .setTitleText(getString(R.string.end_time))
                    .build();
            picker.addOnPositiveButtonClickListener(view -> {
                _binding.endTime.setText(_createJestaViewModel.convertTimeToText(picker.getHour(), picker.getMinute()));
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.HOUR, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                _createJestaViewModel.set_endTime(calendar.getTimeInMillis());

            });
            picker.show(getParentFragmentManager(), getString(R.string.end_time));
        });
    }

}