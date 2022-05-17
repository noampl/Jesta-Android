package com.example.jesta.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jesta.R;
import com.example.jesta.databinding.DialogMyRaitingBinding;
import com.example.jesta.viewmodel.NotificationViewModel;

public class RatingResultDialog extends DialogFragment {

    // region Members

    private AlertDialog _dialog;
    private DialogMyRaitingBinding _binding;
    private String _rate, _comment, _transactionId;
    private NotificationViewModel _notificationViewModel;

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_my_raiting, container,false);
        _dialog.setView(_binding.getRoot());
        _notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        init();
        return _binding.getRoot();

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        _notificationViewModel.closeNotification(_transactionId);
    }

    // endregion

    // region Private Methods

    private void init(){
        initArgs();
        initBinding();
        initListeners();
    }

    private void initArgs(){
        _rate = RatingResultDialogArgs.fromBundle(getArguments()).getRate();
        _comment = RatingResultDialogArgs.fromBundle(getArguments()).getComment();
        _transactionId = RatingResultDialogArgs.fromBundle(getArguments()).getTransactionId();
    }

    private void initBinding(){
        _binding.setRate(_rate);
        _binding.setComment(_comment);
        _binding.executePendingBindings();
    }

    private void initListeners(){
        _binding.close.setOnClickListener(v->{
            // TODo change the state of the notification

            _dialog.dismiss();
        });
    }

    // endregion
}
