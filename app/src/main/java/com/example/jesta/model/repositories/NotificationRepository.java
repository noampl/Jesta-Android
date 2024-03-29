package com.example.jesta.model.repositories;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetUserByIdQuery;
import com.example.jesta.common.Consts;
import com.example.jesta.model.enteties.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _notificationTransaction;
    private final MutableLiveData<Boolean> _isTransactionLoading;

    // endregion

    // region Singleton

    private static NotificationRepository instance = null;

    private NotificationRepository() {
        _notificationTransaction = new MutableLiveData<>(new ArrayList<>());
        _isTransactionLoading = new MutableLiveData<>(true);
    }

    public static NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    public static void cleanInstance() {
        instance = new NotificationRepository();
    }

    // endregion

    // region Public Method

    public MutableLiveData<List<Transaction>> get_notificationTransaction() {
        return _notificationTransaction;
    }

    public void set_notificationTransaction(List<Transaction> transactions) {
        // Sort the transaction by date
        _notificationTransaction.postValue(transactions.stream().sorted(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                long result = 0;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Consts.SERVER_DATE_FORMAT);
                try {
                    Date date1 = sdf.parse(o1.getDateLastModified());
                    Date date2 = sdf.parse(o2.getDateLastModified());
                    result = date2.getTime() - date1.getTime();
                } catch (ParseException | NullPointerException e) {
                    e.printStackTrace();
                }
                return (int) result;
            }
        }).collect(Collectors.toList()));
    }

    public void add_notificationTransaction(List<Transaction> transactions) {
        synchronized (_notificationTransaction) {
            List<Transaction> transactions1 = new ArrayList<>(Objects.requireNonNull(_notificationTransaction.getValue()));
            transactions.forEach(t-> {
                if (!transactions1.contains(t)){
                    transactions1.add(t);
                }
            });
            set_notificationTransaction(transactions1);
        }
    }

    public MutableLiveData<Boolean> get_isTransactionLoading() {
        return _isTransactionLoading;
    }

    public void set_isTransactionLoading(boolean isTransactionLoading) {
        _isTransactionLoading.postValue(isTransactionLoading);
    }

    public void removeTransactionById(String transactionId) {
        synchronized(_notificationTransaction){
            if (_notificationTransaction.getValue().size() > 0) {
                for (Transaction t : _notificationTransaction.getValue()) {
                    if (t.get_id().equals(transactionId)){
                        removeTransaction(t);
                        break;
                    }
                }
            }
        }
    }

    // endregion

    // region Private Methods

    private void removeTransaction(Transaction transaction){
        List<Transaction> transactions = new ArrayList<>(Objects.requireNonNull(_notificationTransaction.getValue()));
        transactions.remove(transaction);
        set_notificationTransaction(transactions);
    }

    // endregion
}
