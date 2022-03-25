package com.example.jesta.view.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jesta.R;
import com.example.jesta.databinding.JestaItemBinding;
import com.example.jesta.model.enteties.Jesta;

public class JestaAdapter extends ListAdapter<Jesta, JestaAdapter.JestaViewHolder> {

    // region C'tor

    public JestaAdapter() {
        super(new DiffUtil.ItemCallback<Jesta>() {
            @Override
            public boolean areItemsTheSame(@NonNull Jesta oldItem, @NonNull Jesta newItem) {
                return true;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Jesta oldItem, @NonNull Jesta newItem) {
                return false;
            }
        });
    }

    // endregion

    // region Recycler Adapter

    @NonNull
    @Override
    public JestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JestaItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.jesta_item, parent, false);
        return new JestaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JestaViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // endregion

    // region ViewHolder

    public static class JestaViewHolder extends RecyclerView.ViewHolder {

        private final JestaItemBinding _binding;

        public JestaViewHolder(@NonNull JestaItemBinding binding) {
            super(binding.getRoot());
            _binding = binding;
        }

        public void bind(Jesta jesta){
            _binding.setJesta(jesta);
            _binding.executePendingBindings();
        }
    }

    // endregion
}
