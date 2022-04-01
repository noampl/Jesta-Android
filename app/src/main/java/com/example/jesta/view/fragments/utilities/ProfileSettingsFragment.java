package com.example.jesta.view.fragments.utilities;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentProfileSettingsBinding;
import com.example.jesta.type.DateTime;
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;


public class ProfileSettingsFragment extends Fragment {

    // region Members

    private FragmentProfileSettingsBinding _binding;
    private UsersViewModel _usersViewModel;

    // endregion


    // region Lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_settings, container, false);
        _usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        init();
        return _binding.getRoot();
    }

    // endregion

    // region Private Methods

    private void init(){
        initBinding();
        initListeners();
        initObservers();
        initAutoComplete();
    }

    private void initBinding(){
        _binding.setUser(_usersViewModel.get_myUser().getValue());
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners(){
        _binding.nameCard.setOnClickListener(view -> {
            showDialog(R.string.full_name,_binding.nameTitle.getText().toString(), FiledType.NAME,
                    _binding.nameTxt.getText().toString());
        });
        _binding.emailCard.setOnClickListener(view -> {
            showDialog(R.string.email,_binding.emailTitle.getText().toString(), FiledType.EMAIL,
                    _binding.emailTxt.getText().toString());
        });
        _binding.phoneCard.setOnClickListener(view -> {
            showDialog(R.string.phone,_binding.phoneTitle.getText().toString(), FiledType.NAME,
                    _binding.phoneTxt.getText().toString());
        });
        _binding.birthdayCard.setOnClickListener(view -> {
            dateDialog();
        });
        _binding.imageCard.setOnClickListener(view -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            pickPhotoResultLauncher.launch(pickPhoto);
        });
        _binding.passwordCard.setOnClickListener(view -> {
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(R.id.action_nav_profile_settings_to_changePasswordDialogFragment);
        });
        _binding.deleteCard.setOnClickListener(view -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.are_u_sure_delete_user)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _usersViewModel.deleteAccount();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        });

    }

    private void initObservers(){
        _usersViewModel.get_myUser().observe(getViewLifecycleOwner(), u-> {
            _binding.setUser(u);});
        // Check is user is update my our changes
        _usersViewModel.get_isUserUpdated().observe(getViewLifecycleOwner(), isUpdated->{
            if (isUpdated)
                _binding.setUser(_usersViewModel.get_myUser().getValue());
        });
    }

    private void initAutoComplete(){
        AutocompleteSupportFragment autoCompleteSrcAddr = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.addr_autocomplete_fragment);
        autoCompleteSrcAddr.setHint(getString(R.string.address));
        if (_usersViewModel.get_myUser().getValue() != null &&
                _usersViewModel.get_myUser().getValue().get_address().fullAddress != null){
            autoCompleteSrcAddr.setText(_usersViewModel.get_myUser().getValue().get_address().fullAddress);
        }
        autoCompleteSrcAddr.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autoCompleteSrcAddr.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                System.out.println("peleg - error " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println("peleg - places selected " + place.getName());
                _usersViewModel.get_myUser().getValue().set_fullAddress(place.getAddress());
                _usersViewModel.updateUser();
            }
        });
    }

    /**
     * Show custom dialog for change paramater settings
     *
     * @param title The title of the dialog
     * @param hint The hint of the input text
     * @param type The type of the dialog
     * @param text
     */

    private void showDialog(int title, String hint, FiledType type, String text){
        ProfileSettingsFragmentDirections.ActionNavProfileSettingsToOneInputDialogFragment action =
                ProfileSettingsFragmentDirections.actionNavProfileSettingsToOneInputDialogFragment(text, hint, type.ordinal(), getString(title));
        Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
    }

    /**
     * Opens date picker dialog
     */
    private void dateDialog(){
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().
                setTitleText(R.string.birthday).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String date = _usersViewModel.convertDateSelection(selection);
            _binding.birthdayTxt.setText(date);
            _usersViewModel.get_myUser().getValue().set_birthday(date);
        });
        datePicker.show(getParentFragmentManager(),getString(R.string.birthday));
    }

    private final ActivityResultLauncher<Intent> pickPhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data != null ? data.getData() : null;
                        System.out.println("peleg - uri is " + uri);
                        // TODO change the profile image
//                        Picasso.with(_binding.getRoot().getContext()).load(uri).fit().into(_binding.profileImageButton);
//                        _filePath = uri;
                    }
                }
            }
    );

    // endregion
}