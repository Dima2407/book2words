package org.book2words.data

import android.content.SharedPreferences
import android.os.Environment
import java.io.File

public class Configs (private val preferences: SharedPreferences) {

    public fun getParagraphsInStep(): Int {
        return preferences.getInt(PARAGRAPHS_IN_STEP, 20)
    }

    public fun getMaxParagraphsInStep(): Int {
        return preferences.getInt(MAX_PARAGRAPHS_IN_STEP, 200)
    }

    public fun getCurrentParagraphsInStep(): Int {
        return preferences.getInt(CURRENT_PARAGRAPHS_IN_STEP, 20)
    }

    public fun setCurrentParagraphsInStep(paragraphs: Int) {
        preferences.edit()
                .putInt(CURRENT_PARAGRAPHS_IN_STEP, paragraphs).commit()
    }

    public fun getCurrentRoot(): java.io.File {
        val path = preferences.getString(CURRENT_ROOT, null)
        if (path == null) {
            return Environment.getExternalStorageDirectory()
        }
        return File(path)
    }

    public fun setCurrentRoot(selectedRoot: File) {
        preferences.edit()
                .putString(CURRENT_ROOT, selectedRoot.getAbsolutePath()).commit()
    }

    companion object {

        private val PARAGRAPHS_IN_STEP = "_paragraphs_in_step"
        private val MAX_PARAGRAPHS_IN_STEP = "_max_paragraphs_in_step"
        private val CURRENT_PARAGRAPHS_IN_STEP = "_current_paragraphs_in_step"
        private val CURRENT_ROOT = "_current_root"

        public fun getRelativePath(file: File): String {
            val storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()
            val root = file.getAbsolutePath()
            val path = root.substring(storageDirectory.length())
            return if (path.length() == 0) "/" else path
        }
    }
}
