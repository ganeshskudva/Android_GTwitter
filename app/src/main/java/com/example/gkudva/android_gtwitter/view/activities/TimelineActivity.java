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
import com.example.gkudva.android_gtwitter.presenter.TimelinePresenter;
import com.example.gkudva.android_gtwitter.util.DividerItemDecoration;
import com.example.gkudva.android_gtwitter.util.EndlessRecyclerViewScrollListener;
import com.example.gkudva.android_gtwitter.util.InfoMessage;
import com.example.gkudva.android_gtwitter.util.SpacesItemDecoration;
import com.example.gkudva.android_gtwitter.view.TimelineMvpView;
import com.example.gkudva.android_gtwitter.view.adapter.TweetsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gkudva.android_gtwitter.R.id.swipeContainer;

public class TimelineActivity extends AppCompatActivity implements TimelineMvpView,SwipeRefreshLayout.OnRefreshListener{

    private TimelinePresenter presenter;
    private TweetsAdapter mAdapter;
    private List<Tweet> mTweetList;
    private InfoMessage mInfoMessage;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private static final String TAG = "Timeline Activity";

    @BindView(swipeContainer)
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
        presenter.initTweetList();

    }

    private void setUserInfo(String usrname) {
        Log.d(TAG, "User: " + usrname);
        getSupportActionBar().setTitle(usrname);
    }

    @Override
    public void onRefresh() {
        presenter.populateTimeline(-1);
    //    swipeRefreshLayout.setRefreshing(false);
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
    public void showTimeline(int curSize, int listSize, List<Tweet> timelineList) {
        TweetsAdapter adapter = (TweetsAdapter) rvTweets.getAdapter();
        adapter.setTimelineList(timelineList);
/*
        if (curSize == 0)
        {
            adapter.notifyItemRangeRemoved(curSize, listSize);
        }
        else
        {
           adapter.notifyItemRangeInserted(curSize, listSize);
        }
*/
        adapter.notifyDataSetChanged();
    //    rvTweets.requestFocus();
    //    rvTweets.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleSwipeRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
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

        final int ydy = 0;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int offset = dy ;//- ydy;
                //ydy = dy;
                boolean shouldRefresh = (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                        && (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) && offset > 30;
                if (shouldRefresh) {
                    //swipeRefreshLayout.setRefreshing(true);
                    //Refresh to load data here.
                    return;
                }
                boolean shouldPullUpRefresh = layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getChildCount() - 1
                        && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && offset < -30;
                if (shouldPullUpRefresh) {
                    //swipeRefreshLayout.setRefreshing(true);
                    //refresh to load data here.
                    return;
                }
                swipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    private void customLoadMoreDataFromApi(int page) {
        // Returns results with an ID less than (that is, older than) or equal to the specified ID.
        long maxId = mTweetList.get(mTweetList.size() - 1).id - 1;
        presenter.populateTimeline(maxId);
    }
}
