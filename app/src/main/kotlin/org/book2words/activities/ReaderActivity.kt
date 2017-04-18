package org.book2words.activities

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ProgressBar
import org.book2words.R
import org.book2words.database.model.LibraryBook
import org.book2words.screens.BookReadFragment
import org.book2words.services.BookReadService
import org.book2words.services.BookReaderBinder


class ReaderActivity : Activity(), ReaderScreen {
    override fun getReader(): BookReaderBinder {
        return reader!!
    }

    private var bound = false
    private var reader: BookReaderBinder ? = null
    private var progressLoading: ProgressBar? = null
    private var book: LibraryBook? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            reader = service as BookReaderBinder
            bound = true
            prepareReader()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            bound = false
        }
    }

    private fun prepareReader() {
        reader!!.prepare({
            loadPartition()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        book = intent.getParcelableExtra(EXTRA_BOOK)
        findViewById(R.id.button_previous).setOnClickListener({
            goToPrevious()
        })

        findViewById(R.id.button_next).setOnClickListener({
            goToNext()
        })
        progressLoading = findViewById(R.id.progress_loading) as ProgressBar
    }

    private fun loadPartition() {
        progressLoading!!.isIndeterminate = false
        val book = reader!!.getBook()
        progressLoading!!.max = book.countPartitions
        progressLoading!!.progress = book.currentPartition
        var fragment = BookReadFragment.create()

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_text, fragment, FRAGMENT_TAG)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        BookReadService.bindForRead(this, connection, book!!)
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    private fun goToPrevious() {
        if(reader!!.previousPartition()) {
            loadPartition()
        }
    }

    private fun goToNext() {
        if(reader!!.nextPartition()) {
            loadPartition()
        }
    }

    companion object {

        val EXTRA_BOOK: String = "_book"
        private val FRAGMENT_TAG = "_fragment"
    }
}
