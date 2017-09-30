package com.example.gkudva.android_gtwitter.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.TwitterApplication;
import com.example.gkudva.android_gtwitter.model.Tweet;
import com.example.gkudva.android_gtwitter.model.TweetManager;
import com.example.gkudva.android_gtwitter.model.User;
import com.example.gkudva.android_gtwitter.util.AppConstants;
import com.example.gkudva.android_gtwitter.util.DividerItemDecoration;
import com.example.gkudva.android_gtwitter.util.EndlessRecyclerViewScrollListener;
import com.example.gkudva.android_gtwitter.util.ItemClickSupport;
import com.example.gkudva.android_gtwitter.util.JSONDeserializer;
import com.example.gkudva.android_gtwitter.util.NetworkUtil;
import com.example.gkudva.android_gtwitter.util.TwitterClient;
import com.example.gkudva.android_gtwitter.util.ErrorHandler;
import com.example.gkudva.android_gtwitter.view.adapter.TweetsAdapter;
import com.example.gkudva.android_gtwitter.view.fragment.ComposeDialogFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {
    private static final String TAG = TimelineActivity.class.getSimpleName();

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.fabComposeTweet)
    FloatingActionButton fabComposeTweet;

    private TwitterClient mClient;
    private TweetManager mTweetManager;
    private TweetsAdapter mAdapter;
    private List<Tweet> mTweetList;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mClient = TwitterApplication.getRestClient();
        mTweetManager = TweetManager.getInstance();
        setCurrentUser();
        initSwipeRefreshLayout();
        initTweetList();
    }

    private void initSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(-1);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.primary,
                R.color.primary_dark,
                R.color.light_gray,
                R.color.extra_light_gray);

    }

    private void initTweetList() {
        mTweetList = new ArrayList<>();
        mAdapter = new TweetsAdapter(this, mTweetList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Tweet tweet = mTweetList.get(position);
                        Intent intent = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                        intent.putExtra(AppConstants.TWEET_EXTRA, Parcels.wrap(tweet));
                        startActivity(intent);
                    }
                });

        if (!NetworkUtil.isOnline()) {
            List<Tweet> offlineTweetList = mTweetManager.getStoredTweetList();
            if (offlineTweetList != null) {
                mTweetList.addAll(offlineTweetList);
                mAdapter.notifyItemRangeInserted(0, offlineTweetList.size());
            }
        } else {
            populateTimeline(-1);
        }
    }

    private void customLoadMoreDataFromApi(int page) {
        // Returns results with an ID less than (that is, older than) or equal to the specified ID.
        long maxId = mTweetList.get(mTweetList.size() - 1).id - 1;
        populateTimeline(maxId);
    }

    // Send an API request to get the timeline JSON
    // Fill the listview by creating the tweet objects from JSON
    private void populateTimeline(final long maxId) {
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
                            mTweetList.clear();
                            mAdapter.notifyItemRangeRemoved(0, listSize);

                            mTweetManager.clearTweetList();
                        }

                        int curSize = mTweetList.size();
                        mTweetList.addAll(tweetResponseList);
                        mAdapter.notifyItemRangeInserted(curSize, tweetResponseList.size());

                        mTweetManager.storeTweetList(mTweetList);
                    }
                } catch (JSONException e) {
                    ErrorHandler.handleAppException(e, "Exception from populating Twitter timeline");
                }

                handleSwipeRefresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handleSwipeRefresh();
                if (errorResponse != null) {
                    ErrorHandler.logAppError(errorResponse.toString());
                }

                ErrorHandler.displayError(TimelineActivity.this, AppConstants.DEFAULT_ERROR_MESSAGE);
            }
        });
    }

    private void handleSwipeRefresh() {
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
    }


    private void setCurrentUser() {
        mCurrentUser = User.getExistingUser();
        if (mCurrentUser != null) {
            Log.d(TAG, "Existing user from DB");
            setUserInfo();
        } else {
            getUser();
        }
    }

    private void setUserInfo() {
        Log.d(TAG, "User: " + mCurrentUser.toString());
        getSupportActionBar().setTitle(mCurrentUser.screenName);
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
                    ErrorHandler.logAppError("current user is NULL");
                } else {
                    User.saveUser(mCurrentUser);
                    setUserInfo();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ErrorHandler.logAppError("getUser onFailure1: " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ErrorHandler.logAppError("getUser onFailure2: " + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                ErrorHandler.logAppError("getUser onFailure3");
            }
        });
    }

    @OnClick(R.id.fabComposeTweet)
    public void composeTweet() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(mCurrentUser);
        composeDialogFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onUpdateStatusSuccess(Tweet status) {
        Log.d(TAG, "Compose tweet success: " + status.text);
        if (status != null) {
            // Add to the beginning of the list and scroll to the top
            mTweetList.add(0, status);
            mAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }
}
