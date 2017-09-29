package com.example.gkudva.android_gtwitter.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.model.Media;
import com.example.gkudva.android_gtwitter.model.Tweet;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gkudva on 28/09/17.
 */

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private static final String TAG = TweetsAdapter.class.getSimpleName();
    public static final int TYPE_TWEET = 0;
    public static final int TYPE_TWEET_MEDIA = 1;

    private List<Tweet> mTweetList;
    private Context mContext;

    public TweetsAdapter(Context context, List<Tweet> tweetList) {
        this.mContext = context;
        this.mTweetList = tweetList;
    }

    public TweetsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mTweetList = Collections.emptyList();
    }

    public void setTimelineList(List<Tweet> timelineList)
    {
        this.mTweetList = timelineList;

    }

    @Override
    public int getItemViewType(int position) {
        Tweet tweet = mTweetList.get(position);
        if (tweet.media == null) {
            return TYPE_TWEET;
        } else {
            return TYPE_TWEET_MEDIA;
        }
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;

        if (viewType == TYPE_TWEET) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tweet, parent, false);
            viewHolder = new TweetViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_media_tweet, parent, false);
            viewHolder = new TweetMediaViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {

        Tweet tweet = mTweetList.get(position);
        Log.d(TAG, "tweet[" + position + "]:\n" + tweet.toString());

        int type = getItemViewType(position);
        if (type == TYPE_TWEET) {
            TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;
            tweetViewHolder.configureViewwithTweet(tweet);
        } else {
            TweetMediaViewHolder tweetMediaViewHolder = (TweetMediaViewHolder) holder;
            tweetMediaViewHolder.configureViewwithMediaTweet(tweet);
        }
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfilePhoto)
        ImageView ivProfilePhoto;
        @BindView(R.id.btFavorite)
        Button btFavorite;
        @BindView(R.id.btRetweet)
        Button btRetweet;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class TweetViewHolder extends ViewHolder {

        @BindView(R.id.ivProfilePhoto)
        ImageView ivProfilePhoto;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTimestamp)
        TextView tvTimeStamp;
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.btReply)
        Button btReply;
        @BindView(R.id.btRetweet)
        Button btRetweet;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;
        @BindView(R.id.btFavorite)
        Button btFavorite;
        @BindView(R.id.tvFavoriteCount)
        TextView tvFavoriteCount;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void configureViewwithTweet(Tweet tweet)
        {
            tvName.setText(tweet.getText());
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvTimeStamp.setText(tweet.getDisplayTimestamp());
            tvText.setText(tweet.getText());
            tvRetweetCount.setText(tweet.getRetweetCount());
            tvFavoriteCount.setText(tweet.getRetweetCount());

            if (TextUtils.isEmpty(tweet.getUser().getProfileImageUrl()))
            {
                ivProfilePhoto.setVisibility(View.GONE);
            }
            else
            {
                ivProfilePhoto.setVisibility(View.VISIBLE);
                if (tweet.isFavorited()) {
                    btFavorite.setBackgroundResource(R.drawable.favorite_on);
                } else {
                    btFavorite.setBackgroundResource(R.drawable.favorite);
                }

                if (tweet.isRetweeted()) {
                    btRetweet.setBackgroundResource( R.drawable.retweet_on);
                } else {
                    btRetweet.setBackgroundResource( R.drawable.retweet);
                }

                if (tweet.getUser() != null) {
                    Glide.with(mContext).load(tweet.getUser().getProfileImageUrl()) //
                            .fitCenter().centerCrop()
                            .into(ivProfilePhoto);
                }

            }
        }

    }

    public class TweetMediaViewHolder extends ViewHolder {
        @BindView(R.id.ivProfilePhoto)
        ImageView ivProfilePhoto;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTimestamp)
        TextView tvTimeStamp;
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.ivMedia)
        ImageView ivMedia;
        @BindView(R.id.btReply)
        Button btReply;
        @BindView(R.id.btRetweet)
        Button btRetweet;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;
        @BindView(R.id.btFavorite)
        Button btFavorite;
        @BindView(R.id.tvFavoriteCount)
        TextView tvFavoriteCount;

        public TweetMediaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void configureViewwithMediaTweet(Tweet tweet)
        {
            tvName.setText(tweet.getText());
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvTimeStamp.setText(tweet.getDisplayTimestamp());
            tvText.setText(tweet.getText());
            tvRetweetCount.setText(tweet.getRetweetCount());
            tvFavoriteCount.setText(tweet.getRetweetCount());

            if (TextUtils.isEmpty(tweet.getUser().getProfileImageUrl()))
            {
                ivProfilePhoto.setVisibility(View.GONE);
            }
            else
            {
                ivProfilePhoto.setVisibility(View.VISIBLE);
                if (tweet.isFavorited()) {
                    btFavorite.setBackgroundResource(R.drawable.favorite_on);
                } else {
                    btFavorite.setBackgroundResource(R.drawable.favorite);
                }

                if (tweet.isRetweeted()) {
                    btRetweet.setBackgroundResource( R.drawable.retweet_on);
                } else {
                    btRetweet.setBackgroundResource( R.drawable.retweet);
                }

                if (tweet.getUser() != null) {
                    Glide.with(mContext).load(tweet.getUser().getProfileImageUrl()) //
                            .fitCenter().centerCrop()
                            .into(ivProfilePhoto);
                }

                Media tweetMedia = tweet.getMedia();
                Glide.with(mContext).load(tweetMedia.getMediaUrl()) // .placeholder(R.drawable.loading_placeholder)
                        .centerCrop()
                        .into(ivMedia);
            }

        }
    }
}

