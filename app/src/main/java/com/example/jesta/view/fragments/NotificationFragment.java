package com.example.jesta.view.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.FragmentNotificationBinding;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.view.adapters.NotificationAdapter;
import com.example.jesta.viewmodel.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment implements INavigationHelper {

    // region Members

    private FragmentNotificationBinding _binding;
    private NotificationViewModel _notificationViewModel;
    private final INavigationHelper _ratingDialogOpener = new INavigationHelper() {
        @Override
        public void navigate(String arg) {
            NotificationFragmentDirections.ActionNavNotificationToRatingDialogFragment action =
            NotificationFragmentDirections.actionNavNotificationToRatingDialogFragment(arg);
            Navigation.findNavController(requireActivity(),R.id.main_container).navigate(action);
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification,container,false);
        _notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        _notificationViewModel.set_iNavigationHelper(this);
        _notificationViewModel.set_ratingDialogOpener(_ratingDialogOpener);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _notificationViewModel.set_iNavigationHelper(null);
    }

    // endregion

    // region Private Methods

    private void init(){
        _notificationViewModel.fetchTransaction();
        initAdapter();
        initSwiper();
    }

    private void initAdapter(){
        NotificationAdapter adapter = new NotificationAdapter(_notificationViewModel);
        _notificationViewModel.get_notificationTransaction().observe(getViewLifecycleOwner(), new Observer<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>>() {
            @Override
            public void onChanged(List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction> transactions) {
                adapter.submitList(transactions); // TODO add here sort by lastDate
             }
        });
        _binding.notificationLst.setAdapter(adapter);
    }

    private void initSwiper(){
        _binding.swiper.setOnRefreshListener(()-> _notificationViewModel.fetchTransaction());
        _notificationViewModel.get_isTransactionLoading().observe(getViewLifecycleOwner(),b -> _binding.swiper.setRefreshing(b));
    }

    @Override
    public void navigate(String arg) {
        NotificationFragmentDirections.ActionNavNotificationToJestaDetailsFragment action =
                NotificationFragmentDirections.actionNavNotificationToJestaDetailsFragment(arg);
        Navigation.findNavController(requireActivity(),R.id.main_container).navigate(action);
    }

    // endregion
}