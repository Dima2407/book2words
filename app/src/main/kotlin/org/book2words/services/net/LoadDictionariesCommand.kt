package org.book2words.services.net

import android.app.Service
import android.content.Intent
import android.os.Messenger
import com.backendless.Backendless
import com.backendless.BackendlessCollection
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.BackendlessDataQuery
import org.book2words.core.Logger
import org.book2words.dao.LibraryDictionary
import org.book2words.data.Configs
import org.book2words.data.DataContext

private class LoadDictionariesCommand(private val configs: Configs, private val context: Service) : RestCommand {
    companion object {
        private val TAG = javaClass<LoadDictionariesCommand>().getSimpleName();
    }

    override fun execute(arguments: Intent) {
        Logger.debug("execute()", TAG)
        val callback: Messenger? = arguments.getParcelableExtra(B2WService.EXTRA_WAITER)
        Logger.debug("execute() : has waiter - ${callback != null}", TAG)
        Logger.debug("ownerId ${configs.getUserId()}", TAG)

        val query = BackendlessDataQuery()
        query.setWhereClause("personal = false OR ownerId = '${ configs.getUserId()}'")
        Backendless.Persistence.mapTableToClass("Dictionaries", javaClass<LibraryDictionary>())

        Backendless.Persistence.of(javaClass<LibraryDictionary>()).find(query, object : AsyncCallback<BackendlessCollection<LibraryDictionary>> {
            override fun handleResponse(p0: BackendlessCollection<LibraryDictionary>?) {
                Logger.debug("handleResponse() : ${p0!!.getData().size()}")
                DataContext.getLibraryDictionaryDao(context)
                        .insertOrIgnoreInTx(p0.getData())
                if(callback != null){
                    B2WHandler.sendSuccess(callback, p0.getData())
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