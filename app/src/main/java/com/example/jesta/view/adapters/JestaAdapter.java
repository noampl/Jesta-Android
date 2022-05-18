package com.example.jesta.view.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.JestaItemBinding;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.viewmodel.MapViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class JestaAdapter extends ListAdapter<Jesta, JestaAdapter.JestaViewHolder> {

    // region Members

    private final LifecycleOwner _lifecycleOwner;
    private final MapViewModel _mapViewModel;
    private List<String> _transactionId;

    // endregion

    // region C'tor

    public JestaAdapter(LifecycleOwner _lifecycleOwner, MapViewModel mapViewModel) {
        super(new DiffUtil.ItemCallback<Jesta>() {
            @Override
            public boolean areItemsTheSame(@NonNull Jesta oldItem, @NonNull Jesta newItem) {
                return oldItem.get_id().equals(newItem.get_id());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Jesta oldItem, @NonNull Jesta newItem) {
                return oldItem.equals(newItem);
            }
        });
        this._lifecycleOwner = _lifecycleOwner;
        this._mapViewModel = mapViewModel;
    }

    // endregion

    // region Recycler Adapter

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
        System.out.println("peleg - is transactionid null " + (_transactionId == null));
        if (_transactionId != null && position < _transactionId.size()){
            holder.setTransactioId(_transactionId.get(position));
        }
        else{
            return;
        }
    }

    public void setTransactionId(List<String> transactionId) {
        _transactionId = transactionId;
    }

    // endregion

    // region ViewHolder

    public static class JestaViewHolder extends RecyclerView.ViewHolder {

        private final JestaItemBinding _binding;
        private final LifecycleOwner _lifecycleOwner;
        private final MapViewModel _mapViewModel;

        public JestaViewHolder(@NonNull JestaItemBinding binding, LifecycleOwner lifecycleOwner, MapViewModel mapViewModel) {
            super(binding.getRoot());
            _binding = binding;
            _lifecycleOwner = lifecycleOwner;
            _mapViewModel = mapViewModel;
        }

        public void bind(Jesta jesta){
            _binding.setJesta(jesta);
            _binding.setMyLocation(_mapViewModel.getMyLocation().getValue());
            if (jesta.getDateToExecute() != null)
            _binding.setSrcDate(jesta.getDateToExecute().toString());
            if (jesta.getDateToFinishExecute() != null)
            _binding.setDestDate(jesta.getDateToFinishExecute().toString());
            _binding.setViewModel(_mapViewModel);
            _mapViewModel.getMyLocation().observe(_lifecycleOwner, new Observer<LatLng>() {
                @Override
                public void onChanged(LatLng latLng) {
                    _binding.setMyLocation(latLng);
                }
            });
            _binding.executePendingBindings();
        }

        public void setTransactioId(String transactioId) {
            _binding.setTransactionId(transactioId);
            _binding.executePendingBindings();
        }
    }

    // endregion
}
