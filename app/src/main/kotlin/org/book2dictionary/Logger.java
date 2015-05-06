package org.book2dictionary;

import android.util.Log;

public class Logger {

    private static final boolean debuggable = true;
    public static final String TAG = "book2words";

    public static void debug(String message){
        if(debuggable) {
            Log.d(TAG, message);
        }
    }

    public static void info(String message){
        Log.i(TAG, message);
    }

}