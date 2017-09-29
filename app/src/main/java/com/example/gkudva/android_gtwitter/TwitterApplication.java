package com.example.gkudva.android_gtwitter;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.example.gkudva.android_gtwitter.util.TwitterClient;

/**
 * Created by gkudva on 29/09/17.
 */

public class TwitterApplication extends com.activeandroid.app.Application {
    private static final String TAG = TwitterApplication.class.getSimpleName();
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context = this;

        Log.d(TAG, "Initializing ActiveAndroid");
        ActiveAndroid.initialize(this);
    }

    public static TwitterClient getRestClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
    }
}
