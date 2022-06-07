package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.model.enteties.User;

public class UsersRepository {

    // region Members

    private final MutableLiveData<User> _detailsUser;
    private final MutableLiveData<User> _localUser;
    private final MutableLiveData<Boolean> _isUserChanged;
    private IDialogConsumerHelper _dialogConsumerHelper;

    // endregion

    // region Singleton

    private static UsersRepository instance = null;

    private UsersRepository(){
        _detailsUser = new MutableLiveData<>();
        _localUser = new MutableLiveData<>();
        _isUserChanged = new MutableLiveData<>(false);
        _dialogConsumerHelper = new IDialogConsumerHelper() {
            @Override
            public void consume(String val) {
                System.out.println("peleg - default consumer " + val);
            }
        };
    }

    public static UsersRepository getInstance(){
        if (instance == null)
            instance = new UsersRepository();
        return instance;
    }

    public static void cleanInstance() {
        instance = new UsersRepository();
    }

    // endregion

    // region Properties

    public void set_localUser(User user){
        _localUser.postValue(user);
    }

    public MutableLiveData<User> get_localUser() {
        return _localUser;
    }

    public MutableLiveData<User> get_detailsUser(){
        return _detailsUser;
    }

    public void set_detailsUser(User user){
        _detailsUser.postValue(user);
    }

    public MutableLiveData<Boolean> get_isUserChanged() {
        return _isUserChanged;
    }

    public void set_isUserChanged(Boolean isUserChanged) {
        this._isUserChanged.postValue(isUserChanged);
    }

    public IDialogConsumerHelper get_dialogConsumerHelper() {
        return _dialogConsumerHelper;
    }

    public void set_dialogConsumerHelper(IDialogConsumerHelper _dialogConsumerHelper) {
        this._dialogConsumerHelper = _dialogConsumerHelper;
    }

    // endregion
}
