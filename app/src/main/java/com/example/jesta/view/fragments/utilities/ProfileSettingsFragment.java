package com.example.jesta.view.fragments.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jesta.R;
import com.example.jesta.common.AlertDialogRtlHelper;
import com.example.jesta.common.IntentUtils;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentProfileSettingsBinding;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.type.DateTime;
import com.example.jesta.view.activities.LoginRegisterActivity;
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import okio.Okio;
import okio.Source;


public class ProfileSettingsFragment extends Fragment {

    // region Members

    private FragmentProfileSettingsBinding _binding;
    private UsersViewModel _usersViewModel;
    private final IDialogConsumerHelper nameConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            String[] names = val.split(" ");
            String lastName = "";
            for (int index = 1; index < names.length; index++) {
                lastName += lastName + " " + names[index];
            }
            _usersViewModel.get_myUser().getValue().set_firstName(names[0]);
            _usersViewModel.get_myUser().getValue().set_lastName(lastName);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper emailConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_myUser().getValue().set_email(val);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper phoneConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_myUser().getValue().set_phone(val);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper descriptionConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_myUser().getValue().setDescription(val);
            _usersViewModel.updateUser();
        }
    };

    // endregion

    //region Activity Result Launchers

    private final ActivityResultLauncher<Intent> _cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                try {
                    uploadImage(result.getData().getData()); // TODO figure if this working
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> _galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                try {
                    uploadImage(result.getData().getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    //endregion

    // region Lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_settings, container, false);
        _usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        init();
        return _binding.getRoot();
    }

    // endregion

    // region Private Methods

    private void init() {
        initBinding();
        initListeners();
        initObservers();
        initAutoComplete();
    }

    private void initBinding() {
        _binding.setUser(_usersViewModel.get_myUser().getValue());
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners() {
        _binding.nameCard.setOnClickListener(view -> {
            showDialog(R.string.full_name, _binding.nameTitle.getText().toString(), FiledType.NAME,
                    _binding.nameTxt.getText().toString(), nameConsumer);
        });
        _binding.emailCard.setOnClickListener(view -> {
            showDialog(R.string.email, _binding.emailTitle.getText().toString(), FiledType.EMAIL,
                    _binding.emailTxt.getText().toString(), emailConsumer);
        });
        _binding.phoneCard.setOnClickListener(view -> {
            showDialog(R.string.phone, _binding.phoneTitle.getText().toString(), FiledType.NAME,
                    _binding.phoneTxt.getText().toString(), phoneConsumer);
        });
        _binding.birthdayCard.setOnClickListener(view -> {
            dateDialog();
        });
        _binding.descriptionCard.setOnClickListener(view -> {
            showDialog(R.string.short_description_about_yourself, _binding.descriptionTitle.getText().toString(), FiledType.DESCRIPTION,
                    _binding.descriptionTxt.getText().toString(), descriptionConsumer);
        });
        _binding.imageCard.setOnClickListener(view -> {
            this.openImageOptionsDialog();
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
                            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(R.id.nav_map); // TODO figure if need to add server interaction msg
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

        _binding.logoutCard.setOnClickListener(v -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.are_u_sure_logout_user)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _usersViewModel.logout();
                            dialogInterface.dismiss();
                            Intent intent = new Intent(requireActivity(), LoginRegisterActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
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

    private void initObservers() {
        _usersViewModel.get_myUser().observe(getViewLifecycleOwner(), u -> {
            _binding.setUser(u);
        });
        // Check is user is update my our changes
        _usersViewModel.get_isUserUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated)
                _binding.setUser(_usersViewModel.get_myUser().getValue());
        });
    }

    private void initAutoComplete() {
        AutocompleteSupportFragment autoCompleteSrcAddr = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.addr_autocomplete_fragment);

        // Removes the search icon:
        ImageView searchIcon = (ImageView) ((LinearLayout) autoCompleteSrcAddr.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);
        // Makes the layout direction RTL:
        autoCompleteSrcAddr.requireView().setTextDirection(View.TEXT_DIRECTION_RTL);

        autoCompleteSrcAddr.setHint(getString(R.string.address));
        if (_usersViewModel.get_myUser().getValue() != null && _usersViewModel.get_myUser().getValue().get_address() != null) {
            autoCompleteSrcAddr.setText(_usersViewModel.get_myUser().getValue().get_address().fullAddress);
        }
        autoCompleteSrcAddr.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autoCompleteSrcAddr.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
               Log.e("OnPlaceSelectedListener", status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                _usersViewModel.get_myUser().getValue().set_fullAddress(place.getAddress());
                _usersViewModel.updateUser();
            }
        });
    }

    /**
     * Show custom dialog for change paramater settings
     *
     * @param title The title of the dialog
     * @param hint  The hint of the input text
     * @param type  The type of the dialog
     * @param text
     */
    private void showDialog(int title, String hint, FiledType type, String text, IDialogConsumerHelper dialogConsumerHelper) {
        _usersViewModel.set_dialogConsumerHelper(dialogConsumerHelper);
        ProfileSettingsFragmentDirections.ActionNavProfileSettingsToOneInputDialogFragment action =
                ProfileSettingsFragmentDirections.actionNavProfileSettingsToOneInputDialogFragment(text, hint, getString(title));
        action.setFiledType(type.ordinal());
        Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
    }

    /**
     * Opens date picker dialog
     */
    private void dateDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().
                setTitleText(R.string.birthday).setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String date = _usersViewModel.convertDateSelection(selection);
            _binding.birthdayTxt.setText(date);
            _usersViewModel.get_myUser().getValue().set_birthday(date);
        });
        datePicker.show(getParentFragmentManager(), getString(R.string.birthday));
    }

    /**
     * Opens the image options dialog.
     */
    private void openImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String[] imageOptions;
        if (_usersViewModel.get_myUser().getValue().get_imagePath() != null) {
            builder.setTitle(R.string.image_change);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery), getString(R.string.delete_image)};
        } else {
            builder.setTitle(R.string.image_upload);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery)};
        }
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    // Camera:
                    case 0:
                        Intent cameraIntent = IntentUtils.camera(requireContext());
                        if (cameraIntent != null) {
                            _cameraLauncher.launch(cameraIntent);
                        } else {
                            Snackbar.make(requireView(), R.string.camera_intent_not_found, Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    // Gallery:
                    case 1:
                        Intent galleryIntent = IntentUtils.gallery(requireContext());
                        if (galleryIntent != null) {
                            _galleryLauncher.launch(galleryIntent);
                        } else {
                            Snackbar.make(requireView(), R.string.gallery_intent_not_found, Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    // Delete Image:
                    case 2:
                        // TODO: remove image
                        break;
                    default:
                        throw new IndexOutOfBoundsException("The option index is not implemented in the image options dialog.");
                }
            }
        });
        AlertDialogRtlHelper.make(builder).show();
    }

    private void uploadImage(Uri imageUri) throws FileNotFoundException {
        InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
        Source source = Okio.source(imageStream);
        _usersViewModel.updateUserImage(source);
    }

    // endregion
}