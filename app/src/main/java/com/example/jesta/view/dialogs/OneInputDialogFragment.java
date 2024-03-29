package com.example.jesta.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentOneInputDialogBinding;
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.material.snackbar.Snackbar;

import okio._Base64Kt;


public class OneInputDialogFragment extends DialogFragment {

    // region Members

    private FragmentOneInputDialogBinding _binding;
    private AlertDialog _dialog;
    private FiledType _filedType;
    private String _title;
    private String _hint;
    private UsersViewModel _userViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_one_input_dialog, container, false);
        _userViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (_userViewModel != null)
            _userViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
    }

    // endregion

    // region Private Methods

    private void init() {
        _hint = OneInputDialogFragmentArgs.fromBundle(getArguments()).getHint();
        _filedType = FiledType.values()[OneInputDialogFragmentArgs.fromBundle(getArguments()).getFiledType()];
        _title = OneInputDialogFragmentArgs.fromBundle(getArguments()).getTitle();
        initDialog();
        initBinding();
        initObserver();
    }

    private void initDialog() {
        _binding.submit.setOnClickListener(view -> {
            String newVal = _binding.inputEditTxt.getText().toString();
            if (!newVal.equals("")) {
                _userViewModel.get_dialogConsumerHelper().consume(newVal);
            }
        });
        _binding.cancelButton.setOnClickListener(view -> _dialog.dismiss());
        _dialog.setView(_binding.getRoot());

    }

    private void initBinding() {
        _binding.setHint(_hint);
        _binding.setTitle(_title);
        int type = 0;

        switch (_filedType) {
            case PHONE:
                type = InputType.TYPE_NUMBER_VARIATION_NORMAL;
                break;
            case EMAIL:
                type = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case NAME:
            default:
                type = InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
                break;
        }
        _binding.inputEditTxt.setInputType(type);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }


    private void initObserver() {
        _userViewModel.get_serverInteractionResult().observe(getViewLifecycleOwner(), msg -> {
            if (msg.equals(Consts.INVALID_STRING))
                return;
            if (msg.equals(Consts.SUCCESS)) {
                _dialog.dismiss();
                _userViewModel.set_serverInteractionResult(Consts.INVALID_STRING);
            } else {
                Snackbar.make(_binding.getRoot(), R.string.error_occurred, Snackbar.LENGTH_SHORT).show();
            }

        });
    }
    // endregion
}