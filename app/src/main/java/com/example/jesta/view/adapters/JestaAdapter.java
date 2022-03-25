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

import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.JestaItemBinding;
import com.example.jesta.viewmodel.MapViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class JestaAdapter extends ListAdapter<GetJestasInRadiusQuery.GetFavorsInRadio, JestaAdapter.JestaViewHolder> {

    // region Members

    private final LifecycleOwner _lifecycleOwner;
    private final MapViewModel _mapViewModel;
    // endregion

    // region C'tor

    public JestaAdapter(LifecycleOwner _lifecycleOwner, MapViewModel mapViewModel) {
        super(new DiffUtil.ItemCallback<GetJestasInRadiusQuery.GetFavorsInRadio>() {
            @Override
            public boolean areItemsTheSame(@NonNull GetJestasInRadiusQuery.GetFavorsInRadio oldItem, @NonNull GetJestasInRadiusQuery.GetFavorsInRadio newItem) {
                return oldItem._id.equals(newItem._id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull GetJestasInRadiusQuery.GetFavorsInRadio oldItem, @NonNull GetJestasInRadiusQuery.GetFavorsInRadio newItem) {
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

        public void bind(GetJestasInRadiusQuery.GetFavorsInRadio jesta){
            _binding.setJesta(jesta);
            _binding.setMyLocation(_mapViewModel.getMyLocation().getValue());
            _binding.setSrcDate(jesta.dateToPublish.toString());
            _binding.setDestDate(jesta.dateToUnpublished.toString());
            _binding.setViewModel(_mapViewModel);
            _mapViewModel.getMyLocation().observe(_lifecycleOwner, new Observer<LatLng>() {
                @Override
                public void onChanged(LatLng latLng) {
                    _binding.setMyLocation(latLng);
                }
            });
            _binding.executePendingBindings();
        }
    }

    // endregion
}
