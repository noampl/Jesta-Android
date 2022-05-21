package com.example.jesta.viewmodel;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.DefaultUpload;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.api.Upload;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.model.enteties.User;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.UsersRepository;
import com.example.jesta.type.UserUpdateInput;

import java.text.SimpleDateFormat;
import java.util.Date;

import okio.Okio;
import okio.Source;

public class UsersViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<User> _myUser;
    private final MutableLiveData<Boolean> _isUserUpdated;
    private IDialogConsumerHelper _dialogConsumerHelper;

    // endregion

    // region C'tor

    public UsersViewModel(){
        _myUser = UsersRepository.getInstance().get_myUser();
        _isUserUpdated = UsersRepository.getInstance().get_isUserChanged();
        _dialogConsumerHelper = UsersRepository.getInstance().get_dialogConsumerHelper();
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

    public IDialogConsumerHelper get_dialogConsumerHelper() {
        return _dialogConsumerHelper;
    }

    public void set_dialogConsumerHelper(IDialogConsumerHelper _dialogConsumerHelper) {
        this._dialogConsumerHelper = _dialogConsumerHelper;
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
                new Optional.Present<>(myUser.get_address().fullAddress),null,null,
                new Optional.Present<>(myUser.getDescription()));
        GraphqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    public void updateRemotePassword(String password){
        UserUpdateInput userUpdateInput = new UserUpdateInput(null,null,null,
                null,new Optional.Present<>(password),null,null,null,null, null);
        GraphqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    public void logout() {
        ShardPreferencesHelper.logout();
        GraphqlRepository.getInstance().getIsLoggedIn().setValue(false);
        // TODO clear firebase token
    }

    public void updateUserImage(Source source){
            DefaultUpload upload = new DefaultUpload.Builder()
                    .content(Okio.buffer(source))
                    .fileName("_" + _myUser.getValue().get_id() + Consts.JPG)
                    .build();
            new Optional.Present<Upload>(upload);
            GraphqlRepository.getInstance().uploadPhoto(new Optional.Present<Upload>(upload), _myUser.getValue().get_id());
    }

    // endregion
}
