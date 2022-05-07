package com.example.jesta.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentRatingDialogBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.viewmodel.JestaDetailsViewModel;
import com.example.jesta.viewmodel.NotificationViewModel;

public class RatingDialogFragment extends DialogFragment {

    // region Members

    private AlertDialog _dialog;
    private FragmentRatingDialogBinding _binding;
    private String _transactionId;
    private float _rating;
    private JestaDetailsViewModel _JestaDetailsViewModel;
    private final INavigationHelper navigationHelper = new INavigationHelper() {
        @Override
        public void navigate(String[] arg) {
            Navigation.findNavController(requireActivity(),R.id.main_container).navigateUp();
        }
    };

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rating_dialog, container,false);
        _dialog.setView(_binding.getRoot());
        _transactionId = RatingDialogFragmentArgs.fromBundle(getArguments()).getTransactionId();
        _rating = RatingDialogFragmentArgs.fromBundle(getArguments()).getRating();
        _JestaDetailsViewModel = new ViewModelProvider(this).get(JestaDetailsViewModel.class);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _JestaDetailsViewModel.set_navigationHelper(null);
    }

    // endregion

    // region Private Methods

    private void init(){
        _JestaDetailsViewModel.set_navigationHelper(navigationHelper);
        initListeners();
        _binding.ratingBar.setRating(_rating > 0 ? _rating : 0);
        _binding.submitBtn.setClickable(_rating <= 0);
    }

    private void initListeners(){
        _binding.submitBtn.setOnClickListener(v->{
            _JestaDetailsViewModel.ownerFinishFavor(_transactionId,_binding.ratingBar.getRating());
            _dialog.dismiss();
        });

        _binding.cancelButton.setOnClickListener(v->{
            _dialog.dismiss();
        });
    }

    // endregion
}