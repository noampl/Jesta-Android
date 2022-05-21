package com.example.jesta.view.fragments.utilities;

import static com.example.jesta.common.Consts.SERVER_PRE_FIX;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentUserProfileBinding;
import com.example.jesta.model.enteties.User;
import com.example.jesta.view.fragments.jestas.JestaDetailsFragmentArgs;
import com.example.jesta.viewmodel.JestaDetailsViewModel;
import com.example.jesta.viewmodel.NotificationViewModel;
import com.example.jesta.viewmodel.UserProfileViewModel;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfileFragment extends Fragment {

    //region Members

    private FragmentUserProfileBinding _binding;
    private UserProfileViewModel _userProfileViewModel;
    private String _userId = null;

    //endregion

    // region Lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        initVm();

        _userId = UserProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        _userProfileViewModel.getUser(_userId);
        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // endregion

    //region Private Methods

    private void init() {
        initObservers();
        initListeners();
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initVm() {
        _userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
    }

    private void initObservers() {
        _userProfileViewModel.get_userDetails().observe(getViewLifecycleOwner(), user -> {
            _binding.setUserDetails(user);
        });
    }

    private void initListeners() {
        _binding.btnReviews.setOnClickListener(v -> {
            UserProfileFragmentDirections.ActionNavUserProfileToCommentsFragment action =
                    UserProfileFragmentDirections.actionNavUserProfileToCommentsFragment(_userId);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
        });

    }

    //endregion

}