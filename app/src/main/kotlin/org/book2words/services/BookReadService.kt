package org.book2words.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.book2words.core.Logger
import org.book2words.database.models.LibraryBook
import java.util.HashMap

class BookReadService : Service() {

    private val bounded = HashMap<String, BookBinder>()

    override fun onBind(intent: Intent): IBinder? {
        val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)
        var binder: BookBinder? = null
        val action = intent.action
        if (ACTION_READ.equals(action)) {
            binder = BookReaderBinder(book, this)
        } else if ( ACTION_ADAPT.equals(action)) {
            binder = BookAdapterBinder(book, this)
        }
        if (binder != null) {
            bounded += Pair(action, binder)
        }
        Logger.debug("onBind() $action")
        return binder
    }

    override fun onRebind(intent: Intent?) {
        val action = intent?.action
        Logger.debug("onRebind() $action")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val action = intent?.action
        Logger.debug("onUnbind() $action")
        if(action != null){
            val binder = bounded[action]
            if(binder != null){
                bounded.remove(action)
                binder.release()
            }
        }
        return super.onUnbind(intent)
    }

    companion object {
        private val ACTION_READ = "org.book2words.intent.action.READ"
        private val ACTION_ADAPT = "org.book2words.intent.action.ADAPT"
        private val EXTRA_BOOK = "_book"

        fun bindForRead(context: Context, connection: ServiceConnection, book: LibraryBook) {
            val intent = Intent(context, BookReadService::class.java)
            intent.action = ACTION_READ
            intent.putExtra(EXTRA_BOOK, book)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        fun bindForAdapt(context: Context, connection: ServiceConnection, book: LibraryBook) {
            val intent = Intent(context, BookReadService::class.java)
            intent.action = ACTION_ADAPT
            intent.putExtra(EXTRA_BOOK, book)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}