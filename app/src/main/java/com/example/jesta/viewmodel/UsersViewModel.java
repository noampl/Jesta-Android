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
import com.example.jesta.model.repositories.CommentsRepository;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.JestaRepository;
import com.example.jesta.model.repositories.JestasListsRepository;
import com.example.jesta.model.repositories.NotificationRepository;
import com.example.jesta.model.repositories.UsersRepository;
import com.example.jesta.type.UserUpdateInput;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okio.Okio;
import okio.Source;

public class UsersViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<User> _localUser;
    private final MutableLiveData<Boolean> _isUserUpdated;
    private IDialogConsumerHelper _dialogConsumerHelper;
    private final MutableLiveData<String> _serverInteractionResult;

    // endregion

    // region C'tor

    public UsersViewModel() {
        _localUser = UsersRepository.getInstance().get_localUser();
        _isUserUpdated = UsersRepository.getInstance().get_isUserChanged();
        _dialogConsumerHelper = UsersRepository.getInstance().get_dialogConsumerHelper();
        _serverInteractionResult = GraphqlRepository.getInstance().get_serverInteractionResult();
    }

    // endregion

    // region Properties

    public void set_serverInteractionResult(String result) {
        _serverInteractionResult.setValue(result);
    }

    public MutableLiveData<String> get_serverInteractionResult() {
        return _serverInteractionResult;
    }

    public MutableLiveData<User> get_localUser() {
        return _localUser;
    }

    public void set_localUser(User _localUser) {
        this._localUser.setValue(_localUser);
    }

    public String getUserPassword() {
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
        UsersRepository.getInstance().set_dialogConsumerHelper(_dialogConsumerHelper);
    }
    // endregion

    // region Private Methods

    /**
     * Gets datapicker result and convert it to display text
     *
     * @param selection The date selected;
     * @return The date in dd/MM/yy
     */
    public String convertDateSelection(Long selection) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(new Date(selection));
    }

    public void deleteAccount() {
        if (get_localUser().getValue() != null) {
            byte[] array = new byte[7]; // length is bounded by 7
            new Random().nextBytes(array);
            String generatedString = new String(array, StandardCharsets.UTF_8);
            updateRemotePassword(generatedString);
            logout();
        }
    }

    /**
     * Update The user
     */
    public void updateUser() {
        User myUser = _localUser.getValue();
        UserUpdateInput userUpdateInput = new UserUpdateInput(
                new Optional.Present<>(myUser.get_firstName()), new Optional.Present<>(myUser.get_lastName()),
                new Optional.Present<>(myUser.get_birthday()), new Optional.Present<>(myUser.get_email()),
                null, new Optional.Present<>(myUser.get_phone()),
                new Optional.Present<>(myUser.get_address() == null ? null : myUser.get_address().fullAddress), null, null,
                new Optional.Present<>(myUser.getDescription()));
        GraphqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    public void updateRemotePassword(String password) {
        UserUpdateInput userUpdateInput = new UserUpdateInput(null, null, null,
                null, new Optional.Present<>(password), null, null, null, null, null);
        GraphqlRepository.getInstance().UpdateUser(new Optional.Present<>(userUpdateInput));
    }

    public void logout() {
        ShardPreferencesHelper.logout();
        GraphqlRepository.getInstance().getIsLoggedIn().setValue(false);
        GraphqlRepository.cleanInstance();
        UsersRepository.cleanInstance();
        CommentsRepository.cleanInstance();
        JestaRepository.cleanInstance();
        JestasListsRepository.cleanInstance();
        NotificationRepository.cleanInstance();
        GraphqlRepository.getInstance().addUserToken(Consts.INVALID_STRING);
    }

    public void updateUserImage(Source source) {
        DefaultUpload upload = new DefaultUpload.Builder()
                .content(Okio.buffer(source))
                .fileName("_" + _localUser.getValue().get_id() + Consts.JPG)
                .build();
        new Optional.Present<Upload>(upload);
        GraphqlRepository.getInstance().uploadPhoto(new Optional.Present<Upload>(upload), _localUser.getValue().get_id());
    }

    public void updateUserImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        DefaultUpload upload = new DefaultUpload.Builder()
                .content(byteArray)
                .fileName("_" + _localUser.getValue().get_id() + Consts.JPG)
                .build();
        new Optional.Present<Upload>(upload);
        bitmap.recycle();
        GraphqlRepository.getInstance().uploadPhoto(new Optional.Present<Upload>(upload), _localUser.getValue().get_id());
    }

    // endregion
}
