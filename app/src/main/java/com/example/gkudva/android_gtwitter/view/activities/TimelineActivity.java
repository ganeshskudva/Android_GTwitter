package com.example.gkudva.android_gtwitter.view.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.model.Tweet;
import com.example.gkudva.android_gtwitter.model.TweetManager;
import com.example.gkudva.android_gtwitter.model.User;
import com.example.gkudva.android_gtwitter.presenter.TimelinePresenter;
import com.example.gkudva.android_gtwitter.util.TwitterClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    private TimelinePresenter presenter;
    private TwitterClient mClient;
    private TweetManager mTweetManager;
    private TweetsAdapter mAdapter;
    private List<Tweet> mTweetList;
    private User mCurrentUser;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.fabComposeTweet)
    FloatingActionButton fabComposeTweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new TimelinePresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

}
