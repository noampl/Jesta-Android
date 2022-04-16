package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.repositories.GrahpqlRepository;
import com.example.jesta.model.repositories.JestaRepository;
import com.example.jesta.model.repositories.MapRepository;
import com.example.jesta.model.repositories.UsersRepository;
import com.google.android.gms.maps.model.LatLng;

public class JestaDetailsViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<GetJestaQuery.GetFavor> _jestaDetails;
    private final MutableLiveData<LatLng> _myLocation;
    private final MutableLiveData<Boolean> _isSuggestHelp;
    private final MutableLiveData<String> _favorTransactionStatus;
    private IDialogConsumerHelper _dialogConsumerHelper;
    private String _comment;
    private INavigationHelper _navigationHelper;

    // endregion

    // region C'Tor

    public JestaDetailsViewModel() {
        this._jestaDetails = JestaRepository.getInstance().get_jestaDetails();
        this._myLocation = MapRepository.getInstance().getMyLocation();
        this._dialogConsumerHelper = UsersRepository.getInstance().get_dialogConsumerHelper();
        this._comment = JestaRepository.getInstance().get_comment();
        this._isSuggestHelp =JestaRepository.getInstance().get_isSuggestHelp();
        this._favorTransactionStatus = JestaRepository.getInstance().get_favorTransaction();
    }

    // endregion

    // region Properties

    public INavigationHelper get_navigationHelper() {
        return _navigationHelper;
    }

    public void set_navigationHelper(INavigationHelper _navigationHelper) {
        this._navigationHelper = _navigationHelper;
    }

    public void set_favorTransactionStatus(String status){
        _favorTransactionStatus.postValue(status);
    }

    public MutableLiveData<String> get_favorTransactionStatus() {
        return _favorTransactionStatus;
    }

    public MutableLiveData<GetJestaQuery.GetFavor> get_jestaDetails() {
        return _jestaDetails;
    }

    public void set_jestaDetails(GetJestaQuery.GetFavor _jestaDetails) {
        this._jestaDetails.postValue(_jestaDetails);
    }

    public MutableLiveData<LatLng> get_myLocation() {
        return _myLocation;
    }

    public IDialogConsumerHelper get_dialogConsumerHelper() {
        return _dialogConsumerHelper;
    }

    public void set_dialogConsumerHelper(IDialogConsumerHelper _dialogConsumerHelper) {
        this._dialogConsumerHelper = _dialogConsumerHelper;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }

    public MutableLiveData<Boolean> get_isSuggestHelp() {
        return _isSuggestHelp;
    }

    public void set_isSuggestHelp(boolean bool){
        _isSuggestHelp.setValue(bool);
    }

    // endregion

    // region Public Methods

    /**
     * Get The Jesta Details by an Id
     * @param id The Id
     */
    public void getDetails(String id){
        GrahpqlRepository.getInstance().getJestaDetails(id);
    }

    public void suggestHelp(String favorId) {
        GrahpqlRepository.getInstance().suggestHelp(favorId, new Optional.Present<>(_comment));
    }

    public void cancelTransaction(String favorId) {
        GrahpqlRepository.getInstance().cancelFavorTransaction(favorId);
    }

    public void notifyExcuterFinish(String jestaId) {
        GrahpqlRepository.getInstance().executorFinishFavor(jestaId);
    }

    public void ownerFinishFavor(String transactionId, float rating) {
        GrahpqlRepository.getInstance().ownerFinishFavor(transactionId, (int) rating);
        _navigationHelper.navigate(null);
    }



    // endregion

    // endregion
}
