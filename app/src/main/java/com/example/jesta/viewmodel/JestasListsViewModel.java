package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.JestasListsRepository;
import com.example.jesta.type.FavorTransactionStatus;

import java.util.List;

public class JestasListsViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<List<Transaction>> _transactions;

    // endregion

    public JestasListsViewModel(){
        _transactions = JestasListsRepository.getInstance().get_transactions();
    }

    // region Properties

    public MutableLiveData<List<Transaction>> get_transactions() {
        return _transactions;
    }
    
    public void set_transactions(List<Transaction> transactions){
        _transactions.setValue(transactions);
    }

    // endregion

    // region Public Methods

    /**
     * Fetch all done jestas by the user
     */
    public void fetchDoneJestas() {
        GraphqlRepository.getInstance().getExecuterFavorTransaction(FavorTransactionStatus.JESTA_DONE);
    }

    /**
     * Fetch all pending for approve jestas by owner
     */
    public void fetchWatingJestas() {
        GraphqlRepository.getInstance().getExecuterFavorTransaction(FavorTransactionStatus.PENDING_FOR_OWNER);
    }

    /**w
     * Fetch all to do jestas by excecuter
     */
    public void fetchTodoJestas() {
        GraphqlRepository.getInstance().getExecuterFavorTransaction(FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME);
    }

    // endregion
}
