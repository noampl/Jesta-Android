package com.example.jesta.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.User;

import java.util.List;

public class WaitingJestasViewModel extends ViewModel {

    private final LiveData<List<Jesta>> _userWaitingJestas;

    public WaitingJestasViewModel() {
        //User user = UsersModel.getInstance().getCurrentUser().getValue();
        _userWaitingJestas = new MutableLiveData<>(); // JestasModel.getInstance().getAllByUser(user.getId())
    }

    public LiveData<List<Jesta>> getUserWaitingJestas() {
        return _userWaitingJestas;
    }

}
