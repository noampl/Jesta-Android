package com.example.jesta.view.fragments.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.databinding.FragmentRegisterBinding;
import com.example.jesta.view.activities.MainActivity;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okio.Okio;
import okio.Source;


public class RegisterFragment extends Fragment {

    // region members

    private FragmentRegisterBinding _binding;
    private LoginRegisterViewModel _loginRegisterViewModel;
    private Uri _filePath;
//    private ContentResolver contentResolver = getContext().getContentResolver();

    // endregion

    // region LifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        _loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
        _binding.setIsloading(false);
        init();

        return _binding.getRoot();
    }

    // endregion

    // region Private Methods

    /**
     * Initialize the app
     */
    private void init() {
        Picasso.with(_binding.confirmPasswordEditTxt.getContext()).load(R.drawable.no_profile_picture).fit().into(_binding.profileImageButton);
        initListeners();
        initObservers();
    }

    /**
     * Register all observers
     */
    private void initObservers() {
        _loginRegisterViewModel.getServerErrorMsg().observe(getViewLifecycleOwner(), msg -> {
            if (!msg.equals(Consts.INVALID_STRING)) {
                _binding.setIsloading(false);

                if (msg.contains(Consts.EXISTS)) {
                    Snackbar.make(_binding.getRoot(), R.string.email_is_already_taken, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(_binding.getRoot(), R.string.error_occurred, Snackbar.LENGTH_SHORT).show();
                }
                zeroErrorsMsgs(null);
            }
        });

        _loginRegisterViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (isLoggedIn) {
                _binding.setIsloading(false);
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    /**
     * Register all listeners
     */
    private void initListeners() {
        _binding.login.setOnClickListener((v) ->
                Navigation.findNavController(requireActivity(), R.id.login_register_container).popBackStack());

        _binding.profileImageButton.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            pickPhotoResultLauncher.launch(pickPhoto);
        });

        _binding.signupBtn.setOnClickListener((v) -> {
            register();
        });

        _loginRegisterViewModel.setServerErrorMsg(Consts.INVALID_STRING);
    }

    // endregion

    // region Private Methods

    /**
     * Register new user
     */
    private void register() {
        // valid password srength
        String pass1 = _binding.passwordEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPasswordValid(pass1)) {
            _binding.passwordLayout.setError(getString(R.string.password_error));
            zeroErrorsMsgs(_binding.passwordLayout);
            return;
        }
        // valid passwords match
        String pass2 = _binding.confirmPasswordEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPasswordsMatch(pass1, pass2)) {
            _binding.confirmPasswordLayout.setError(getString(R.string.password_not_the_same));
            zeroErrorsMsgs(_binding.confirmPasswordLayout);
            return;
        }
        // valid email address
        String email = _binding.emailEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesEmailValid(email)) {
            _binding.email.setError(getString(R.string.email_validation_error));
            zeroErrorsMsgs(_binding.email);
            return;
        }
        // valid phone
        String phone = _binding.phoneEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPhoneValid(phone)) {
            _binding.phone.setError(getString(R.string.phone_validation_error));
            zeroErrorsMsgs(_binding.phone);
            return;
        }

        String name = _binding.fullNameEditTxt.getText().toString();
        String[] names = name.split(" ");
        if (names.length < 2){
            _binding.fullName.setError(getString(R.string.name_validation_error));
            zeroErrorsMsgs(_binding.fullName);
            return;
        }
        String lastName = "";
        for (int i = 1; i < names.length; i++) {
            lastName += lastName + " " + names[i];
        }
        InputStream inputStream = null;
        Source source = null;
        if (_filePath != null) {
            try {
                inputStream = getContext().getContentResolver().openInputStream(_filePath);
                source = Okio.source(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        _loginRegisterViewModel.register(names[0], lastName, null, _binding.emailEditTxt.getText().toString(),
                _binding.passwordEditTxt.getText().toString(), phone, null, source,
                Consts.INVALID_INTEGER, Consts.INVALID_INTEGER, null);

        _loginRegisterViewModel.setServerErrorMsg(Consts.INVALID_STRING);
        _binding.setIsloading(true);
    }

    /**
     * Zero all the Input error except of the layout
     *
     * @param layout The layout of the error
     */
    public void zeroErrorsMsgs(TextInputLayout layout) {
        if (layout == _binding.passwordLayout) {
            _binding.confirmPasswordLayout.setError(null);
            _binding.email.setError(null);
            _binding.fullName.setError(null);
            _binding.phone.setError(null);
        } else if (layout == _binding.confirmPasswordLayout) {
            _binding.email.setError(null);
            _binding.fullName.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.phone.setError(null);
        } else if (layout == _binding.email) {
            _binding.confirmPasswordLayout.setError(null);
            _binding.fullName.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.phone.setError(null);
        } else if (layout == _binding.fullName) {
            _binding.confirmPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.email.setError(null);
            _binding.phone.setError(null);
        } else if (layout == _binding.phone) {
            _binding.confirmPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.email.setError(null);
            _binding.fullName.setError(null);
        } else{
            _binding.confirmPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.email.setError(null);
            _binding.fullName.setError(null);
            _binding.phone.setError(null);
        }
    }

    // endregion

    // region helpers

    private final ActivityResultLauncher<Intent> pickPhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data != null ? data.getData() : null;
                        Picasso.with(_binding.getRoot().getContext()).load(uri).fit().into(_binding.profileImageButton);
                        _filePath = uri;
                    }
                }
            }
    );

    // endregion
}