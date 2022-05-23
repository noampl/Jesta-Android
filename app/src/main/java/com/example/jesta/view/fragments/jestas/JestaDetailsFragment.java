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

import com.example.jesta.GetJestaQuery;
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
import com.example.jesta.viewmodel.UsersViewModel;
import com.google.android.material.snackbar.Snackbar;


public class JestaDetailsFragment extends Fragment {

    // region Members

    private FragmentJestaDetailsBinding _binding;
    private JestaDetailsViewModel _jestaDetailsViewModel;
    private NotificationViewModel _notificationViewModel;
    private String _jestaId;
    private String _transactionId;

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jesta_details, container, false);
        initVm();

        _jestaId = JestaDetailsFragmentArgs.fromBundle(getArguments()).getJestaId();
        _jestaDetailsViewModel.getDetails(_jestaId);
        _transactionId = JestaDetailsFragmentArgs.fromBundle(getArguments()).getTransactionId();
        if (_transactionId != null && _transactionId.length() > 3) {
            _jestaDetailsViewModel.getTransaction(_transactionId);
            System.out.println("peleg - get transactionID");
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
            if (favor != null) {
                if (favor.ownerId != null) {
                    User user = new User(favor.ownerId._id, favor.ownerId.firstName, favor.ownerId.lastName);
                    user.set_rating(favor.ownerId.rating);
                    user.set_phone(favor.ownerId.phone);
                    _binding.setOwner(user);
                }
                if (favor.categoryId != null && favor.categoryId.size() > 0){
                    String title = "";

                    if (favor.categoryId.size() > 1){
                        title = favor.categoryId.get(1).name + " " + favor.categoryId.get(0).name;
                    }
                    else{
                        title = favor.categoryId.get(0).name;
                    }
                    _binding.setCategoryTitle(title);
                }
            }

        });
        _jestaDetailsViewModel.get_myLocation().observe(getViewLifecycleOwner(), latLng ->
                _binding.setMyLocation(latLng));

        _jestaDetailsViewModel.get_favorTransactionStatus().observe(getViewLifecycleOwner(), status -> {
            _binding.setTransactionStatus(status);
            System.out.println("peleg - set TransactionStatus " + status);
            validateButtons(status);
            _binding.executePendingBindings();
        });

        _jestaDetailsViewModel.get_detailsTransaction().observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                _binding.setTransaction(transaction);
                _jestaDetailsViewModel.set_favorTransactionStatus(transaction.getStatus().toString());
                System.out.println("peleg - set status " + transaction.getStatus().toString());
            }
        });
    }

    private void validateButtons(String status) {
        validateDoneBtn(status);
        validateStatusBtn(status);
    }

    private void validateStatusBtn(String status) {
        if (_jestaDetailsViewModel.get_jestaDetails().getValue() != null) {
            if (status != null) {
                if (status.equals(FavorTransactionStatus.CLOSED.toString())) {
                    _binding.suggestHelp.setVisibility(View.GONE);
                    _binding.sendMsg.setVisibility(View.GONE);
                } else if (_jestaDetailsViewModel.get_jestaDetails().getValue().ownerId._id.equals(_jestaDetailsViewModel.get_userId())) {
                    _binding.suggestHelp.setVisibility(View.GONE);
                    _binding.sendMsg.setVisibility(View.GONE);
                } else {
                    _binding.suggestHelp.setVisibility(View.VISIBLE);
                    _binding.sendMsg.setVisibility(View.VISIBLE);
                    _binding.sendMsg.setClickable(true);
                    _binding.suggestHelp.setClickable(true);
                }
            }
        }
    }

    private void validateDoneBtn(String status) {
        if (_jestaDetailsViewModel.get_jestaDetails().getValue() != null && status != null) {
            if (status.equals(FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.toString()) &&
                    !_jestaDetailsViewModel.get_jestaDetails().getValue().ownerId._id.equals(_jestaDetailsViewModel.get_userId())) {
                _binding.doneBtn.setVisibility(View.VISIBLE);
                _binding.doneBtn.setClickable(true);
            } else {
                _binding.doneBtn.setVisibility(View.INVISIBLE);
                _binding.doneBtn.setClickable(false);
            }
        }
    }

    private void initListeners() {
        _binding.suggestHelp.setOnClickListener(v -> {
            if (_jestaDetailsViewModel.get_favorTransactionStatus().getValue() != null) {
                if (_jestaDetailsViewModel.get_jestaDetails().getValue().ownerId._id.equals(_jestaDetailsViewModel.get_userId()))
                    return;
                if (_jestaDetailsViewModel.get_favorTransactionStatus().getValue().equals(FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString()) ||
                        _jestaDetailsViewModel.get_favorTransactionStatus().getValue().equals(FavorTransactionStatus.JESTA_DONE.toString()) ||
                        _jestaDetailsViewModel.get_favorTransactionStatus().getValue().equals(FavorTransactionStatus.CLOSED.toString())) {
                    return;
                }
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
                                    }
                            ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                }
            } else {
                if (_binding.getJestaDetails().ownerId._id.equals(_jestaDetailsViewModel.get_userId()))
                    return;
                _jestaDetailsViewModel.suggestHelp(_jestaId);
            }
        });

        _binding.sendMsg.setOnClickListener(v -> {
            if (_jestaDetailsViewModel.get_jestaDetails().getValue().ownerId._id.equals(_jestaDetailsViewModel.get_userId()))
                return;
            this.openSendMessageOptionsDialog();
        });

        _binding.doneBtn.setOnClickListener(v -> {
            _jestaDetailsViewModel.notifyExcuterFinish(_jestaId);
        });

        _binding.statusLayout.setOnClickListener(v -> {
            if (_binding.getTransaction() != null) {
                if (_jestaDetailsViewModel.get_userId().equals(_binding.getTransaction().getFavorOwnerId().get_id())) {
                    JestaDetailsFragmentDirections.ActionJestaDetailsFragmentToNavUserProfile action =
                            JestaDetailsFragmentDirections.actionJestaDetailsFragmentToNavUserProfile();
                    action.setUserId(_binding.getTransaction().getHandledByUserId().get_id());
                    Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
                }
            }
        });

        _binding.approve.setOnClickListener(v -> {
            if (_binding.getTransaction() != null &&
                    _binding.getTransaction().getStatus() == FavorTransactionStatus.PENDING_FOR_OWNER) {
                _notificationViewModel.approveSuggestion(_transactionId);
                Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
            } else {
                JestaDetailsFragmentDirections.ActionJestaDetailsFragmentToRatingDialogFragment action =
                        JestaDetailsFragmentDirections.actionJestaDetailsFragmentToRatingDialogFragment(_transactionId);
                Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
            }
        });

        _binding.reject.setOnClickListener(v -> {
            if (_binding.getTransaction() != null && _binding.getTransaction().getStatus() == FavorTransactionStatus.EXECUTOR_FINISH_JESTA) {
                Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setMessage(getString(R.string.are_u_sure_cancel_jesta))
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton(R.string.yes, ((dialog, which) -> {
                            _notificationViewModel.cancelSuggetstion(_transactionId);
                            Navigation.findNavController(requireActivity(), R.id.main_container).navigateUp();
                        }))
                        .show();
            }
        });

        _binding.userName.setOnClickListener(v -> {
            JestaDetailsFragmentDirections.ActionJestaDetailsFragmentToNavUserProfile action =
                    JestaDetailsFragmentDirections.actionJestaDetailsFragmentToNavUserProfile();
            action.setUserId(_binding.getOwner().get_id());
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
        });

        _binding.userIcon.setOnClickListener(v -> {
            JestaDetailsFragmentDirections.ActionJestaDetailsFragmentToNavUserProfile action =
                    JestaDetailsFragmentDirections.actionJestaDetailsFragmentToNavUserProfile();
            action.setUserId(_binding.getOwner().get_id());
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
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
                        Intent smsIntent = IntentUtils.sms(_binding.getOwner().get_phone());
                        startActivity(smsIntent);
                        break;
                    // WhatsApp:
                    case 1:
                        Intent whatsappIntent = IntentUtils.whatsApp(_binding.getOwner().get_phone());
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