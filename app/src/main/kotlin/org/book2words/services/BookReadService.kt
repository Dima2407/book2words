package org.book2words.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.book2words.dao.LibraryBook

public class BookReadService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)
        if (ACTION_READ.equals(intent.getAction())) {
            return BookReaderBinder(book, this)
        } else if ( ACTION_ADAPT.equals(intent.getAction())) {
            return BookAdapterBinder(book, this)
        }
        return null
    }

    companion object {
        private val ACTION_READ = "org.book2words.intent.action.READ"
        private val ACTION_ADAPT = "org.book2words.intent.action.ADAPT"
        private val EXTRA_BOOK = "_book"

        public fun bindForRead(context: Context, connection: ServiceConnection, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookReadService>())
            intent.setAction(ACTION_READ)
            intent.putExtra(EXTRA_BOOK, book)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        public fun bindForAdapt(context: Context, connection: ServiceConnection, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookReadService>())
            intent.setAction(ACTION_ADAPT)
            intent.putExtra(EXTRA_BOOK, book)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }
}