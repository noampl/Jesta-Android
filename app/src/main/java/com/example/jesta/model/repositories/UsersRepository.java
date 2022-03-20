package com.example.jesta.model.repositories;

import android.hardware.usb.UsbDevice;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.User;

public class UsersRepository {

    // region Members

    private MutableLiveData<User> _myUser;

    // endregion

    // region Singleton

    private static UsersRepository instance = null;

    private UsersRepository(){
        _myUser = new MutableLiveData<>();
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
        System.out.println("peleg = setuser " + user.get_firstName());
        _myUser.postValue(user);
    }

    // endregion
}
