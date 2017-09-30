package com.example.gkudva.android_gtwitter.util;

import android.util.Log;

import java.io.IOException;

/**
 * Created by gkudva on 29/09/17.
 */

public class NetworkUtil {
    public static String TAG = "NetworkUtil";
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            Log.d(TAG, "isOnLine() -- IOException");
        } catch (InterruptedException e) {
            Log.d(TAG, "isOnLine() -- InterruptedException");
        }
        return false;
    }
}
