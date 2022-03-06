package com.example.jesta.view.fragments;

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
    private void init(){
        initListeners();
        initObserver();
    }

    /**
     * Register all observers
     */
    private void initObserver() {
        _loginRegisterViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(),(isLoggedIn)->{
            if (isLoggedIn){
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        _loginRegisterViewModel.getServerErrorMsg().observe(getViewLifecycleOwner(), (msg)->{
            if (!msg.equals(Consts.INVALID_STRING)){
                if (msg.contains("user")){
                    _binding.email.setError(msg);
                    _binding.passwordLayout.setError(null);
                }
                else{
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
        _binding.signUp.setOnClickListener((v)->
                Navigation.findNavController(requireActivity(),R.id.login_register_container).navigate(R.id.action_loginFragment_to_registerFragment));

        _binding.loginBtn.setOnClickListener(v->{
            _loginRegisterViewModel.login(_binding.emailEditTxt.getText().toString(), _binding.passwordEditTxt.getText().toString());
        });
    }

    // endregion
}