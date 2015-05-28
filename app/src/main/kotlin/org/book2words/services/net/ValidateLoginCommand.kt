package org.book2words.services.net

import android.content.Intent
import android.os.Messenger
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import org.book2words.core.Logger

private class ValidateLoginCommand : RestCommand {

    companion object {
        private val TAG = javaClass<ValidateLoginCommand>().getSimpleName();
    }

    override fun execute(arguments: Intent) {
        Logger.debug("execute()", TAG)
        val callback: Messenger? = arguments.getParcelableExtra(B2WService.EXTRA_WAITER)
        Logger.debug("execute() : has waiter - ${callback != null}", TAG)
        Backendless.UserService.isValidLogin(object : AsyncCallback<Boolean> {
            override fun handleResponse(aBoolean: Boolean?) {
                Logger.debug("handleResponse() : ${aBoolean}", TAG)
                if (callback != null) {
                    B2WHandler.send(callback, aBoolean as Boolean, null)
                }
            }

            override fun handleFault(fault: BackendlessFault) {
                Logger.debug("handleFault() : ${fault.getCode()} - ${fault.getMessage()}", TAG)
                if (callback != null) {
                    B2WHandler.sendError(callback, null)
                }
            }
        })
    }
}