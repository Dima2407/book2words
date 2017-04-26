package org.book2words.services

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.book2words.MainActivity
import org.book2words.R
import org.book2words.core.Logger
import org.book2words.database.models.LibraryBook
import org.book2words.data.DataContext
import org.book2words.models.TextSplitter

public class BookSplitService : IntentService(BookSplitService::class.simpleName) {

    private val NOTIFICATION_ID = 101

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action

            val builder = Notification.Builder(this)
            builder.setSmallIcon(R.drawable.ic_launcher)
            builder.setProgress(100, 0, true)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK)

            builder.setContentIntent(pendingIntent)
            startForeground(NOTIFICATION_ID, builder.build())

            when (action) {
                OPEN_ACTION -> {
                    val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)

                    builder.setContentTitle("Adapting ${book.name}")
                    startForeground(NOTIFICATION_ID, builder.build())

                    startBook(book)

                }
                CLOSE_ACTION -> {
                    val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)

                    builder.setContentTitle("Adapting ${book.name}")
                    startForeground(NOTIFICATION_ID, builder.build())

                    stopBook(book)
                }
                SPLIT_ACTION -> {
                    val id = intent.getLongExtra(EXTRA_ID, 0)
                    val index = intent.getIntExtra(EXTRA_INDEX, 0)
                    val text = intent.getStringExtra(EXTRA_TEXT)

                    builder.setContentTitle("Processing ${index} chapter")
                    startForeground(NOTIFICATION_ID, builder.build())

                    splitText(id, index, text)
                }

            }
        }
    }

    private fun stopBook(book: LibraryBook) {
        Logger.debug("stopBook(${book.id})", TAG)

        val textSplitter = TextSplitter.getInstance()

        book.adapted = LibraryBook.ADAPTED
        book.wordsCount = textSplitter.getAllFoundWordsCount()
        book.uniqueWordsCount = textSplitter.getUniqueWordsCount()

        book.countPartitions = textSplitter.getPartitionsCount()

        Logger.debug("time split ${(System.currentTimeMillis() - time) / 1000}", TAG)
        textSplitter.clearCapital()
        textSplitter.clearWithApostrophe()
        val strings = resources.getStringArray(R.array.worlds_english)
        Logger.debug("default words ${strings.size}", TAG)
        textSplitter.clearWidelyUsed(strings)
        textSplitter.clearWithDuplicates()

        val dictionaries = DataContext.getDictionaries()

        dictionaries.forEach {
            textSplitter.clearFromDictionary(it.path)
        }

        Logger.debug("time clear ${(System.currentTimeMillis() - time) / 1000}", TAG)

        DataContext.getWordsFoundDao(this).save(textSplitter.getWords())

        Logger.debug("stopBook(${book.id})", TAG)

        book.unknownWordsCount = textSplitter.getUnknownWordsCount()

        textSplitter.release()

        DataContext.getLibraryBookDao(this).save(book)

        sendBroadcast(Intent(LibraryService.ACTION_PREPARED))

    }

    private fun startBook(book: LibraryBook) {
        time = System.currentTimeMillis()
        Logger.debug("startBook(${book.id})", TAG)
        book.adapted = LibraryBook.ADAPTING

        DataContext.getLibraryBookDao(this).save(book)

        sendBroadcast(Intent(LibraryService.ACTION_PREPARED))

        TextSplitter.getInstance().release()
    }

    private fun splitText(id: Long, index: Int, text: String) {
        Logger.debug("splitText($index)", TAG)
        val textSplitter = TextSplitter.getInstance()
        textSplitter.findCapital(text)
        val partitions = textSplitter.toPartitions(id, text)
        partitions.forEach {
            val partition = it
            textSplitter.split(partition, id)
        }

        DataContext.getPartsDao(this).save(partitions);

    }

    companion object {

        @Deprecated("")
        var time = 0L

        private val TAG = BookSplitService::class.simpleName

        private val OPEN_ACTION = "org.book2words.intent.action.OPEN"
        private val CLOSE_ACTION = "org.book2words.intent.action.CLOSE"
        private val SPLIT_ACTION = "org.book2words.intent.action.SPLIT"

        private val EXTRA_ID: String = "_id"
        private val EXTRA_INDEX: String = "_index"
        private val EXTRA_TEXT: String = "_text"

        private val EXTRA_BOOK: String = "_book"

        fun save(context: Context, id: Long, index: Int, text: String) {
            val intent = Intent(context, BookSplitService::class.java)
            intent.action = SPLIT_ACTION
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_INDEX, index)
            intent.putExtra(EXTRA_TEXT, text)
            context.startService(intent)
        }

        public fun openBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, BookSplitService::class.java)
            intent.setAction(OPEN_ACTION)
            intent.putExtra(EXTRA_BOOK, book)
            context.startService(intent)
        }

        public fun closeBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, BookSplitService::class.java)
            intent.setAction(CLOSE_ACTION)
            intent.putExtra(EXTRA_BOOK, book)
            context.startService(intent)
        }
    }
}