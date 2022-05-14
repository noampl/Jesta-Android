package com.example.jesta.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jesta.R;
import com.example.jesta.databinding.UserTransactionItemBinding;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.viewmodel.MapViewModel;

import java.util.Map;

public class UserTransactionAdapter extends ListAdapter<Transaction,UserTransactionAdapter.UserTransactionViewHolder> {

    // region Members

    private final MapViewModel _mapViewModel;

    // endregion

    protected UserTransactionAdapter(MapViewModel mapViewModel) {
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

        this._mapViewModel = mapViewModel;
    }

    @NonNull
    @Override
    public UserTransactionAdapter.UserTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserTransactionItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.user_transaction_item, parent, false);
        return new UserTransactionViewHolder(binding, _mapViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTransactionAdapter.UserTransactionViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    protected static class UserTransactionViewHolder extends RecyclerView.ViewHolder {

        private final UserTransactionItemBinding _binding;
        private final MapViewModel _mapViewModel;


        public UserTransactionViewHolder(UserTransactionItemBinding binding, MapViewModel mapViewModel) {
            super(binding.getRoot());
            _binding = binding;
            _mapViewModel = mapViewModel;
        }

        public void bind(Transaction transaction){
            _binding.setTransation(transaction);
            _binding.executePendingBindings();

            _binding.container.setOnClickListener(v->{
                _mapViewModel.markerClicked(transaction.getFavorId().get_id(), transaction.get_id());
            });
        }
    }
}
