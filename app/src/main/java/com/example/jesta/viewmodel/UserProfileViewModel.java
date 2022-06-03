package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.GetJestaQuery;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.model.enteties.User;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.UsersRepository;

public class UserProfileViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<User> _userDetails;
    private String _localUserId;

    // endregion

    // region C'Tor

    public UserProfileViewModel() {
        this._userDetails = UsersRepository.getInstance().get_myUser();
        this._localUserId = ShardPreferencesHelper.readId();
    }

    // endregion

    // region Properties

    public MutableLiveData<User> get_userDetails() {
        return _userDetails;
    }

    public void set_userDetails(User user) {
        this._userDetails.postValue(user);
    }

    public String get_localUserId() {
        return _localUserId;
    }

    // endregion

    //region Public Methods

    /**
     * Gets the User by an Id
     *
     * @param id The Id
     */
    public void getUser(String id) {
        GraphqlRepository.getInstance().getUserDetails(id);
    }

    //endregion

}
