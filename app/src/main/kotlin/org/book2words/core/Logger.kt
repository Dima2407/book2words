package org.book2words.core

import android.util.Log

public class Logger {
    companion object {

        private val debuggable = true
        public val TAG: String = "book2words"

        public fun debug(message: String, tag: String = "") {
            if (debuggable) {
                Log.d(TAG, "${tag} ${message}")
            }
        }

        public fun info(message: String) {
            Log.i(TAG, message)
        }

        public fun error(e: Throwable) {
            error("", e)
        }

        public fun error(message: String, e: Throwable) {
            Log.e(TAG, message, e)
        }
    }
}