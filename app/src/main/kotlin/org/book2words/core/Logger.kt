package org.book2words.core

import android.util.Log

class Logger {
    companion object {

        private val debuggable = true
        val TAG: String = "book2words"

        fun debug(message: String, tag: String? = "") {
            if (debuggable) {
                Log.d(TAG, "$tag $message")
            }
        }

        fun info(message: String) {
            Log.i(TAG, message)
        }

        fun error(message: String) {
            Log.e(TAG, message, null)
        }

        fun error(e: Throwable) {
            error("", e)
        }

        fun error(message: String, e: Throwable) {
            Log.e(TAG, message, e)
        }
    }
}