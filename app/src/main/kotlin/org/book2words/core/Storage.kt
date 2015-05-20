package org.book2words.core

import android.content.Context
import com.easydictionary.app.R
import org.book2words.models.LibraryLevel
import org.book2words.models.LibraryUser

public class Storage {

    companion object {
        private val NAME = "user_storage"

        private val FIRST_NAME = "_first_name"

        private val LAST_NAME = "_last_name"

        private val LEVEL = "_level"

        private var current : LibraryUser? = null

        public fun saveUser(context : Context, user : LibraryUser): LibraryUser {
            current = LibraryUser(user)
            if(current != null){
                val preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString(FIRST_NAME, current!!.firstName)
                editor.putString(LAST_NAME, current!!.lastName)
                editor.putInt(LEVEL, current!!.level.level)
                editor.commit()
            }
            return current as LibraryUser
        }

        public fun getUser(context : Context): LibraryUser {
            if(current == null){
                val preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                val firstName = preferences.getString(FIRST_NAME, context.getString(R.string.unknown))
                val lastName = preferences.getString(FIRST_NAME, context.getString(R.string.unknown))
                val level = preferences.getInt(LEVEL, 0)
                current = LibraryUser(firstName, lastName, from(level))
            }
            return current as LibraryUser
        }

        private fun from(level: Int): LibraryLevel {
            return LibraryLevel.values().first() {
                it.level == level
            }
        }
    }
}