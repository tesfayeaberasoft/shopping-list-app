package com.shoppinglist.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.repository.SyncManager;

public class BackgroundSyncService extends Worker {
    public BackgroundSyncService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SyncManager syncManager = new SyncManager(getApplicationContext());
        if (syncManager.isNetworkAvailable()) {
            SessionManager session = new SessionManager(getApplicationContext());
            if (session.isLoggedIn()) {
                syncManager.syncUp(session.getUser().getId());
            }
            return Result.success();
        }
        return Result.retry();
    }
}