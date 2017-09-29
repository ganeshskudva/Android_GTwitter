package com.example.gkudva.android_gtwitter.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by gkudva on 28/09/17.
 */

// https://dev.twitter.com/overview/api/entities-in-twitter-objects#media
@Table(name = "Media")
@Parcel(analyze = {Media.class})
public class Media extends Model implements JSONSerializable {
    @Column(name = "MediaId")
    public long id;

    @Column(name = "MediaUrl")
    public String mediaUrl;

    @Column(name = "Url")
    public String url;

    @Column(name = "Type")
    public String type;

    @Column(name = "Width")
    public int width;

    @Column(name = "Height")
    public int height;

    @Column(name = "ResizeStr")
    public String resizeStr;

    public Media() {
        super();
    }

    @Override
    public void configureFromJSON(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getLong("id");
        mediaUrl = jsonObject.getString("media_url");
        url = jsonObject.getString("url");

        JSONObject sizesObj = jsonObject.getJSONObject("sizes");
        if (sizesObj != null) {
            JSONObject smallObj = sizesObj.getJSONObject("large");
            if (smallObj != null) {
                width = smallObj.getInt("w");
                height = smallObj.getInt("h");
                resizeStr = smallObj.getString("resize");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("id=").append(id).append("\n");
        str.append("url=").append(url).append("\n");
        str.append("mediaUrl=").append(mediaUrl).append("\n");
        str.append("width=").append(width).append("\n");
        str.append("height=").append(height).append("\n");
        str.append("resizeStr=").append(resizeStr);

        return str.toString();
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getResizeStr() {
        return resizeStr;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setResizeStr(String resizeStr) {
        this.resizeStr = resizeStr;
    }
}

