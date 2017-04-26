package org.book2words.data

import android.content.SharedPreferences

public class CacheDictionary(private val preferences: SharedPreferences) {

    public fun take(word: String): String? {
        return preferences.getString(word, null)
    }

    public fun save(word: String, result: String) {
        preferences.edit()
                .putString(word, result).commit()
    }
}
