package org.book2words.services.net

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger
import com.backendless.Backendless
import org.book2words.dao.LibraryBook
import org.book2words.dao.LibraryDictionary
import org.book2words.data.ConfigsContext
import java.util.HashMap

public class B2WService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.getAction()
            commands.get(action)?.execute(intent)
        }
        return Service.START_STICKY;
    }

    private val commands = HashMap<String, RestCommand>()

    override fun onCreate() {
        super.onCreate()
        val appVersion = "v1"//getVersion()
        val appId = "5FD37D15-656F-74EB-FF6A-798280E35800"
        val appSecret = "21B76DBE-F4AC-3B8C-FFC1-3E9202DCA600"
        val configs = ConfigsContext.getConfigs(this)
        Backendless.initApp(this, appId, appSecret, appVersion)
        commands.put(ACTION_CHECK_LOGIN, ValidateLoginCommand())
        commands.put(ACTION_LOGIN, LoginCommand(configs))
        commands.put(ACTION_LOAD_DICTIONARIES, LoadDictionariesCommand(configs, this))
        commands.put(ACTION_CREATE_DICTIONARY, CreateDictionaryCommand(configs, this))
    }

    private fun getVersion(): String {
        val packageManager = getPackageManager()
        val packageInfo = packageManager.getPackageInfo(getPackageName(), 0)
        return packageInfo.versionName;
    }

    companion object {
        private val TAG = javaClass<B2WService>().getSimpleName()
        private val ACTION_CHECK_LOGIN: String = "org.book2words.intent.action.CHECK_LOGIN"
        private val ACTION_LOGIN: String = "org.book2words.intent.action.LOGIN"
        private val ACTION_CREATE_DICTIONARY: String = "org.book2words.intent.action.CREATE_DICTIONARY"
        private val ACTION_REMOVE_DICTIONARY: String = "org.book2words.intent.action.REMOVE_DICTIONARY"
        private val ACTION_UPDATE_DICTIONARY: String = "org.book2words.intent.action.UPDATE_DICTIONARY"
        private val ACTION_LOAD_DICTIONARIES: String = "org.book2words.intent.action.LOAD_DICTIONARIES"
        private val ACTION_CREATE_BOOK: String = "org.book2words.intent.action.CREATE_BOOK"
        private val ACTION_REMOVE_BOOK: String = "org.book2words.intent.action.REMOVE_BOOK"
        private val ACTION_UPDATE_BOOK: String = "org.book2words.intent.action.UPDATE_BOOK"

        val EXTRA_WAITER: String = "_messenger"

        val EXTRA_LOGIN: String = "_login"

        val EXTRA_PASSWORD: String = "_password"

        val EXTRA_DICTIONARY: String = "_dictionary"
        private val EXTRA_BOOK: String = "_book"

        public fun checkLogin(context: Context, callback: Handler) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_CHECK_LOGIN)
            intent.putExtra(EXTRA_WAITER, Messenger(callback))
            context.startService(intent)
        }

        public fun login(context: Context, login: String, password: String, callback: Handler) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_LOGIN)
            intent.putExtra(EXTRA_LOGIN, login)
            intent.putExtra(EXTRA_PASSWORD, password)
            intent.putExtra(EXTRA_WAITER, Messenger(callback))
            context.startService(intent)
        }

        public fun loadDictionaries(context: Context, callback: Handler? = null) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_LOAD_DICTIONARIES)
            if (callback != null) {
                intent.putExtra(EXTRA_WAITER, Messenger(callback))
            }
            context.startService(intent)
        }

        public fun addDictionary(context: Context, dictionary: String, callback: Handler? = null) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_CREATE_DICTIONARY)
            intent.putExtra(EXTRA_DICTIONARY, dictionary)
            if (callback != null) {
                intent.putExtra(EXTRA_WAITER, Messenger(callback))
            }
            context.startService(intent)
        }

        public fun removeDictionary(context: Context, dictionary: LibraryDictionary, callback: Handler? = null) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_REMOVE_DICTIONARY)
            intent.putExtra(EXTRA_DICTIONARY, dictionary)
            if (callback != null) {
                intent.putExtra(EXTRA_WAITER, Messenger(callback))
            }
            context.startService(intent)
        }

        public fun updateDictionary(context: Context, dictionary: LibraryDictionary, callback: Handler? = null) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_UPDATE_DICTIONARY)
            intent.putExtra(EXTRA_DICTIONARY, dictionary)
            if (callback != null) {
                intent.putExtra(EXTRA_WAITER, Messenger(callback))
            }
            context.startService(intent)
        }

        public fun updateBook(context: Context, book: LibraryBook, callback: Handler? = null) {
            val intent = Intent(context, javaClass<B2WService>())
            intent.setAction(ACTION_UPDATE_BOOK)
            intent.putExtra(EXTRA_BOOK, book)
            if (callback != null) {
                intent.putExtra(EXTRA_WAITER, Messenger(callback))
            }
            context.startService(intent)
        }
    }
}