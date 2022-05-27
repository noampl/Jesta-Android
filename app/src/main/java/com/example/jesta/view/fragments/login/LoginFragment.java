package com.example.jesta.view.fragments.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.databinding.FragmentLoginBinding;
import com.example.jesta.view.activities.MainActivity;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;


public class LoginFragment extends Fragment {

    // region Members

    private FragmentLoginBinding _binding;
    private LoginRegisterViewModel _loginRegisterViewModel;

    // endregion

    // region LifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        _loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
        _loginRegisterViewModel.setServerErrorMsg(Consts.INVALID_STRING);
        init();

        return _binding.getRoot();
    }

    // endregion

    // region private Methods

    /**
     * Initialize the fragment
     */
    private void init() {
        initListeners();
        initObserver();
    }

    /**
     * Register all observers
     */
    private void initObserver() {
        _loginRegisterViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), (isLoggedIn) -> {
            if (isLoggedIn) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        _loginRegisterViewModel.getServerErrorMsg().observe(getViewLifecycleOwner(), (msg) -> {
            if (!msg.equals(Consts.INVALID_STRING)) {
                // TODO ohad raise an error
                if (msg.contains("user")) {
                    _binding.email.setError(msg);
                    _binding.passwordLayout.setError(null);
                } else {
                    _binding.passwordLayout.setError(msg);
                    _binding.email.setError(null);
                }
            }
        });

    }

    /**
     * Register all listeners
     */
    private void initListeners() {
        _binding.signUp.setOnClickListener((v) ->
                Navigation.findNavController(requireActivity(), R.id.login_register_container).navigate(R.id.action_loginFragment_to_registerFragment));

        _binding.loginBtn.setOnClickListener(v -> {
            validLoginParams();
        });
    }

    private void validLoginParams() {
        String mail = _binding.emailEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesEmailValid(mail)) {
            _binding.email.setError(getString(R.string.email_validation_error));
            zeroErrorsMsgs(_binding.email);
            return;
        }
        String pass = _binding.passwordEditTxt.getText().toString();
        if (!_loginRegisterViewModel.doesPasswordValid(pass)) {
            _binding.passwordLayout.setError(getString(R.string.password_error));
            zeroErrorsMsgs(_binding.passwordLayout);
            return;
        }
        _loginRegisterViewModel.login(mail, pass);
    }

    public void zeroErrorsMsgs(TextInputLayout layout) {
        if (layout == _binding.passwordLayout) {
            _binding.email.setError(null);
        } else if (layout == _binding.email) {
            _binding.passwordLayout.setError(null);
        }
    }

    // endregion
}