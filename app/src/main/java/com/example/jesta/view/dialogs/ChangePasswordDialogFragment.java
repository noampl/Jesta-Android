package com.example.jesta.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentChangePasswordDialogBinding;
import com.example.jesta.databinding.FragmentOneInputDialogBinding;
import com.example.jesta.model.enteties.User;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordDialogFragment extends DialogFragment {

    // region Members

    private FragmentChangePasswordDialogBinding _binding;
    private AlertDialog _dialog;
    private UsersViewModel _usersViewModel;
    private LoginRegisterViewModel _loginRegisterViewModel;

    // endregion

    // region LifeCycle

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        _dialog = builder.create();
        return _dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_change_password_dialog, container, false);
        _dialog.setView(_binding.getRoot());
        _usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        _loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);

        init();
        return _binding.getRoot();
    }


    private void init(){
        initListeners();
    }

    private void initListeners(){
        _binding.cancelButton.setOnClickListener(view -> _dialog.dismiss());
        _binding.submit.setOnClickListener(view -> {
            String newPassword;
            // check pld password;
            if (!_binding.oldPasswordEditTxt.getText().toString().equals(_usersViewModel.getUserPassword())){
                _binding.oldPasswordLayout.setError(getString(R.string.password_not_the_same));
                zeroError(_binding.oldPasswordLayout);
                return;
            }
            newPassword = _binding.passwordEditTxt.getText().toString();
            if (!_loginRegisterViewModel.doesPasswordValid(newPassword)){
                _binding.passwordLayout.setError(getString(R.string.password_error));
                zeroError(_binding.passwordLayout);
                return;
            }
            if (!_loginRegisterViewModel.doesPasswordsMatch(newPassword,_binding.confirmPasswordEditTxt.getText().toString())){
                _binding.confirmPasswordLayout.setError(getString(R.string.password_not_the_same));
                zeroError(_binding.confirmPasswordLayout);
                return;
            }
            _usersViewModel.updateRemotePassword(newPassword);
            _dialog.dismiss();
        });
    }


    private void zeroError(TextInputLayout layout){
        if (layout == _binding.passwordLayout){
            _binding.confirmPasswordLayout.setError(null);
            _binding.oldPasswordLayout.setError(null);
        }
        else if (layout == _binding.confirmPasswordLayout){
            _binding.oldPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
        }
        else if(layout == _binding.oldPasswordLayout){
            _binding.confirmPasswordLayout.setError(null);
            _binding.passwordLayout.setError(null);
        }
        else{
            Log.e("ChangePasswordDialog", "Unknown layout has arrrived " + layout);
        }
    }
    // endregion
}