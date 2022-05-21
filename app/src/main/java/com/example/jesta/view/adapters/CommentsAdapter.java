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
import com.example.jesta.databinding.CommentItemBinding;
import com.example.jesta.model.enteties.Transaction;

public class CommentsAdapter extends ListAdapter<Transaction, CommentsAdapter.CommentsViewHolder> {

    public CommentsAdapter() {
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
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.comment_item, parent, false);
        return new CommentsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        holder.ondBind(getItem(position));
    }

    protected static class CommentsViewHolder extends RecyclerView.ViewHolder {

        private CommentItemBinding _binding;

        public CommentsViewHolder(CommentItemBinding _binding){
            super(_binding.getRoot());
            this._binding = _binding;
        }

        public void ondBind(Transaction transaction){
            _binding.setTransaction(transaction);
            _binding.executePendingBindings();
        }
    }
}
