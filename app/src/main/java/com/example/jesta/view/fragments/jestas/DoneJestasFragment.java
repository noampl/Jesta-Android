package com.example.jesta.view.fragments.jestas;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentDoneJestasBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Address;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.view.adapters.JestaAdapter;
import com.example.jesta.viewmodel.JestasListsViewModel;
import com.example.jesta.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class DoneJestasFragment extends Fragment {

    // region Members

    private FragmentDoneJestasBinding _binding;
    private JestasListsViewModel _jestasListsViewModel;
    private MapViewModel _mapViewModel;
    private final INavigationHelper navigationHelper = new INavigationHelper() {
        @Override
        public void navigate(String[] args) {
            DoneJestasFragmentDirections.ActionNavDoneJestasToJestaDetailsFragment action =
                    DoneJestasFragmentDirections.actionNavDoneJestasToJestaDetailsFragment(args[0]);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_done_jestas, container, false);
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
        _jestasListsViewModel.fetchDoneJestas();
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
                transactions.forEach(t -> jestas.add(new Jesta(t.getFavorId().get_id(),
                        t.getFavorId().getStatus(), t.getFavorId().getOwnerId(),
                        new Address(t.getFavorId().getSourceAddress().getFullAddress(), t.getFavorId().getSourceAddress().getCoordinates()),
                        t.getFavorId().getNumOfPeople(), t.getFavorId().getDateToExecute(), t.getFavorId().getDateToFinishExecute(), t.getFavorId().getCategories())));

                adapter.submitList(jestas);
//                adapter.notifyDataSetChanged();

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
                _jestasListsViewModel.fetchDoneJestas();
            }
        });
    }

    // endregion

}