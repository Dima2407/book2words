package org.book2words.services.net

import android.content.Intent
import android.os.Messenger
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import org.book2words.core.Logger
import org.book2words.data.Configs

private class LoginCommand(private val configs: Configs) : RestCommand {
    companion object {
        private val TAG = javaClass<LoginCommand>().getSimpleName()

        private val USER_EXIST = "3033"
    }

    override fun execute(arguments: Intent) {
        Logger.debug("execute()", TAG)
        val callback: Messenger? = arguments.getParcelableExtra(B2WService.EXTRA_WAITER)
        val login = arguments.getStringExtra(B2WService.EXTRA_LOGIN)
        val password = arguments.getStringExtra(B2WService.EXTRA_PASSWORD)
        Logger.debug("execute() : has waiter - ${callback != null}", TAG)
        val user = BackendlessUser();
        user.setEmail(login);
        user.setPassword(password);

        Logger.debug("execute() - register", TAG)
        Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser> {
            override fun handleResponse(result: BackendlessUser?) {
                Logger.debug("objectId ${configs.getUserId()}", TAG)
                if (callback != null) {
                    B2WHandler.sendSuccess(callback, result as BackendlessUser)
                }
            }

            override fun handleFault(fault: BackendlessFault) {
                if (fault.getCode() == USER_EXIST) {
                    Logger.debug("execute() - login", TAG)
                    Backendless.UserService.login(login, password, object : AsyncCallback<BackendlessUser> {
                        override fun handleResponse(result: BackendlessUser?) {
                            Logger.debug("objectId ${configs.getUserId()}", TAG)
                            configs.setUserId(result!!.getObjectId())
                            if (callback != null) {
                                B2WHandler.sendSuccess(callback, result)
                            }
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            Logger.debug("handleFault() : ${fault.getCode()} - ${fault.getMessage()}", TAG)
                            if (callback != null) {
                                B2WHandler.sendError(callback, null)
                            }
                        }
                    }, true)
                } else {
                    Logger.debug("handleFault() : ${fault.getCode()} - ${fault.getMessage()}", TAG)
                    if (callback != null) {
                        B2WHandler.sendError(callback, null)
                    }
                }
            }
        });
    }
}
