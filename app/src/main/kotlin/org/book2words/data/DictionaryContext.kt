package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context

public class DictionaryContext {
    companion object {
        private val NAME = "dictionary"

        public fun <T> setup(context: T): CacheDictionary where T : Application, T : PreferenceHolder {
            val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            return CacheDictionary(sharedPreferences)
        }

        public fun <T : Application> getConfigs(context: T): CacheDictionary {
            val application = context as PreferenceHolder
            return application.getDictionary()
        }

        public fun <T : Activity> getConfigs(context: T): CacheDictionary {
            return getConfigs(context.application)
        }

        public fun <T : Service> getConfigs(context: T): CacheDictionary {
            return getConfigs(context.application)
        }

        public fun <T : Fragment> getConfigs(context: T): CacheDictionary {
            return getConfigs(context.activity)
        }
    }
}
