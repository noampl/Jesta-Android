package com.example.jesta.view.fragments.utilities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentFullImageBinding;

public class FullImageFragment extends Fragment {

    private FragmentFullImageBinding _binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_full_image, container, false);
        _binding.setImagePath(FullImageFragmentArgs.fromBundle(getArguments()).getImagePath());

        return _binding.getRoot();
    }
}