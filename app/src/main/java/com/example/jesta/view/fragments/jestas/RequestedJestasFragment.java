package com.example.jesta.view.fragments.jestas;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentGenericJestasListBinding;
import com.example.jesta.databinding.FragmentRequestedJestasBinding;
import com.example.jesta.databinding.FragmentWaitingJestasBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Address;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.view.adapters.JestaAdapter;
import com.example.jesta.view.adapters.MyRequestedJestaAdapter;
import com.example.jesta.viewmodel.JestasListsViewModel;
import com.example.jesta.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestedJestasFragment extends Fragment {

    // region Members

    private FragmentRequestedJestasBinding _binding;
    private JestasListsViewModel _jestasListsViewModel;
    private MapViewModel _mapViewModel;
    private final INavigationHelper navigationHelper = new INavigationHelper() {
        @Override
        public void navigate(String[] args) {
            RequestedJestasFragmentDirections.ActionNavRequestJestasToJestaDetailsFragment action =
                    RequestedJestasFragmentDirections.actionNavRequestJestasToJestaDetailsFragment(args[0]);
            if (args.length > 1 && args[1] != null)
                action.setTransactionId(args[1]);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_requested_jestas, container, false);
        _jestasListsViewModel = new ViewModelProvider(this).get(JestasListsViewModel.class);
        _mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        _mapViewModel.set_navigationHelper(navigationHelper);
        _binding.genericList.swiper.setRefreshing(true);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mapViewModel.set_navigationHelper(null);

    }

    // endregion

    // region Private Methods

    private void init() {
        _jestasListsViewModel.fetchMyJestas();
        initObservers();
        initListeners();
    }

    private void initObservers() {
        MyRequestedJestaAdapter adapter = new MyRequestedJestaAdapter(getViewLifecycleOwner(), _mapViewModel);
        _jestasListsViewModel.get_jestasMap().observe(getViewLifecycleOwner(), new Observer<Map<Jesta, List<Transaction>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(Map<Jesta, List<Transaction>> transactions) {
                if (transactions == null)
                    return;
                List<Pair<Jesta, List<Transaction>>> pairs = new ArrayList<>();
                transactions.forEach((k, v) -> pairs.add(new Pair<>(k, v)));
                adapter.submitList(pairs);
                adapter.notifyDataSetChanged();

                // Checks whether to show the list or an "empty" message:
                if (transactions.size() > 0) {
                    _binding.genericList.llNotFound.setVisibility(View.GONE);
                    _binding.genericList.list.setVisibility(View.VISIBLE);
                } else {
                    _binding.genericList.list.setVisibility(View.GONE);
                    _binding.genericList.llNotFound.setVisibility(View.VISIBLE);
                }
                _binding.genericList.swiper.setRefreshing(false);
            }
        });
        _binding.genericList.list.setAdapter(adapter);
    }

    private void initListeners() {
        _binding.genericList.swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _jestasListsViewModel.fetchMyJestas();
            }
        });
    }

//    // endregion

}