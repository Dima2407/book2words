package org.book2words.services

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.book2words.MainActivity
import org.book2words.R
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.data.ConfigsContext
import org.book2words.data.DataContext
import org.book2words.models.TextSplitter
import java.io.FileOutputStream

public class BookSplitService : IntentService(BookSplitService::class.simpleName) {

    private val NOTIFICATION_ID = 101

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction()

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

                    builder.setContentTitle("Adapting ${book.getName()}")
                    startForeground(NOTIFICATION_ID, builder.build())

                    startBook(book)

                }
                CLOSE_ACTION -> {
                    val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)

                    builder.setContentTitle("Adapting ${book.getName()}")
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
        Logger.debug("stopBook(${book.getId()})", TAG)

        val textSplitter = TextSplitter.getInstance()

        book.setAdapted(LibraryBook.ADAPTED)
        book.setWordsCount(textSplitter.getAllFoundWordsCount())
        book.setUniqueWordsCount(textSplitter.getUniqueWordsCount())

        book.setCurrentPartition(1)
        book.setCountPartitions(textSplitter.getPartitionsCount())

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

        val file = FileStorage.createWordsFile(book.getId())
        val bos = FileOutputStream(file).bufferedWriter(Charsets.UTF_8)

        val words = textSplitter.getWords()
        words.forEach {
            bos.appendln(it.toSeparatedString(";"))
            bos.flush()
        }

        bos.close()

        Logger.debug("stopBook(${book.getId()})", TAG)

        book.setUnknownWordsCount(textSplitter.getUnknownWordsCount())

        textSplitter.release()

        DataContext.getLibraryBookDao(this).update(book)

        sendBroadcast(Intent(LibraryService.ACTION_PREPARED))
        Logger.debug("time store ${(System.currentTimeMillis() - time) / 1000}", TAG)
    }

    private fun startBook(book: LibraryBook) {
        time = System.currentTimeMillis()
        Logger.debug("startBook(${book.getId()})", TAG)
        book.setAdapted(LibraryBook.ADAPTING)

        DataContext.getLibraryBookDao(this).update(book)

        sendBroadcast(Intent(LibraryService.ACTION_PREPARED))

        FileStorage.clearBook(book.getId())
        TextSplitter.getInstance().release()
    }

    private fun splitText(id: Long, index: Int, text: String) {
        Logger.debug("splitText(${index})", TAG)
        val textSplitter = TextSplitter.getInstance()
        val configs = ConfigsContext.getConfigs(this)
        textSplitter.findCapital(text)
        val partitions = textSplitter.toPartitions(index, text, configs.getCurrentParagraphsInStep())
        partitions.forEach {
            val partition = it.value
            textSplitter.nextPartition()

            textSplitter.split(partition)

            val file = FileStorage.createChapterFile(id, textSplitter.getPartitionsCount())
            val bos = FileOutputStream(file).bufferedWriter(Charsets.UTF_8)
            partition.forEach {
                bos.appendln(it)
                bos.flush()
            }
            bos.close()
        }

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

        public fun save(context: Context, id: Long, index: Int, text: String) {
            val intent = Intent(context, BookSplitService::class.java)
            intent.setAction(SPLIT_ACTION)
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