package com.example.gkudva.android_gtwitter.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.model.Tweet;
import com.example.gkudva.android_gtwitter.model.TweetManager;
import com.example.gkudva.android_gtwitter.model.User;
import com.example.gkudva.android_gtwitter.presenter.TimelinePresenter;
import com.example.gkudva.android_gtwitter.util.EndlessRecyclerViewScrollListener;
import com.example.gkudva.android_gtwitter.util.InfoMessage;
import com.example.gkudva.android_gtwitter.util.SpacesItemDecoration;
import com.example.gkudva.android_gtwitter.util.TwitterClient;
import com.example.gkudva.android_gtwitter.view.TimelineMvpView;
import com.example.gkudva.android_gtwitter.view.adapter.TweetsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements TimelineMvpView,SwipeRefreshLayout.OnRefreshListener{

    private TimelinePresenter presenter;
    private TweetsAdapter mAdapter;
    private List<Tweet> mTweetList;
    private InfoMessage mInfoMessage;
    private LinearLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private static final String TAG = "Timeline Activity";

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.fabComposeTweet)
    FloatingActionButton fabComposeTweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new TimelinePresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mInfoMessage = new InfoMessage(this);

        setupRecyclerView(rvTweets);

        setUserInfo(presenter.getCurrentUser());
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void setUserInfo(String usrname) {
        Log.d(TAG, "User: " + usrname);
        getSupportActionBar().setTitle(usrname);
    }

    @Override
    public void onRefresh() {
        presenter.loadArticles(mQuery, mFilterOptions);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMessage(String message) {
        rvTweets.setVisibility(View.INVISIBLE);
        mInfoMessage.showMessage(message);
    }

    @Override
    public void showTimeline(List<Tweet> timelineList) {
        TweetsAdapter adapter = (TweetsAdapter) rvTweets.getAdapter();
        adapter.setTimelineList(timelineList);
        adapter.notifyDataSetChanged();
        rvTweets.requestFocus();
        rvTweets.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        TweetsAdapter adapter = new TweetsAdapter(this);
    /*
        adapter.setCallback(new NYTAdapter.CallbackListener() {
            @Override
            public void onItemClick(Doc article) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("Article", Parcels.wrap(article));
                startActivity(intent);
            }

        });
    */
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                presenter.loadArticles(mQuery, mFilterOptions);
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
    }
}
