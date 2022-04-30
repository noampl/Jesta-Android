package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetUserByIdQuery;
import com.example.jesta.model.enteties.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NotificationRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _notificationTransaction;
    private final MutableLiveData<Boolean> _isTransactionLoading;

    // endregion

    // region Singleton

    private static NotificationRepository instance = null;

    private NotificationRepository(){
        _notificationTransaction = new MutableLiveData<>(new ArrayList<>());
        _isTransactionLoading = new MutableLiveData<>(false);
    }

    public static NotificationRepository getInstance(){
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    // endregion

    // region Public Method

    public MutableLiveData<List<Transaction>> get_notificationTransaction() {
        return _notificationTransaction;
    }

    public void set_notificationTransaction(List<Transaction> transactions){
        _notificationTransaction.postValue(transactions);
    }

    public synchronized void add_notificationTransaction(List<Transaction> transactions){
        List<Transaction> transactions1 = new ArrayList<>(Objects.requireNonNull(_notificationTransaction.getValue()));
        System.out.println("peleg - add transaction list size before " + transactions1.size());
        transactions1.addAll(transactions);
        System.out.println("peleg - add transaction list size after " + transactions1.size());
        set_notificationTransaction(transactions1);
    }

    public MutableLiveData<Boolean> get_isTransactionLoading() {
        return _isTransactionLoading;
    }

    public void set_isTransactionLoading(boolean isTransactionLoading){
        _isTransactionLoading.postValue(isTransactionLoading);
    }

    // endregion
}
