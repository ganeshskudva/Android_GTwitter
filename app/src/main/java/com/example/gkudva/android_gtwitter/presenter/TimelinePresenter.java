package com.example.gkudva.android_gtwitter.presenter;

import android.util.Log;

import com.example.gkudva.android_gtwitter.TwitterApplication;
import com.example.gkudva.android_gtwitter.model.Tweet;
import com.example.gkudva.android_gtwitter.model.TweetManager;
import com.example.gkudva.android_gtwitter.model.User;
import com.example.gkudva.android_gtwitter.util.JSONDeserializer;
import com.example.gkudva.android_gtwitter.util.NetworkUtil;
import com.example.gkudva.android_gtwitter.util.TwitterClient;
import com.example.gkudva.android_gtwitter.view.TimelineMvpView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by gkudva on 28/09/17.
 */

public class TimelinePresenter implements Presenter<TimelineMvpView> {

    private TimelineMvpView mainMvpView;
    private List<Tweet> mTweetList;
    private TwitterClient mClient;
    private TweetManager mTweetManager;
    private User mCurrentUser;
    private String mUserInfo;
    private static final String TAG = "TimelinePresenter";

    @Override
    public void attachView(TimelineMvpView view) {
        this.mainMvpView = view;
        mTweetList = new ArrayList<Tweet>();
    }

    @Override
    public void detachView() {
        this.mainMvpView = null;
    }

    public String getCurrentUser()
    {
        mClient = TwitterApplication.getRestClient();
        mTweetManager = TweetManager.getInstance();

        mCurrentUser = User.getExistingUser();
        if (mCurrentUser != null) {
            Log.d(TAG, "Existing user from DB");
            mUserInfo = mCurrentUser.getName();
        } else {
            getUser();
        }

        return mUserInfo;
    }

    private void getUser() {
        Log.d(TAG, "Fetching user from the server");

        mClient.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "getUser onSuccess: " + response.toString());
                JSONDeserializer<User> deserializer = new JSONDeserializer<>(User.class);
                mCurrentUser = deserializer.configureJSONObject(response);
                if (mCurrentUser == null) {
                    Log.d(TAG, "current user is NULL");
                } else {
                    User.saveUser(mCurrentUser);
                    mUserInfo = mCurrentUser.getName();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getUser onFailure1: " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "getUser onFailure2: " + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "getUser onFailure3");
            }
        });
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from JSON
    public void populateTimeline(final long maxId) {
        mClient.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "populateTimeline onSuccess: " + response.toString());
                try {
                    JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
                    List<Tweet> tweetResponseList = deserializer.fromJSONArrayToList(response);
                    if (tweetResponseList != null) {
                        Log.d(TAG, "tweet size: " + tweetResponseList.size());
                        if (maxId == -1) {
                            int listSize = mTweetList.size();
                        //    mTweetList.clear();
                        //    mainMvpView.showTimeline(0, listSize, mTweetList);
                        //    mAdapter.notifyItemRangeRemoved(0, listSize);

                            mTweetManager.clearTweetList();
                        }

                        int curSize = mTweetList.size();
                        mTweetList.addAll(tweetResponseList);
                        mainMvpView.showTimeline(curSize, tweetResponseList.size(), mTweetList);
                        //mAdapter.notifyItemRangeInserted(curSize, tweetResponseList.size());

                        mTweetManager.storeTweetList(mTweetList);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Exception from populating Twitter timeline");
                }

                mainMvpView.handleSwipeRefresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mainMvpView.handleSwipeRefresh();
                if (errorResponse != null) {
                    Log.d(TAG, "Error: " + errorResponse.toString());
                }
                mainMvpView.showMessage("Oops, something went wrong. Please try again later.");
            }
        });
    }

    public void initTweetList()
    {
        if (!NetworkUtil.isOnline()) {
            List<Tweet> offlineTweetList = mTweetManager.getStoredTweetList();
            if (offlineTweetList != null) {
                mTweetList.addAll(offlineTweetList);
                mainMvpView.showTimeline(0, offlineTweetList.size(), mTweetList);
    //            mAdapter.notifyItemRangeInserted(0, offlineTweetList.size());
            }
        } else {
            populateTimeline(-1);
        }
    }

}
