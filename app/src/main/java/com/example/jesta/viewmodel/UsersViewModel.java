package com.example.jesta.viewmodel;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.model.enteties.User;
import com.example.jesta.model.repositories.GrahpqlRepository;
import com.example.jesta.model.repositories.UsersRepository;
import com.example.jesta.type.UserUpdateInput;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UsersViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<User> _myUser;
    private final MutableLiveData<Boolean> _isUserUpdated;

    // endregion

    // region C'tor

    public UsersViewModel(){
        _myUser = UsersRepository.getInstance().get_myUser();
        _isUserUpdated = UsersRepository.getInstance().get_isUserChanged();
    }

    // endregion

    // region Properties

    public MutableLiveData<User> get_myUser() {
        return _myUser;
    }

    public void set_myUser(User _myUser) {
        this._myUser.setValue(_myUser);
    }

    public String getUserPassword(){
        return ShardPreferencesHelper.readPassword();
    }

    public MutableLiveData<Boolean> get_isUserUpdated() {
        return _isUserUpdated;
    }

    public void set_isUserUpdated(boolean _isUserUpdated) {
        this._isUserUpdated.setValue(_isUserUpdated);
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

    /**
     * Update The user
     */
    public void updateUser() {
        User myUser = _myUser.getValue();
        UserUpdateInput userUpdateInput = new UserUpdateInput(
                new Optional.Present<>(myUser.get_firstName()), new Optional.Present<>(myUser.get_lastName()),
                new Optional.Present<>(myUser.get_birthday()), new Optional.Present<>(myUser.get_email()),
                null, new Optional.Present<>(myUser.get_phone()),
                new Optional.Present<>(myUser.get_address().fullAddress),null,null);
        GrahpqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    public void updateRemotePassword(String password){
        UserUpdateInput userUpdateInput = new UserUpdateInput(null,null,null,
                null,new Optional.Present<>(password),null,null,null,null);
        GrahpqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    // endregion
}
