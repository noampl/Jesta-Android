package com.example.jesta.model.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.model.enteties.Transaction;

import java.util.ArrayList;
import java.util.List;

public class CommentsRepository {

    // region Members

    private final MutableLiveData<List<Transaction>> _commenets;

    // endregion

    // region Singleton

    private static CommentsRepository instance = null;

    private CommentsRepository(){
        _commenets = new MutableLiveData<>(new ArrayList<>());
    }

    public static CommentsRepository getInstance(){
        if (instance == null)
            instance = new CommentsRepository();
        return instance;
    }

    public static void cleanInstance() {
        instance = new CommentsRepository();
    }

    // endregion

    // region Properties

    public MutableLiveData<List<Transaction>> get_commenets() {
        return _commenets;
    }

    public void set_commenets(List<Transaction> comments){
        _commenets.postValue(comments);
    }

    // endregion
}
