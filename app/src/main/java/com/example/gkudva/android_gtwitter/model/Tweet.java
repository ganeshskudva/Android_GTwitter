package com.example.gkudva.android_gtwitter.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.example.gkudva.android_gtwitter.util.DateUtil;
import com.example.gkudva.android_gtwitter.util.JSONDeserializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by gkudva on 28/09/17.
 */

@Table(name = "Tweet")
@Parcel(analyze = {Tweet.class})
public class Tweet extends Model implements JSONSerializable {
    private static final String TAG = Tweet.class.getSimpleName();

    @Column(name = "TweetId")
    public long id;

    @Column(name = "Text")
    public String text;

    @Column(name = "CreatedAt")
    public String createdAt;

    @Column(name = "DisplayTimeStamp")
    public String displayTimestamp;

    @Column(name = "Favorited")
    public boolean favorited;

    @Column(name = "FavoriteCount")
    public int favoriteCount;

    @Column(name = "Retweeted")
    public boolean retweeted;

    @Column(name = "RetweetCount")
    public int retweetCount;

    @Column(name = "ReplyTweet")
    public String replyTweet;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;

    @Column(name = "Media", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public Media media;

    // empty constructor needed by the Parceler library
    public Tweet() {
        super();
    }

    @Override
    public void configureFromJSON(JSONObject jsonObject) throws JSONException {
        Log.d(TAG, "tweet response: " + jsonObject.toString());

        id = jsonObject.getLong("id");
        text = jsonObject.getString("text");
        createdAt = jsonObject.getString("created_at");
        favorited = jsonObject.getBoolean("favorited");
        favoriteCount = jsonObject.getInt("favorite_count");
        retweeted = jsonObject.getBoolean("retweeted");
        retweetCount = jsonObject.getInt("retweet_count");
        setDisplayTimestamp();

        user = new User();
        user.configureFromJSON(jsonObject.getJSONObject("user"));

        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities != null) {
            try {
                JSONArray mediaArray = entities.getJSONArray("media");
                if (mediaArray != null) {
                    JSONDeserializer<Media> deserializer = new JSONDeserializer<>(Media.class);
                    List<Media> mediaList = deserializer.fromJSONArrayToList(mediaArray);
                    if (mediaList != null) {
                        // just grab the first item for now
                        media = mediaList.get(0);
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "No media specified");
                media = null;
            }
        }
    }

    private void setDisplayTimestamp() {
        String relativeTimestamp = DateUtil.getRelativeTimeAgo(createdAt);
        // hacky - remove 'ago' then only grab the first letter of the time
        String timestamp = relativeTimestamp.replace("ago", "");
        String[] time = timestamp.split(" ");
        if (time.length == 2) {
            displayTimestamp = time[0] + time[1].charAt(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("id=").append(id).append(";\n");
        str.append("text=").append(text).append(";\n");
        str.append("createdAt=").append(createdAt).append(";\n");

        if (user != null) {
            str.append("user=").append(user.toString()).append(";\n");
        }

        if (media != null) {
            str.append("media=").append(media.toString()).append(";\n");
        }

        return str.toString();
    }
}
