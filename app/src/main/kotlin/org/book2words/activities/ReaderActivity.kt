package org.book2words.activities

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import org.book2words.R
import org.book2words.dao.LibraryBook
import org.book2words.screens.BookReadFragment
import org.book2words.services.BookReadService


public class ReaderActivity : Activity(), ReaderScreen {
    override fun getReader(): BookReadService.BookReaderBinder {
        return reader!!
    }

    private var bound = false
    private var reader: BookReadService.BookReaderBinder ? = null
    private var progressView: TextView? = null
    private var book: LibraryBook? = null
    private val connection = object : ServiceConnection {

        override public fun onServiceConnected(className: ComponentName, service: IBinder) {
            reader = service as BookReadService.BookReaderBinder
            bound = true
            prepareReader()
        }

        override public fun onServiceDisconnected(className: ComponentName) {
            bound = false
        }
    }

    private fun prepareReader() {
        reader!!.prepare(book!!, {
            loadPartition()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        book = getIntent().getParcelableExtra(EXTRA_BOOK)
        val titleView = findViewById(R.id.text_title) as TextView
        titleView.setText(book!!.getName())
        progressView = findViewById(R.id.text_progress) as TextView
        findViewById(R.id.button_previous).setOnClickListener({
            goToPrevious()
        })

        findViewById(R.id.button_next).setOnClickListener({
            goToNext()
        })
    }

    private fun loadPartition() {
        progressView!!.setText("${book!!.getCurrentPartition()}/${book!!.getCountPartitions()}")
        var fragment = BookReadFragment.create(book!!.getId(), book!!.getCurrentPartition())

        val transaction = getFragmentManager().beginTransaction()
        transaction.replace(R.id.frame_text, fragment, FRAGMENT_TAG)
        transaction.commit()
    }

    override fun onStart() {
        super<Activity>.onStart()
        val intent = Intent(this, javaClass<BookReadService>())
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super<Activity>.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    private fun goToPrevious() {
        book!!.setCurrentPartition(book!!.getCurrentPartition() - 1)
        loadPartition()
    }

    private fun goToNext() {
        book!!.setCurrentPartition(book!!.getCurrentPartition() + 1)
        loadPartition()
    }

    companion object {

        public val EXTRA_BOOK: String = "_book"
        private val FRAGMENT_TAG = "_fragment"
    }
}
