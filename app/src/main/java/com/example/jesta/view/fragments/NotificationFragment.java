package com.example.jesta.view.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.R;
import com.example.jesta.common.CustomLeanerManager;
import com.example.jesta.databinding.FragmentNotificationBinding;
import com.example.jesta.interfaces.IDeepLinkHelper;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.view.adapters.NotificationAdapter;
import com.example.jesta.viewmodel.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment implements INavigationHelper {

    // region Members

    private FragmentNotificationBinding _binding;
    private NotificationViewModel _notificationViewModel;
    private final INavigationHelper _ratingDialogOpener = new INavigationHelper() {
        @Override
        public void navigate(String[] args) {
            NotificationFragmentDirections.ActionNavNotificationToRatingDialogFragment action =
            NotificationFragmentDirections.actionNavNotificationToRatingDialogFragment(args[0]);
            Navigation.findNavController(requireActivity(),R.id.main_container).navigate((NavDirections) action);
        }
    };
    private final IDeepLinkHelper _deepLinkHelper = new IDeepLinkHelper() {
        @Override
        public void navigate(List<String> args) {
            try {
                String lat = args.get(0);
                String lng = args.get(1);
                // Launch Waze to look for Hawaii:
                String url = "https://www.waze.com/ul?ll=" + lat + "%2C"+lng+"&navigate=yes&zoom=17";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // If Waze is not installed, open it in Google Play:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                startActivity(intent);
            }
        }
    };

    private final INavigationHelper _showRatingDialog = new INavigationHelper() {
        @Override
        public void navigate(String[] args) {
            NotificationFragmentDirections.ActionNavNotificationToRatingResultDialog action =
                    NotificationFragmentDirections.actionNavNotificationToRatingResultDialog(args[0], args[1], args[2]);
            Navigation.findNavController(requireActivity(), R.id.main_container).navigate(action);


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
        _notificationViewModel.set_deepLingHelper(_deepLinkHelper);
        _notificationViewModel.set_showRateDialog(_showRatingDialog);

        init();
        return _binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        _notificationViewModel.fetchTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _notificationViewModel.set_iNavigationHelper(null);
        _notificationViewModel.set_deepLingHelper(null);
        _notificationViewModel.set_ratingDialogOpener(null);
        _notificationViewModel.set_showRateDialog(null);

    }

    // endregion

    // region Private Methods

    private void init(){
        initAdapter();
        initSwiper();
    }

    private void initAdapter(){
        NotificationAdapter adapter = new NotificationAdapter(_notificationViewModel);
        _notificationViewModel.get_notificationTransaction().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter.submitList(transactions); // TODO add here sort by lastDate
             }
        });
        _binding.notificationLst.setAdapter(adapter);
        _binding.notificationLst.setLayoutManager(new CustomLeanerManager(requireContext()));
    }

    private void initSwiper(){
        _binding.swiper.setOnRefreshListener(()-> _notificationViewModel.fetchTransaction());
        _notificationViewModel.get_isTransactionLoading().observe(getViewLifecycleOwner(),b -> _binding.swiper.setRefreshing(b));
    }

    @Override
    public void navigate(String[] args) {
        NotificationFragmentDirections.ActionNavNotificationToJestaDetailsFragment action =
                NotificationFragmentDirections.actionNavNotificationToJestaDetailsFragment(args[0]);
        action.setTransactionId(args[1]);
        Navigation.findNavController(requireActivity(),R.id.main_container).navigate((NavDirections) action);
    }

    // endregion
}