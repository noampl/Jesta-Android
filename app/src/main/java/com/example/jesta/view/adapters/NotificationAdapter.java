package com.example.jesta.view.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.R;
import com.example.jesta.common.enums.FavorTransactionStatus;
import com.example.jesta.databinding.NotificationItemBinding;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;


public class NotificationAdapter extends ListAdapter<Transaction, NotificationAdapter.NotificationViewHolder> {

    // region Members

    private final NotificationViewModel _notificationViewModel;

    // endregion

    public NotificationAdapter(NotificationViewModel notificationViewModel) {
        super(new DiffUtil.ItemCallback<Transaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.get_id().equals(newItem.get_id());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.equals(newItem);
            }
        });
        _notificationViewModel = notificationViewModel;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.notification_item, parent, false);
        return new NotificationViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position), _notificationViewModel);
    }

    protected static class NotificationViewHolder extends RecyclerView.ViewHolder {

        // region Members

        private final NotificationItemBinding _binding;

        // endregion

        public NotificationViewHolder(NotificationItemBinding binding) {
            super(binding.getRoot());
            _binding = binding;
        }

        public void bind(Transaction transaction, NotificationViewModel viewModel) {
            _binding.setTransaction(transaction);
            _binding.executePendingBindings();

            _binding.positiveBtn.setOnClickListener(v -> {
                if (transaction.getStatus() == null) {
                    viewModel.suggestHelp(transaction.get_id());
                    return;
                }
                if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(transaction.getStatus())) {
                    viewModel.approveSuggestion(transaction.get_id());
                } else if (FavorTransactionStatus.JESTA_DONE.equals(transaction.getStatus())) {
                    viewModel.openShowRateDialog(transaction);
                } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(transaction.getStatus())) {
                    viewModel.openRating(transaction.get_id());
                } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.equals(transaction.getStatus())) {
                    List<String> lst = new ArrayList<>();
                    lst.add(String.valueOf(transaction.getFavorId().getSourceAddress().getCoordinates().get(0)));
                    lst.add(String.valueOf(transaction.getFavorId().getSourceAddress().getCoordinates().get(1)));
                    viewModel.openNavigationApp(lst);
                } else {
                    Log.d("NotificationViewHolder", "unrecognized status " + transaction.getStatus());
                }
                viewModel.fetchTransaction();
            });

            _binding.detailsBtn.setOnClickListener(v -> viewModel.openDetails(transaction.getFavorId().get_id(), transaction.get_id()));
        }
    }
}
