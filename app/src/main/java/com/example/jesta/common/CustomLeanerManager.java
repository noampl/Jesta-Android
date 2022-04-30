package com.example.jesta.common;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLeanerManager extends LinearLayoutManager {

    public CustomLeanerManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        try {

            super.onLayoutChildren(recycler, state);

        } catch (IndexOutOfBoundsException e) {

            Log.e(TAG, "Inconsistency detected");
        }

    }
}
