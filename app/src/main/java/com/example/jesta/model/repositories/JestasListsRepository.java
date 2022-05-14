package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;

import java.util.List;

public class JestasListsRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _transactions;
    private final MutableLiveData<List<Jesta>> _jestas;

    // ednregion

    // region Singleton

    private static JestasListsRepository instance = null;

    private JestasListsRepository(){
        _transactions = new MutableLiveData<>();
        _jestas = new MutableLiveData<>();
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

    public MutableLiveData<List<Jesta>> get_jestas() {
       return _jestas;
    }

    public void setJestas(List<Jesta> jestas){
        _jestas.postValue(jestas);
    }
    // endregion
}
