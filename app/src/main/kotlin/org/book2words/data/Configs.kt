package org.book2words.data

import android.content.SharedPreferences
import android.os.Environment
import java.io.File

class Configs(private val preferences: SharedPreferences) {

    fun getCurrentRoot() = Environment.getExternalStorageDirectory()!!

    fun dictionariesLoaded(success: Boolean) = preferences.edit().
            putBoolean(IS_FIRST_LAUNCHING, success).apply()

    fun isDictionariesLoaded() = preferences.getBoolean(IS_FIRST_LAUNCHING, false)


    companion object {

        private val IS_FIRST_LAUNCHING = "_is_first_launching"


        fun getRelativePath(file: File): String {
            val storageDirectory = Environment.getExternalStorageDirectory().absolutePath
            val root = file.absolutePath
            val path = root.substring(storageDirectory.length)
            return if (path.isEmpty()) "/" else path
        }
    }
}
