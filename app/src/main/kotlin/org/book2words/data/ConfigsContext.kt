package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context

public class ConfigsContext {
    companion object {
        private val NAME = "configs"

        public fun <T> setup(context: T): Configs where T : Application, T : PreferenceHolder {
            val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            return Configs(sharedPreferences)
        }

        public fun <T> getConfigs(context: T) :Configs where T : Application {
            val application = context as PreferenceHolder
            return application.getConfigs()
        }

        public fun <T : Activity> getConfigs(context: T): Configs {
            return getConfigs(context.application)
        }

        public fun <T : Service> getConfigs(context: T): Configs {
            return getConfigs(context.application)
        }

        public fun <T : Fragment> getConfigs(context: T): Configs {
            return getConfigs(context.activity)
        }
    }
}
