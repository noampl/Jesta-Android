package com.example.jesta.view.fragments.utilities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentAboutBinding;
import com.example.jesta.databinding.FragmentTermOfUseBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TermOfUseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TermOfUseFragment extends Fragment {

    //region members

    private FragmentTermOfUseBinding _binding;

    //end region

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_term_of_use, container, false);

        return _binding.getRoot();
    }
}