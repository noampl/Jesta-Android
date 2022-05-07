package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.Transaction;

import java.util.List;

public class JestasListsRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _transactions;

    // ednregion

    // region Singleton

    private static JestasListsRepository instance = null;

    private JestasListsRepository(){
        _transactions = new MutableLiveData<>();
    }

    public static JestasListsRepository getInstance(){
        if (instance == null){
            instance = new JestasListsRepository();
        }
        return instance;
    }

    // endregion

    // region Properties

    public MutableLiveData<List<Transaction>> get_transactions() {
        return _transactions;
    }
    public void setTransactions(List<Transaction> transactions){
        _transactions.postValue(transactions);
    }

    // endregion
}
