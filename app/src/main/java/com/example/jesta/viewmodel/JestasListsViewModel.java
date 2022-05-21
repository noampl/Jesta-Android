package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.JestasListsRepository;
import com.example.jesta.type.FavorTransactionStatus;

import java.util.List;
import java.util.Map;

public class JestasListsViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<List<Transaction>> _transactions;
    private final MutableLiveData<Map<Jesta,List<Transaction>>> _jestasMap;

    // endregion

    public JestasListsViewModel(){
        _transactions = JestasListsRepository.getInstance().get_transactions();
        _jestasMap = JestasListsRepository.getInstance().get_jestasMap();
    }

    // region Properties

    public MutableLiveData<List<Transaction>> get_transactions() {
        return _transactions;
    }
    
    public void set_transactions(List<Transaction> transactions){
        _transactions.setValue(transactions);
    }

    public MutableLiveData<Map<Jesta, List<Transaction>>> get_jestasMap() {
        return _jestasMap;
    }

    // endregion

    // region Public Methods

    /**
     * Fetch all done jestas by the user
     */
    public void fetchDoneJestas() {
        GraphqlRepository.getInstance().fetchDoneJesta();
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

    public void fetchMyJestas() {
        GraphqlRepository.getInstance().GetAllUserFavors();
    }

    // endregion
}
