package com.example.gkudva.android_gtwitter.util;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.example.gkudva.android_gtwitter.R;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * Created by gkudva on 28/09/17.
 */

public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "vj6uxhD1HkgiXder1W2BpWn41";       // Change this
    public static final String REST_CONSUMER_SECRET = "P8FRqaXMSnxKkCkDuI6mNKmGMbWpkJirbyDUh6kLuQxorxFmUk"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://twitterclient"; // Change this (here and in manifest)
    public static final int TWEET_FETCH_COUNT = 20;

    /*
    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
*/
    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
/*	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	} */

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    // HomeTimeline - Gets us the home timeline
    // GET statuses/home_timeline.json
    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        //Log.d(TAG, "------getHomeTimeline = max_id: " + maxId);

        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEET_FETCH_COUNT);
        if (maxId != -1) {
            params.put("max_id", maxId);
        } else {
            params.put("since_id", 1);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void postTweet(String body, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body);
        getClient().post(apiUrl, params, handler);
    }

    // GET account/verify_credentials.json
    // requesting user if authenticated
    public void getUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, handler);
    }
}
