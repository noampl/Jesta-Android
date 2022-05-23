package com.example.jesta.view.fragments.jestas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentGenericJestasListBinding;
import com.example.jesta.databinding.FragmentWaitingJestasBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Address;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.view.adapters.JestaAdapter;
import com.example.jesta.viewmodel.JestasListsViewModel;
import com.example.jesta.viewmodel.MapViewModel;
import com.example.jesta.viewmodel.WaitingJestasViewModel;

import java.util.ArrayList;
import java.util.List;

public class WaitingJestasFragment extends Fragment {

    // region Members

    private FragmentWaitingJestasBinding _binding;
    private JestasListsViewModel _jestasListsViewModel;
    private MapViewModel _mapViewModel;
    private final INavigationHelper navigationHelper = new INavigationHelper() {
        @Override
        public void navigate(String[] args) {
            WaitingJestasFragmentDirections.ActionNavWaitingJestasToJestaDetailsFragment action =
                    WaitingJestasFragmentDirections.actionNavWaitingJestasToJestaDetailsFragment(args[0]);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_waiting_jestas, container, false);
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
        _jestasListsViewModel.set_transactions(null);

    }

    // endregion

    // region Private Methods

    private void init() {
        _jestasListsViewModel.fetchWatingJestas();
        initObservers();
        initListeners();
    }

    private void initObservers() {
        JestaAdapter adapter = new JestaAdapter(getViewLifecycleOwner(), _mapViewModel);
        _jestasListsViewModel.get_transactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Transaction> transactions) {
                if (transactions == null)
                    return;
                List<Jesta> jestas = new ArrayList<>();
                List<String> transactionId = new ArrayList<>();
                transactions.forEach(t -> {
                    jestas.add(new Jesta(t.getFavorId().get_id(),
                            t.getFavorId().getStatus(), t.getFavorId().getOwnerId(),
                            new Address(t.getFavorId().getSourceAddress().getFullAddress(), t.getFavorId().getSourceAddress().getCoordinates()),
                            t.getFavorId().getNumOfPeople(), t.getFavorId().getDateToExecute(), t.getFavorId().getDateToFinishExecute(), t.getFavorId().getCategories()));
                    transactionId.add(t.get_id());
                    System.out.println("peleg - added catagory " + t.getFavorId().getCategories().size());
                });

                adapter.submitList(jestas);
                adapter.setTransactionId(transactionId);
                adapter.notifyDataSetChanged();
                _binding.genericList.swiper.setRefreshing(false);
            }
        });
        _binding.genericList.list.setAdapter(adapter);
    }

    private void initListeners() {
        // TODO Implement this
        _binding.genericList.swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _jestasListsViewModel.fetchWatingJestas();
            }
        });
    }

//    // endregion

}