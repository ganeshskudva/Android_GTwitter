package com.example.gkudva.android_gtwitter.model;

import android.util.Log;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.List;

/**
 * Created by gkudva on 28/09/17.
 */

public class TweetManager {
    private static final String TAG = TweetManager.class.getSimpleName();
    private static TweetManager mInstance;

    public static TweetManager getInstance() {
        if (mInstance == null) {
            mInstance = new TweetManager();
        }

        return mInstance;
    }

    public void storeTweetList(List<Tweet> tweetList) {
        for (Tweet tweet : tweetList) {
            if (tweet.user != null) {
                tweet.user.save();
            }

            if (tweet.media != null) {
                tweet.media.save();
            }

            tweet.save();
            Log.d(TAG, "Storing tweet: " + tweet.toString());
        }
    }

    public List<Tweet> getStoredTweetList() {
        List<Tweet> tweetList = new Select()
                .from(Tweet.class)
                .orderBy("TweetId ASC").execute();

        return tweetList;
    }

    public void clearTweetList() {
        SQLiteUtils.execSql("DELETE FROM Tweet");
    }
}
