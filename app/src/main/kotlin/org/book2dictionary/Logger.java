package org.book2dictionary;

import android.util.Log;

import java.io.IOException;

public class Logger {

    private static final boolean debuggable = true;
    public static final String TAG = "book2words";

    public static void debug(String message) {
        debug(null, message);
    }

    public static void debug(String tag, String message) {
        if (debuggable) {
            if (tag != null) {
                message = String.format("%s - %s", tag, message);
            }
            Log.d(TAG, message);
        }
    }

    public static void info(String message) {
        Log.i(TAG, message);
    }

    public static void error(Throwable e) {
        error("", e);
    }

    public static void error(String message, Throwable e) {
        Log.e(TAG, message, e);
    }
}