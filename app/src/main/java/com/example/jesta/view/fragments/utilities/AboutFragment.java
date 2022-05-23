package com.example.jesta.view.fragments.utilities;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    //region members

    private FragmentAboutBinding _binding;

    //end region

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_about, container, false);

        return _binding.getRoot();
    }
}
