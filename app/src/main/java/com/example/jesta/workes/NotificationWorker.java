package com.example.jesta.workes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.jesta.model.repositories.GraphqlRepository;

/**
 * Worker for getting the notification in the app
 */
public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        GraphqlRepository.getInstance().getAllFavorTransaction();
        return Result.success();
    }
}
