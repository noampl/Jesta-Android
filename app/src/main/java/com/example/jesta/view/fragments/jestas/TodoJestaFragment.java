package com.example.jesta.view.fragments.jestas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentGenericJestasListBinding;

public class TodoJestaFragment extends Fragment {

    private FragmentGenericJestasListBinding _binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generic_jestas_list, container, false);
    }

}