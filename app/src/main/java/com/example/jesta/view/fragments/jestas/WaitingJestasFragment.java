package com.example.jesta.view.fragments.jestas;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.view.adapters.JestaAdapter;
import com.example.jesta.viewmodel.WaitingJestasViewModel;

import java.util.List;

public class WaitingJestasFragment extends Fragment {

    private FragmentGenericJestasListBinding _binding;
    private WaitingJestasViewModel _viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        _viewModel = new ViewModelProvider(this).get(WaitingJestasViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentGenericJestasListBinding.inflate(inflater, container, false);

        RecyclerView rvList = _binding.rvList;
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        JestaAdapter adapter = null; // new JestaAdapter(_viewModel.getUserWaitingJestas())
//        adapter.setOnItemClickListener(new JestaAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(Jesta jesta, View view) {
//                // Navigates to the details fragment of the jesta:
//                Navigation.findNavController(view).navigate(WaitingJestasFragmentDirections.actionNavWaitingJestasToJestaDetailsFragment(jesta));
//            }
//        });
//        rvList.setAdapter(adapter);

        _binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        // Listens to data changes (while the fragment is alive):
        _viewModel.getUserWaitingJestas().observe(getViewLifecycleOwner(), new Observer<List<Jesta>>() {
            @Override
            public void onChanged(List<Jesta> jestas) {
                adapter.notifyDataSetChanged();
                if (jestas.size() < 1) {
                    _binding.rvList.setVisibility(View.GONE);
                    _binding.llNotFound.setVisibility(View.VISIBLE);
                } else {
                    _binding.llNotFound.setVisibility(View.GONE);
                    _binding.rvList.setVisibility(View.VISIBLE);
                }
            }
        });

        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Loads the data:
        this.refreshList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    private void refreshList() {
        _binding.swipeRefreshLayout.setRefreshing(true);
//        User user = UsersModel.getInstance().getCurrentUser().getValue();
//        if (user == null) {
//            throw new IllegalStateException("User cannot be null in UserRemediesFragment");
//        }
//        JestasModel.getInstance().refreshGetAllByUser(user.getId(), new OnCompleteListener() {
//            @Override
//            public void onSuccess() {
//                if (_binding != null) {
//                    _binding.swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//
//            @Override
//            public void onFailure() {
//                if (_binding != null) {
//                    _binding.swipeRefreshLayout.setRefreshing(false);
//                    Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    //endregion

}