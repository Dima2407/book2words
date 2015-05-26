package org.book2words.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault

public class B2WService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.getAction()
        val callback: Messenger = intent.getParcelableExtra(EXTRA_WAITER)
        when (action) {
            ACTION_CHECK_LOGIN -> {
                checkLogin(callback)
            }

            ACTION_LOGIN -> {
                val login = intent.getStringExtra(EXTRA_LOGIN)
                val password = intent.getStringExtra(EXTRA_PASSWORD)
                login(login, password, callback)
            }
        }
        return Service.START_STICKY
    }

    private fun login(login: String, password: String, callback: Messenger) {

        val user = BackendlessUser();
        user.setEmail(login);
        user.setPassword(password);

        Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser> {
            override fun handleResponse(aBoolean: BackendlessUser?) {
                B2WHandler.send(callback, aBoolean as Boolean, null)
            }

            override fun handleFault(backendlessFault: BackendlessFault) {
                if (backendlessFault.getCode() == "3033") {
                    Backendless.UserService.login(login, password, object : AsyncCallback<BackendlessUser> {
                        override fun handleResponse(aBoolean: BackendlessUser?) {
                            B2WHandler.sendSuccess(callback, aBoolean)
                        }

                        override fun handleFault(backendlessFault: BackendlessFault) {
                            B2WHandler.sendError(callback, null)
                        }
                    }, true)
                } else {
                    B2WHandler.sendError(callback, null)
                }
            }
        });

    }

    private fun checkLogin(callback: Messenger) {
        Backendless.UserService.isValidLogin(object : AsyncCallback<Boolean> {
            override fun handleResponse(aBoolean: Boolean?) {
                B2WHandler.send(callback, aBoolean as Boolean, null)
            }

            override fun handleFault(backendlessFault: BackendlessFault) {
                B2WHandler.sendError(callback, null)
            }
        })
    }

    override fun onCreate() {
        super.onCreate()
        val appVersion = "v1"//getVersion()
        val appId = "5FD37D15-656F-74EB-FF6A-798280E35800"
        val appSecret = "21B76DBE-F4AC-3B8C-FFC1-3E9202DCA600"
        Backendless.initApp(this, appId, appSecret, appVersion);
    }

    private fun getVersion(): String {
        val packageManager = getPackageManager()
        val packageInfo = packageManager.getPackageInfo(getPackageName(), 0)
        return packageInfo.versionName;
    }

    companion object {
        private val ACTION_CHECK_LOGIN: String = "org.book2words.intent.action.CHECK_LOGIN"
        private val ACTION_LOGIN: String = "org.book2words.intent.action.LOGIN"

        private val EXTRA_WAITER: String = "_messenger"

        private val EXTRA_LOGIN: String = "_login"

        private val EXTRA_PASSWORD: String = "_password"

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
    }
}