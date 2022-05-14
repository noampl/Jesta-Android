package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.GetJestaQuery;
import com.example.jesta.model.enteties.User;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.UsersRepository;

public class UserProfileViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<User> _userDetails;

    // endregion

    // region C'Tor

    public UserProfileViewModel() {
        this._userDetails = UsersRepository.getInstance().get_myUser();
    }

    // endregion

    // region Properties

    public MutableLiveData<User> get_userDetails() {
        return _userDetails;
    }

    public void set_userDetails(User user) {
        this._userDetails.postValue(user);
    }

    // endregion

    //region Public Methods

    /**
     * Gets the User by an Id
     *
     * @param id The Id
     */
    public void getUser(String id) {
        UsersRepository.getInstance().get_myUser(); // TODO: replace with an ID
    }

    //endregion

}
