package com.example.gkudva.android_gtwitter.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gkudva.android_gtwitter.R;
import com.example.gkudva.android_gtwitter.TwitterApplication;
import com.example.gkudva.android_gtwitter.model.Tweet;
import com.example.gkudva.android_gtwitter.model.User;
import com.example.gkudva.android_gtwitter.util.AppConstants;
import com.example.gkudva.android_gtwitter.util.JSONDeserializer;
import com.example.gkudva.android_gtwitter.util.TwitterClient;
import com.example.gkudva.android_gtwitter.util.ErrorHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by gkudva on 29/09/17.
 */

public class ComposeDialogFragment extends DialogFragment {
    private static final String TAG = ComposeDialogFragment.class.getSimpleName();

    @BindView(R.id.btTweet)
    Button btTweet;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.tvCharsLeft)
    TextView tvCharsLeft;
    @BindView(R.id.btClose)
    Button btClose;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;

    private TwitterClient mClient;
    private User mUser;
    private int charLength;

    public interface ComposeDialogListener {
        void onUpdateStatusSuccess(Tweet statusTweet);
    }

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        mClient = TwitterApplication.getRestClient();
    }

    public static ComposeDialogFragment newInstance(User user) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(AppConstants.USER_EXTRA, Parcels.wrap(user));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        charLength = Integer.parseInt(getActivity().getResources().getString(R.string.tweetLimit));
        mUser = Parcels.unwrap(getArguments().getParcelable(AppConstants.USER_EXTRA));
        if (mUser != null) {
            initDialog();
        } else {
            ErrorHandler.logAppError("user is null");
        }

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                int remainingChar = charLength - s.length();
                tvCharsLeft.setText(Integer.toString(remainingChar));
                if (remainingChar < 0) {
                    tvCharsLeft.setTextColor(ContextCompat.getColor(getActivity(), R.color.error));
                    btTweet.setEnabled(false);
                } else {
                    tvCharsLeft.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_gray));
                    btTweet.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // DialogFragment is not taking up the whole screen
            // http://stackoverflow.com/a/26163346
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void initDialog() {
        tvName.setText(mUser.name);
        tvScreenName.setText(mUser.screenName);

        Glide.with(getActivity()).load(mUser.profileImageUrl)
                .fitCenter().centerCrop()
                .into(ivProfilePhoto);
    }

    @OnClick(R.id.btTweet)
    public void postTweet() {
        mClient.postStatus(etMessage.getText().toString(), -1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Compose tweet successful: " + response.toString());
                JSONDeserializer<Tweet> deserializer = new JSONDeserializer<>(Tweet.class);
                Tweet statusTweet = deserializer.configureJSONObject(response);
                if (statusTweet != null) {
                    sendSuccess(statusTweet);
                } else {
                    handleError("Error from deserializing JSON response");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handleError("onFailure1: " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handleError("onFailure2: " + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handleError("onFailure3");
            }
        });
    }

    @OnClick(R.id.btClose)
    public void closeDialog() {
        dismiss();
    }

    private void sendSuccess(Tweet statusTweet) {
        ComposeDialogListener listener = (ComposeDialogListener) getActivity();
        if (listener != null) {
            listener.onUpdateStatusSuccess(statusTweet);
        }
        dismiss();
    }

    private void handleError(String errorMessage) {
        ErrorHandler.logAppError(errorMessage);
        ErrorHandler.displayError(getActivity(), AppConstants.DEFAULT_ERROR_MESSAGE);
        dismiss();
    }
}
