package com.example.jesta.model.repositories;

import android.hardware.usb.UsbDevice;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.User;

public class UsersRepository {

    // region Members

    private final MutableLiveData<User> _myUser;
    private final MutableLiveData<Boolean> _isUserChanged;

    // endregion

    // region Singleton

    private static UsersRepository instance = null;

    private UsersRepository(){
        _myUser = new MutableLiveData<>();
        _isUserChanged = new MutableLiveData<>(false);
    }

    public static UsersRepository getInstance(){
        if (instance == null)
            instance = new UsersRepository();
        return instance;
    }

    // endregion

    // region Properties

    public MutableLiveData<User> get_myUser(){
        return _myUser;
    }

    public void set_myUser(User user){
        _myUser.postValue(user);
    }

    public MutableLiveData<Boolean> get_isUserChanged() {
        return _isUserChanged;
    }

    public void set_isUserChanged(Boolean isUserChanged) {
        this._isUserChanged.postValue(isUserChanged);
    }

    // endregion
}
