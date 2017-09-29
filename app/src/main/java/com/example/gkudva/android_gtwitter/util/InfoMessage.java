package com.example.gkudva.android_gtwitter.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by gkudva on 29/09/17.
 */

public class InfoMessage {
    private Context context;

    public InfoMessage(Context context) {
        this.context = context;
    }

    public void showMessage(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}