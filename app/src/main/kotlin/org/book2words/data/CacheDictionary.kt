package org.book2words.data

import android.content.SharedPreferences

class CacheDictionary(private val preferences: SharedPreferences) {

    fun take(word: String): String? {
        return preferences.getString(word, null)
    }

    fun save(word: String, result: String) {
        preferences.edit()
                .putString(word, result).commit()
    }
}
