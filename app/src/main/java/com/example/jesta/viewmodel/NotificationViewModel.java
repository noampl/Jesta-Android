package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.interfaces.IDeepLinkHelper;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.GrahpqlRepository;
import com.example.jesta.model.repositories.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<List<Transaction>> _notificationTransaction;
    private INavigationHelper _iNavigationHelper;
    private INavigationHelper _ratingDialogOpener;
    private IDeepLinkHelper _deepLingHelper;
    private final MutableLiveData<Boolean> _isTransactionLoading;

    // endregion

    // region C'tor

    public NotificationViewModel() {
        this._notificationTransaction = NotificationRepository.getInstance().get_notificationTransaction();
        this._isTransactionLoading = NotificationRepository.getInstance().get_isTransactionLoading();
    }

    // endregion

    // region Properties

    public IDeepLinkHelper get_deepLingHelper() {
        return _deepLingHelper;
    }

    public void set_deepLingHelper(IDeepLinkHelper _deepLingHelper) {
        this._deepLingHelper = _deepLingHelper;
    }

    public MutableLiveData<List<Transaction>> get_notificationTransaction() {
        return _notificationTransaction;
    }

    public MutableLiveData<Boolean> get_isTransactionLoading() {
        return _isTransactionLoading;
    }

    public void setIsTransactionLoading(boolean isTransactionLoading){
        _isTransactionLoading.setValue(isTransactionLoading);
    }

    public INavigationHelper get_iNavigationHelper() {
        return _iNavigationHelper;
    }

    public void set_ratingDialogOpener(INavigationHelper _ratingDialogOpener) {
        this._ratingDialogOpener = _ratingDialogOpener;
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

    public void suggestHelp(String favorId) {
        GrahpqlRepository.getInstance().suggestHelp(favorId, null);
    }

    public void openRating(String id) {
        if (_ratingDialogOpener != null){
            _ratingDialogOpener.navigate(id);
        }
    }

    public void openNavigationApp(List<String> lst) {
        _deepLingHelper.navigate(lst);
    }

    // endregion
}
