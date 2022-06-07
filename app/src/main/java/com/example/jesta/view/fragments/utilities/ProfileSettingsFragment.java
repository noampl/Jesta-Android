package com.example.jesta.view.fragments.utilities;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.common.AlertDialogRtlHelper;
import com.example.jesta.common.Consts;
import com.example.jesta.common.IntentUtils;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentProfileSettingsBinding;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.view.activities.LoginRegisterActivity;
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
            _usersViewModel.get_localUser().getValue().set_firstName(names[0]);
            _usersViewModel.get_localUser().getValue().set_lastName(lastName);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper emailConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_localUser().getValue().set_email(val);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper phoneConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_localUser().getValue().set_phone(val);
            _usersViewModel.updateUser();
        }
    };
    private final IDialogConsumerHelper descriptionConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _usersViewModel.get_localUser().getValue().setDescription(val);
            _usersViewModel.updateUser();
        }
    };

    // endregion

    //region Activity Result Launchers

    private final ActivityResultLauncher<Intent> _cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                if (bitmap != null)
                    _usersViewModel.updateUserImage(bitmap);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (_usersViewModel != null)
            _usersViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
    }

    // endregion

    // region Private Methods

    private void init() {
        initBinding();
        initListeners();
        initObservers();
    }

    private void initBinding() {
        _binding.setUser(_usersViewModel.get_localUser().getValue());
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
        _binding.descriptionCard.setOnClickListener(view -> {
            showDialog(R.string.short_description_about_yourself, _binding.descriptionTxt.getText().toString(), FiledType.DESCRIPTION,
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
        _usersViewModel.get_localUser().observe(getViewLifecycleOwner(), u -> {
            _binding.setUser(u);
        });
        // Check is user is update my our changes
        _usersViewModel.get_isUserUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated)
                _binding.setUser(_usersViewModel.get_localUser().getValue());
        });

        _usersViewModel.get_serverInteractionResult().observe(getViewLifecycleOwner(), msg -> {
            if (!msg.equals(Consts.INVALID_STRING)) {
                if (msg.equals(Consts.SUCCESS)) {
                    Snackbar.make(_binding.getRoot(), R.string.mission_complete, Snackbar.LENGTH_SHORT).show();
                    _usersViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
                } else {
                    Snackbar.make(_binding.getRoot(), R.string.error_occurred, Snackbar.LENGTH_SHORT).show();
                }
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
     * Opens the image options dialog.
     */
    private void openImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String[] imageOptions;
        if (_usersViewModel.get_localUser().getValue().get_imagePath() != null) {
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