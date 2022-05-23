package com.example.jesta.view.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentCommentsBinding;
import com.example.jesta.view.adapters.CommentsAdapter;
import com.example.jesta.viewmodel.CommentsViewModel;


public class CommentsFragment extends Fragment {

    // region members

    private FragmentCommentsBinding _binding;
    private CommentsViewModel _commentsViewModel;
    private String _userId;

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false);
        _commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        _userId = CommentsFragmentArgs.fromBundle(getArguments()).getUserId();
        _binding.swiper.setRefreshing(true);
        if (_userId == null || _userId.equals("1")){
            _userId = _commentsViewModel.getCurrentUserId();
        }

        init();
        return _binding.getRoot();
    }

    // endregion

    // region Private Methods

    private void init(){
        _commentsViewModel.fetchComments(_userId);
        initObservers();
        initSwiper();
    }

    private void initObservers(){
        CommentsAdapter adapter = new CommentsAdapter();
        _commentsViewModel.get_comments().observe(getViewLifecycleOwner(), comm ->{
            System.out.println("peleg - comm size is " + comm.size());
            adapter.submitList(comm);
            adapter.notifyDataSetChanged();
            _binding.swiper.setRefreshing(false);
        });
        _binding.list.setAdapter(adapter);
    }

    private void initSwiper(){
        _binding.swiper.setOnRefreshListener(()->{
            _commentsViewModel.fetchComments(_userId);
        });
    }

    // endregion

}