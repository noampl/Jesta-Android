package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.interfaces.INavigationHelper;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.GraphqlRepository;
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
    private String _userId;
    private final MutableLiveData<Transaction> _detailsTransaction;
    private final MutableLiveData<Boolean> _isJestaDetailsLoading;
    private final MutableLiveData<String> _rejectServerMsg;
    private final MutableLiveData<String> _approveServerMsg;
    private final MutableLiveData<String> _suggestHelpServerMsg;
    private final MutableLiveData<String> _doneServerMsg;
    private final MutableLiveData<Integer> _numberOfApprovedJestionar;

    // endregion

    // region C'Tor

    public JestaDetailsViewModel() {
        this._jestaDetails = JestaRepository.getInstance().get_jestaDetails();
        this._myLocation = MapRepository.getInstance().getMyLocation();
        this._dialogConsumerHelper = UsersRepository.getInstance().get_dialogConsumerHelper();
        this._comment = JestaRepository.getInstance().get_comment();
        this._isSuggestHelp = JestaRepository.getInstance().get_isSuggestHelp();
        this._favorTransactionStatus = JestaRepository.getInstance().get_favorTransactionStatus();
        this._userId = ShardPreferencesHelper.readId();
        this._detailsTransaction = JestaRepository.getInstance().get_detailsTransaction();
        this._isJestaDetailsLoading = JestaRepository.getInstance().get_jestaDetailsLoading();
        this._approveServerMsg = JestaRepository.getInstance().get_approveServerMsg();
        this._rejectServerMsg = JestaRepository.getInstance().get_rejectServerMsg();
        this._suggestHelpServerMsg = JestaRepository.getInstance().get_suggestHelpServerMsg();
        this._doneServerMsg = JestaRepository.getInstance().get_doneServerMsg();
        this._numberOfApprovedJestionar = JestaRepository.getInstance().get_numberOfApprovedJestionar();
    }

    // endregion

    // region Properties

    public void set_numberOfApprovedJestionar(int num){
        _numberOfApprovedJestionar.setValue(num);
    }

    public MutableLiveData<Integer> get_numberOfApprovedJestionar() {
        return _numberOfApprovedJestionar;
    }

    public void set_doneServerMsg(String msg) {
        _doneServerMsg.setValue(msg);
    }

    public void set_rejectServerMsg(String msg) {
        _rejectServerMsg.setValue(msg);
    }

    public void set_approveServerMsg(String msg) {
        _approveServerMsg.setValue(msg);
    }

    public void set_suggestHelpServerMsg(String msg) {
        _suggestHelpServerMsg.setValue(msg);
    }

    public MutableLiveData<String> get_rejectServerMsg() {
        return _rejectServerMsg;
    }

    public MutableLiveData<String> get_approveServerMsg() {
        return _approveServerMsg;
    }

    public MutableLiveData<String> get_suggestHelpServerMsg() {
        return _suggestHelpServerMsg;
    }

    public MutableLiveData<String> get_doneServerMsg() {
        return _doneServerMsg;
    }

    public MutableLiveData<Boolean> get_isJestaDetailsLoading() {
        return _isJestaDetailsLoading;
    }

    public void set_isJestaDetailsLoading(boolean isLoading) {
        _isJestaDetailsLoading.setValue(isLoading);
    }

    public INavigationHelper get_navigationHelper() {
        return _navigationHelper;
    }

    public void set_navigationHelper(INavigationHelper _navigationHelper) {
        this._navigationHelper = _navigationHelper;
    }

    public void set_favorTransactionStatus(String status) {
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

    public void set_isSuggestHelp(boolean bool) {
        _isSuggestHelp.setValue(bool);
    }

    public String get_userId() {
        return _userId;
    }

    public MutableLiveData<Transaction> get_detailsTransaction() {
        return _detailsTransaction;
    }

    public void set_detailsTransaction(Transaction transaction) {
        _detailsTransaction.setValue(transaction);
    }

    // endregion

    // region Public Methods

    /**
     * Get The Jesta Details by an Id
     *
     * @param id The Id
     */
    public void getDetails(String id) {
        GraphqlRepository.getInstance().getJestaDetails(id);
    }

    public void suggestHelp(String favorId) {
        GraphqlRepository.getInstance().suggestHelp(favorId, new Optional.Present<>(_comment));
    }

    public void cancelTransaction(String favorId) {
        GraphqlRepository.getInstance().cancelFavorTransaction(favorId);
    }

    public void notifyExcuterFinish(String jestaId) {
        GraphqlRepository.getInstance().executorFinishFavor(jestaId);
    }

    public void ownerFinishFavor(String transactionId, float rating, String comment) {
        GraphqlRepository.getInstance().ownerFinishFavor(transactionId, rating, comment);
        _navigationHelper.navigate(null);
    }

    /**
     * Get Transaction By Id
     *
     * @param transactionId the transaction ID
     */
    public void getTransaction(String transactionId) {
        GraphqlRepository.getInstance().getTransactionById(transactionId);
    }

    public void close() {
        set_jestaDetails(null);
        set_favorTransactionStatus(null);
        set_detailsTransaction(null);
        set_isJestaDetailsLoading(true);
        set_doneServerMsg(Consts.INVALID_STRING);
        set_rejectServerMsg(Consts.INVALID_STRING);
        set_approveServerMsg(Consts.INVALID_STRING);
        set_suggestHelpServerMsg(Consts.INVALID_STRING);
    }

    // endregion

    // endregion
}
