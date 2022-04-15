package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetUserByIdQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationRepository {

    // region Members

    private final MutableLiveData<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>> _notificationTransaction;

    // endregion

    // region Singleton

    private static NotificationRepository instance = null;

    private NotificationRepository(){
        _notificationTransaction = new MutableLiveData<>(new ArrayList<>());
    }

    public static NotificationRepository getInstance(){
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    // endregion

    // region Public Method

    public MutableLiveData<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>> get_notificationTransaction() {
        return _notificationTransaction;
    }

    public void set_notificationTransaction(List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction> transactions){
        _notificationTransaction.postValue(transactions);
    }

    public void addNotification(GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction transaction) {
        System.out.println("peleg - add notifications");
        List<GetAllUserFavorsRequestedTransactionQuery.
                GetAllUserFavorsRequestedTransaction> transactions = new ArrayList<>(_notificationTransaction.getValue());

        transactions.add(transaction);
        _notificationTransaction.postValue(transactions);
    }

    // endregion
}
