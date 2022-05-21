package com.example.jesta.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.CommentsRepository;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.UsersRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    // region Members

    private final MutableLiveData<List<Transaction>> _comments;

    // endregion

    // region C'tor

    public CommentsViewModel(){
        _comments = CommentsRepository.getInstance().get_commenets();
    }

    // endregion

    // region Properties

    public MutableLiveData<List<Transaction>> get_comments() {
        return _comments;
    }

    public void fetchComments(String userId) {
        System.out.println("peleg - fetch userId " + userId);
        GraphqlRepository.getInstance().getCommentsOnUserId(userId);
    }

    public String getCurrentUserId() {
        return UsersRepository.getInstance().get_myUser().getValue().get_id();
    }

    // endregion

}
