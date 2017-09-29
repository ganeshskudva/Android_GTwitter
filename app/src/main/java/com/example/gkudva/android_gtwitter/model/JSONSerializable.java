package com.example.gkudva.android_gtwitter.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gkudva on 28/09/17.
 */

public interface JSONSerializable {
    void configureFromJSON(JSONObject jsonObject) throws JSONException;
}
