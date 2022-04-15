package com.example.jesta.view.adapters;

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
import com.example.jesta.databinding.NotificationItemBinding;
import com.example.jesta.type.FavorTransactionStatus;
import com.example.jesta.viewmodel.NotificationViewModel;


public class NotificationAdapter extends ListAdapter<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction, NotificationAdapter.NotificationViewHolder> {

    // region Members

    private NotificationViewModel _notificationViewModel;

    // endregion

    public NotificationAdapter(NotificationViewModel notificationViewModel) {
        super(new DiffUtil.ItemCallback<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction oldItem, @NonNull GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction newItem) {
                return oldItem._id.equals(newItem._id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction oldItem, @NonNull GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction newItem) {
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

        public void bind(GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction transaction , NotificationViewModel viewModel){
            System.out.println("peleg - " + transaction.toString());
            _binding.setTransaction(transaction);
            _binding.executePendingBindings();

            _binding.positiveBtn.setOnClickListener(v->{
                if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(transaction.status)) {
                    viewModel.approveSuggestion(transaction._id);
                    _binding.positiveBtn.setText(v.getContext().getString(R.string.sent));
                } else if (FavorTransactionStatus.JESTA_DONE.equals(transaction.status)) {

                } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(transaction.status)) {

                } else{
                    Log.d("NotificationViewHolder","unrecognized status " + transaction.status);
                }
            });

            _binding.detailsBtn.setOnClickListener(v->viewModel.openDetails(transaction.favorId._id));
        }
    }
}
