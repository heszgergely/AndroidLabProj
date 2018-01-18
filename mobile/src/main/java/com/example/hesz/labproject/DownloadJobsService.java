package com.example.hesz.labproject;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by gergelyhesz on 18/01/2018.
 */

public class DownloadJobsService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadJobsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String targetURL = intent.getStringExtra("targetURL");
        // Problem: targetURL = null
        //read sensors, send POST-Request via okhttp <- working


    }


}
