package com.example.jesta.workes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.apollographql.apollo3.api.Optional;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.MapRepository;

import java.util.ArrayList;
import java.util.List;

public class FavorsWorker extends Worker {

    public FavorsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("favors worker", "get remote favors");
        List<Double> center = new ArrayList<>();
        center.add(MapRepository.getInstance().getMyLocation().getValue().latitude);
        center.add(MapRepository.getInstance().getMyLocation().getValue().longitude);
        GraphqlRepository.getInstance().GetRemoteJestas(new Optional.Present<>(center), MapRepository.getInstance().getRadiusInKm().getValue());
        return Result.success();
    }
}
