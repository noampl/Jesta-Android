package com.example.jesta.viewmodel;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.enteties.User;
import com.example.jesta.model.repositories.UsersRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UsersViewModel extends ViewModel {
    public String getUserPassword;

    // region Members

    private MutableLiveData<User> _myUser;

    // endregion

    // region C'tor

    public UsersViewModel(){
        _myUser = UsersRepository.getInstance().get_myUser();
    }

    // endregion

    // region Properties

    public MutableLiveData<User> get_myUser() {
        return _myUser;
    }

    public void set_myUser(User _myUser) {
        this._myUser.setValue(_myUser);
    }

    // endregion

    // region Private Methods

    /**
     * Gets datapicker result and convert it to display text
     * @param selection The date selected;
     * @return The date in dd/MM/yy
     */
    public String convertDateSelection(Long selection) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(new Date(selection));
    }

    public void deleteAccount() {
        // TODO implement
    }

    // endregion
}
