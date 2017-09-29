package com.example.gkudva.android_gtwitter.util;

import android.util.Log;

import com.example.gkudva.android_gtwitter.model.JSONSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by gkudva on 28/09/17.
 */

public class JSONDeserializer <T extends JSONSerializable> {
    private static final String TAG = JSONDeserializer.class.getName();
    private Class<T> mClazz;

    public JSONDeserializer(Class<T> clazz) {
        this.mClazz = clazz;
    }

    public List<T> fromJSONArrayToList(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null)
            return null;

        List<T> list = new ArrayList<T>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            T obj = configureJSONObject(jsonObj);
            if (obj != null)
                list.add(obj);
        }

        return list;
    }

    public T configureJSONObject(JSONObject json) {
        try {
            if(json == null)
                return null;

            T obj = mClazz.newInstance();
            obj.configureFromJSON(json);
            return obj;
        } catch (InstantiationException e) {
            Log.d( TAG, " InstantiationException");
        } catch (IllegalAccessException e) {
            Log.d( TAG," IllegalAccessException");
        } catch (JSONException e) {
            log.d( TAG," JSONException");
        }

        return null;
    }
}