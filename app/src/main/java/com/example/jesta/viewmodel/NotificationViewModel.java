package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.PeriodicWorkRequest;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetUserByIdQuery;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.repositories.GrahpqlRepository;
import com.example.jesta.model.repositories.NotificationRepository;

import java.util.HashMap;
import java.util.List;

public class NotificationViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>> _notificationTransaction;
    private INavigationHelper _iNavigationHelper;

    // endregion

    // region C'tor

    public NotificationViewModel() {
        this._notificationTransaction = NotificationRepository.getInstance().get_notificationTransaction();
    }

    // endregion

    // region Properties

    public MutableLiveData<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>> get_notificationTransaction() {
        return _notificationTransaction;
    }

    public INavigationHelper get_iNavigationHelper() {
        return _iNavigationHelper;
    }

    public void set_iNavigationHelper(INavigationHelper _iNavigationHelper) {
        this._iNavigationHelper = _iNavigationHelper;
    }

    // endregion

    // region Public Methods

    public void fetchTransaction() {
        GrahpqlRepository.getInstance().getFavorTransaction();
    }

    public void approveSuggestion(String id) {
        GrahpqlRepository.getInstance().approveFavorSuggestion(id, null);
    }

    public void openDetails(String id) {
        _iNavigationHelper.navigate(id);
    }

    // endregion
}
