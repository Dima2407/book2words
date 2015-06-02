package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context

public class DictionaryContext {
    companion object {
        private val NAME = "dictionary"

        public fun setup<T : Application> (context: T): CacheDictionary where T : PreferenceHolder {
            val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            return CacheDictionary(sharedPreferences)
        }

        public fun getConfigs<T : Application>(context: T): CacheDictionary {
            val application = context as PreferenceHolder
            return application.getDictionary()
        }

        public fun getConfigs<T : Activity>(context: T): CacheDictionary {
            return getConfigs(context.getApplication())
        }

        public fun getConfigs<T : Service>(context: T): CacheDictionary {
            return getConfigs(context.getApplication())
        }

        public fun getConfigs<T : Fragment>(context: T): CacheDictionary {
            return getConfigs(context.getActivity())
        }
    }
}
