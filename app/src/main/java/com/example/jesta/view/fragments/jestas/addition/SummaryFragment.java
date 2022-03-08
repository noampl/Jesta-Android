package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentSummaryBinding;
import com.example.jesta.viewmodel.CreateJestaViewModel;

public class SummaryFragment extends Fragment {

    // region Members

    private FragmentSummaryBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary,container,false);
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
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners(){
        _binding.finish.setOnClickListener(view -> {
            // TODO implement addition of jesta
        });
    }
}