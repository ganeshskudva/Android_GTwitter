package com.example.gkudva.android_gtwitter.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by gkudva on 28/09/17.
 */

@Table(name = "User")
@Parcel(analyze = {User.class})   // add Parceler annotation here
public class User extends Model implements JSONSerializable {
    @Column(name = "UserId")
    public long uid;

    @Column(name = "Name")
    public String name;

    @Column(name = "ScreenName")
    public String screenName;

    @Column(name = "ProfileImageUrl")
    public String profileImageUrl;

    public User() {
        super();
    }

    @Override
    public void configureFromJSON(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        uid = jsonObject.getLong("id");
        screenName = "@" + jsonObject.getString("screen_name");
        profileImageUrl = jsonObject.getString("profile_image_url");
    }

    // Save this user to the database
    public static void saveUser(User user) {
        user.save();
    }

    // Get the existing user from the database
    public static User getExistingUser() {
        List<User> userList = new Select()
                .from(User.class).execute();

        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("name=").append(name).append(";\n");
        str.append("screenName=").append(screenName).append(";\n");
        str.append("profileImageUrl=").append(profileImageUrl);

        return str.toString();
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getUid() {

        return uid;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
