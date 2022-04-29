package com.example.jesta.view.fragments.utilities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentUserProfileBinding;

public class UserProfileFragment extends Fragment {

    //region Members

    private FragmentUserProfileBinding _binding;

    //endregion

    // region Lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        // TODO: add view model

        init();
        return _binding.getRoot();
    }

    // endregion

    //region Private Methods

    private void init() {
        initBinding();
        initListeners();
        initObservers();
    }

    private void initBinding() {

    }

    private void initListeners() {

    }

    private void initObservers() {

    }

    //endregion

}