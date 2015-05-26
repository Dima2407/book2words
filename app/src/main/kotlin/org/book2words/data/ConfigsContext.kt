package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context

public class ConfigsContext {
    companion object {
        private val NAME = "configs"

        public fun setup<T : Application> (context: T): Configs where T : ConfigsHolder {
            val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            return Configs(sharedPreferences)
        }

        public fun getConfigs<T : Application>(context: T): Configs {
            val application = context as ConfigsHolder
            return application.getConfigs()
        }

        public fun getConfigs<T : Activity>(context: T): Configs {
            return getConfigs(context.getApplication())
        }

        public fun getConfigs<T : Service>(context: T): Configs {
            return getConfigs(context.getApplication())
        }

        public fun getConfigs<T : Fragment>(context: T): Configs {
            return getConfigs(context.getActivity())
        }
    }
}
