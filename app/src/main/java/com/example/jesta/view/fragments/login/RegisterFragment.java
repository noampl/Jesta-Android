package com.example.jesta.view.fragments.login;

import android.app.Activity;
import android.content.Intent;
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

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.databinding.FragmentRegisterBinding;
import com.example.jesta.view.activities.MainActivity;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;


public class RegisterFragment extends Fragment {

    // region members

    private FragmentRegisterBinding _binding;
    private LoginRegisterViewModel _loginRegisterViewModel;
    private Uri _filePath;

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
        init();

        return _binding.getRoot();
    }

    // endregion

    // region Private Methods

    /**
     * Initialize the app
     */
    private void init(){
        Picasso.with(_binding.confirmPasswordEditTxt.getContext()).load(R.drawable.no_profile_picture).fit().into(_binding.profileImageButton);
        initListeners();
        initObservers();
    }

    /**
     * Register all observers
     */
    private void initObservers(){
        _loginRegisterViewModel.getServerErrorMsg().observe(getViewLifecycleOwner(),msg->{
           if (!msg.equals(Consts.INVALID_STRING)){
               _binding.email.setError(msg);
               zeroErrorsMsgs(_binding.email);
           }
        });

        _loginRegisterViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(),isLoggedIn->{
            if (isLoggedIn){
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
        _binding.login.setOnClickListener((v)->
                Navigation.findNavController(requireActivity(),R.id.login_register_container).popBackStack());

        _binding.profileImageButton.setOnClickListener(v->{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            pickPhotoResultLauncher.launch(pickPhoto);
        });

        _binding.signupBtn.setOnClickListener((v)->{
            register();
        });
    }

    // endregion

    // region Private Methods

    /**
     * Register new user
     */
    private void register(){
        // valid password srength
        String pass1 = _binding.passwordEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPasswordValid(pass1)){
            _binding.passwordLayout.setError(getString(R.string.password_error));
            zeroErrorsMsgs(_binding.passwordLayout);
            return;
        }
        // valid passwords match
        String pass2 = _binding.confirmPasswordEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPasswordsMatch(pass1,pass2)){
            _binding.confirmPasswordLayout.setError(getString(R.string.password_not_the_same));
            zeroErrorsMsgs(_binding.confirmPasswordLayout);
            return;
        }
        // valid email address
        String email = _binding.emailEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesEmailValid(email)){
            _binding.email.setError(getString(R.string.email_validation_error));
            zeroErrorsMsgs(_binding.email);
            return;
        }

        String name = _binding.fullNameEditTxt.getText().toString();
        String[] names = name.split(" ");
        String lastName ="";
        for (int i= 1 ; i < names.length; i++) {
            lastName += lastName + " " + names[i];
        }
        _loginRegisterViewModel.register(names[0], lastName,null, _binding.emailEditTxt.getText().toString(),
                _binding.passwordEditTxt.getText().toString(),null,null,null,null, Consts.INVALID_INTEGER ,
                _filePath, Consts.INVALID_INTEGER, Consts.INVALID_INTEGER );
    }

    /**
     * Zero all the Input error except of the layout
     * @param layout The layout of the error
     */
    public void zeroErrorsMsgs(TextInputLayout layout){
        if (layout == _binding.passwordLayout){
            _binding.confirmPasswordLayout.setError(null);
            _binding.email.setError(null);
            _binding.fullName.setError(null);
        }
        else if(layout == _binding.confirmPasswordLayout){
            _binding.email.setError(null);
            _binding.fullName.setError(null);
            _binding.passwordLayout.setError(null);
        }
        else if (layout == _binding.email){
            _binding.confirmPasswordLayout.setError(null);
            _binding.fullName.setError(null);
            _binding.passwordLayout.setError(null);
        }
        else if(layout == _binding.fullName){
            _binding.confirmPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
            _binding.email.setError(null);
        }
    }

    // endregion

    // region helpers

    private final ActivityResultLauncher<Intent> pickPhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
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