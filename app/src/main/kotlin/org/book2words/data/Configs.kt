package org.book2words.data

import android.content.SharedPreferences
import android.os.Environment
import java.io.File

public class Configs (private val preferences: SharedPreferences) {

    public fun getParagraphsInStep(): Int {
        return preferences.getInt(PARAGRAPHS_IN_STEP, 10)
    }

    public fun getMaxParagraphsInStep(): Int {
        return preferences.getInt(MAX_PARAGRAPHS_IN_STEP, 50)
    }

    public fun getCurrentParagraphsInStep(): Int {
        return preferences.getInt(CURRENT_PARAGRAPHS_IN_STEP, 10)
    }

    public fun setCurrentParagraphsInStep(paragraphs: Int) {
        preferences.edit()
                .putInt(CURRENT_PARAGRAPHS_IN_STEP, paragraphs).commit()
    }

    public fun getCurrentRoot(): java.io.File {
        return Environment.getExternalStorageDirectory()
    }

    companion object {

        private val PARAGRAPHS_IN_STEP = "_paragraphs_in_step"
        private val MAX_PARAGRAPHS_IN_STEP = "_max_paragraphs_in_step"
        private val CURRENT_PARAGRAPHS_IN_STEP = "_current_paragraphs_in_step"
        private val CURRENT_USER_ID = "_current_user_id"


        public fun getRelativePath(file: File): String {
            val storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()
            val root = file.getAbsolutePath()
            val path = root.substring(storageDirectory.length())
            return if (path.length() == 0) "/" else path
        }
    }


    fun setUserId(objectId: String) {
        preferences.edit()
                .putString(CURRENT_USER_ID, objectId).commit()
    }

    fun getUserId(): String {
        return preferences.getString(CURRENT_USER_ID, "4AD0BA02-B8CF-29F6-FFC9-AF57765C5A00")
    }
}
