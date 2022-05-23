package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;

import java.util.List;
import java.util.Map;

public class JestasListsRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _transactions;
    private final MutableLiveData<Map<Jesta,List<Transaction>>> _jestasMap;

    // ednregion

    // region Singleton

    private static JestasListsRepository instance = null;

    private JestasListsRepository(){
        _transactions = new MutableLiveData<>();
        _jestasMap = new MutableLiveData<>();
    }

    public static JestasListsRepository getInstance(){
        if (instance == null){
            instance = new JestasListsRepository();
        }
        return instance;
    }

    public static void cleanInstance() {
        instance = new JestasListsRepository();
    }

    // endregion

    // region Properties

    public MutableLiveData<List<Transaction>> get_transactions() {
        return _transactions;
    }

    public void setTransactions(List<Transaction> transactions){
        _transactions.postValue(transactions);
    }

    public MutableLiveData<Map<Jesta, List<Transaction>>> get_jestasMap() {
        return _jestasMap;
    }

    public void set_jestasMap(Map<Jesta, List<Transaction>> jestaListMap){
        _jestasMap.postValue(jestaListMap);
    }

    // endregion
}
