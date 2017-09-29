package com.example.gkudva.android_gtwitter.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.model.Media;
import com.example.gkudva.android_gtwitter.model.Tweet;

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
            tweetViewHolder.bindTo(tweet);
        } else {
            TweetMediaViewHolder tweetMediaViewHolder = (TweetMediaViewHolder) holder;
            tweetMediaViewHolder.bindTo(tweet);

            Media tweetMedia = tweet.media;
            Glide.with(mContext).load(tweetMedia.mediaUrl) // .placeholder(R.drawable.loading_placeholder)
                    .centerCrop()
                    .into(tweetMediaViewHolder.ivMedia);
        }

        if (TextUtils.isEmpty(tweet.user.profileImageUrl)) {
            holder.ivProfilePhoto.setVisibility(View.GONE);
        } else {
            holder.ivProfilePhoto.setVisibility(View.VISIBLE);
            if (tweet.favorited) {
                holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite_on));
            } else {
                holder.btFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.favorite));
            }

            if (tweet.retweeted) {
                holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet_on));
            } else {
                holder.btRetweet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.retweet));
            }

            if (tweet.user != null) {
                Glide.with(mContext).load(tweet.user.profileImageUrl) // .placeholder(R.drawable.loading_placeholder)
                        .fitCenter().centerCrop()
                        .into(holder.ivProfilePhoto);
            }
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

    public static class TweetViewHolder extends ViewHolder {
        private ItemTweetBinding mBinding;

        public TweetViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public void bindTo(Tweet tweet) {
            mBinding.setTweet(tweet);
            mBinding.executePendingBindings();
        }
    }

    public static class TweetMediaViewHolder extends ViewHolder {
        private ItemMediaTweetBinding mBinding;

        @BindView(R.id.ivMedia)
        ImageView ivMedia;

        public TweetMediaViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public void bindTo(Tweet tweet) {
            mBinding.setTweet(tweet);
            mBinding.executePendingBindings();
        }
    }
}

