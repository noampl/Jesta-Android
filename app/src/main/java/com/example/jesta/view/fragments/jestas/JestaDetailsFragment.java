package com.example.jesta.view.fragments.jestas;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesta.R;
import com.example.jesta.common.AlertDialogRtlHelper;
import com.example.jesta.common.IntentUtils;
import com.example.jesta.common.enums.FavorTransactionStatus;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentJestaDetailsBinding;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.model.enteties.User;
import com.example.jesta.viewmodel.JestaDetailsViewModel;
import com.example.jesta.viewmodel.NotificationViewModel;
import com.google.android.material.snackbar.Snackbar;


public class JestaDetailsFragment extends Fragment {

    // region Members

    private FragmentJestaDetailsBinding _binding;
    private JestaDetailsViewModel _jestaDetailsViewModel;
    private NotificationViewModel _notificationViewModel;
    private String _jestaId;
    private String _transactionId;
    private final IDialogConsumerHelper messageConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _jestaDetailsViewModel.set_comment(val);
//            _jestaDetailsViewModel.suggestHelp(_jestaId); // TODO does needed here?
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jesta_details, container, false);
        initVm();

        _jestaId = JestaDetailsFragmentArgs.fromBundle(getArguments()).getJestaId();
        _jestaDetailsViewModel.getDetails(_jestaId);
        _transactionId = JestaDetailsFragmentArgs.fromBundle(getArguments()).getTransactionId();
        if (_transactionId != null && _transactionId.length() > 3) {
            _jestaDetailsViewModel.getTransaction(_transactionId);
        }
        init();

        return _binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _jestaDetailsViewModel.set_jestaDetails(null);
        _jestaDetailsViewModel.set_favorTransactionStatus(null);
        _jestaDetailsViewModel.set_detailsTransaction(null);
    }

    // endregion

    // region Private Methods

    private void init() {
        initObserver();
        initListeners();
        _binding.setUserId(_jestaDetailsViewModel.get_userId());
        _binding.setIsSuggestHelp(_jestaDetailsViewModel.get_isSuggestHelp());
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initVm() {
        _jestaDetailsViewModel = new ViewModelProvider(this).get(JestaDetailsViewModel.class);
        _notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }

    private void initObserver() {
        _jestaDetailsViewModel.get_jestaDetails().observe(getViewLifecycleOwner(), favor -> {
            _binding.setJestaDetails(favor);
            if (favor != null && favor.ownerId != null) {
                User user = new User(favor.ownerId._id, favor.ownerId.firstName, favor.ownerId.lastName);
                user.set_rating(favor.ownerId.rating);
                _binding.setOwner(user);
            }
        });
        _jestaDetailsViewModel.get_myLocation().observe(getViewLifecycleOwner(), latLng ->
                _binding.setMyLocation(latLng));

        _jestaDetailsViewModel.get_favorTransactionStatus().observe(getViewLifecycleOwner(), status -> {
            _binding.setTransactionStatus(status);
            System.out.println("peleg - set TransactionStatus " + status);
        });

        _jestaDetailsViewModel.get_detailsTransaction().observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                _binding.setTransaction(transaction);
                _binding.setTransactionStatus(transaction.getStatus().toString());
                System.out.println("peleg - set status " + transaction.getStatus().toString());
            }
        });
    }

    private void initListeners() {
        _binding.suggestHelp.setOnClickListener(v -> {
            if (_jestaDetailsViewModel.get_favorTransactionStatus().getValue() != null) {
                if (_jestaDetailsViewModel.get_favorTransactionStatus().getValue().equals(FavorTransactionStatus.CANCELED.toString())) {
                    _jestaDetailsViewModel.suggestHelp(_jestaId);
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage(R.string.disable_offer)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            _jestaDetailsViewModel.cancelTransaction(_jestaId);
                                            dialogInterface.dismiss();
                                        }

                                        ;
                                    }
                            ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                }
            } else {
                _jestaDetailsViewModel.suggestHelp(_jestaId);
            }
        });

        _binding.sendMsg.setOnClickListener(v -> {
            this.openSendMessageOptionsDialog();
        });

        _binding.doneBtn.setOnClickListener(v -> {
            _jestaDetailsViewModel.notifyExcuterFinish(_jestaId);
        });

        _binding.statusLayout.setOnClickListener(v -> {
            if (_binding.getTransaction() != null) {
                if (_jestaDetailsViewModel.get_userId().equals(_binding.getTransaction().getFavorOwnerId().get_id())) {
                    // TODO Navigation to user profile with handeld ID
                }
            }
        });

        _binding.approve.setOnClickListener(v -> {
            _notificationViewModel.approveSuggestion(_transactionId);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
        });

        _binding.reject.setOnClickListener(v -> {
            _notificationViewModel.cancelSuggetstion(_transactionId);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
        });
    }

    /**
     * Opens the "Send Message" options dialog.
     */
    private void openSendMessageOptionsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.send_msg);
        String[] imageOptions = new String[]{getString(R.string.through_sms), getString(R.string.through_whatsapp)};
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    // SMS:
                    case 0:
                        Intent smsIntent = IntentUtils.sms("0541234567"); // TODO: Grab phone from server
                        startActivity(smsIntent);
                        break;
                    // WhatsApp:
                    case 1:
                        Intent whatsappIntent = IntentUtils.whatsApp("0541234567"); // TODO: Grab phone from server
                        startActivity(whatsappIntent);
                        break;
                    default:
                        throw new IndexOutOfBoundsException("The option index is not implemented in the Send Message options dialog.");
                }
            }
        });
        AlertDialogRtlHelper.make(builder).show();
    }

    // endregion
}