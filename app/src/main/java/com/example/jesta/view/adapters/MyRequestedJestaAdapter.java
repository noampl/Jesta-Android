package com.example.jesta.view.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jesta.R;
import com.example.jesta.databinding.JestaItemBinding;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.viewmodel.MapViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MyRequestedJestaAdapter extends ListAdapter<Pair<Jesta, List<Transaction>>, MyRequestedJestaAdapter.JestaViewHolder> {

    private final LifecycleOwner _lifecycleOwner;
    private final MapViewModel _mapViewModel;

    public MyRequestedJestaAdapter(LifecycleOwner _lifecycleOwner, MapViewModel mapViewModel) {
        super(new DiffUtil.ItemCallback<Pair<Jesta, List<Transaction>>>() {
            @Override
            public boolean areItemsTheSame(@NonNull Pair<Jesta, List<Transaction>> oldItem, @NonNull Pair<Jesta, List<Transaction>> newItem) {
                return oldItem.first.get_id().equals(newItem.first.get_id());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Pair<Jesta, List<Transaction>> oldItem, @NonNull Pair<Jesta, List<Transaction>> newItem) {
                return oldItem.first.equals(newItem.first);
            }
        });

        this._lifecycleOwner = _lifecycleOwner;
        this._mapViewModel = mapViewModel;
    }

    @NonNull
    @Override
    public JestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JestaItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.jesta_item, parent, false);
        return new JestaViewHolder(binding, _lifecycleOwner, _mapViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull JestaViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    protected static class JestaViewHolder extends RecyclerView.ViewHolder {

        private final JestaItemBinding _binding;
        private final MapViewModel _mapViewModel;
        private final LifecycleOwner _lifeCycleOwner;

        public JestaViewHolder(JestaItemBinding binding, LifecycleOwner lifecycleOwner, MapViewModel mapViewModel) {
            super(binding.getRoot());
            _binding = binding;
            _lifeCycleOwner = lifecycleOwner;
            _mapViewModel = mapViewModel;
        }

        public void bind(Pair<Jesta, List<Transaction>> pair) {
            _binding.setJesta(pair.first);
            _binding.setIsMyRequestedJestas(true);
            _binding.setViewModel(_mapViewModel);
            _binding.setMyLocation(_mapViewModel.getMyLocation().getValue());
            if (pair.first.getDateToExecute() != null)
                _binding.setSrcDate(pair.first.getDateToExecute().toString());
            if (pair.first.getDateToFinishExecute() != null)
                _binding.setDestDate(pair.first.getDateToFinishExecute().toString());
            _mapViewModel.getMyLocation().observe(_lifeCycleOwner, new Observer<LatLng>() {
                @Override
                public void onChanged(LatLng latLng) {
                    _binding.setMyLocation(latLng);
                }
            });
            _binding.executePendingBindings();

            _binding.container.setOnClickListener(v -> {
                if (pair.second == null || pair.second.size() < 1){
                    _mapViewModel.markerClicked(pair.first.get_id(), null);
                    return;
                }
                if (_binding.innerList.getVisibility() > 0){
                    _binding.innerList.setVisibility(View.VISIBLE);
                    _binding.sideIconName.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
                else{
                    _binding.innerList.setVisibility(View.GONE);
                    _binding.sideIconName.setBackgroundResource(R.drawable.ic_baseline_arrow_right_24);
                }
            });
            UserTransactionAdapter adapter = new UserTransactionAdapter(_mapViewModel);
            adapter.submitList(pair.second);
            _binding.innerList.setAdapter(adapter);
        }
    }
}
