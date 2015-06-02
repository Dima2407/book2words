package org.book2words.services

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import org.book2words.MainActivity
import org.book2words.R
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.dao.LibraryDictionaryDao
import org.book2words.data.ConfigsContext
import org.book2words.data.DataContext
import org.book2words.models.TextSplitter
import java.io.FileOutputStream

public class BookSplitService : IntentService(javaClass<BookSplitService>().getSimpleName()) {

    private val NOTIFICATION_ID = 101

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction()

            val builder = Notification.Builder(this)
            builder.setSmallIcon(R.drawable.ic_launcher)
            builder.setProgress(100, 0, true);

            val notificationIntent = Intent(this, javaClass<MainActivity>())
            val pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

            builder.setContentIntent(pendingIntent)
            startForeground(NOTIFICATION_ID, builder.build());

            when (action) {
                OPEN_ACTION -> {
                    val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)

                    builder.setContentTitle("Adapting ${book.getName()}")
                    startForeground(NOTIFICATION_ID, builder.build());

                    startBook(book)

                }
                CLOSE_ACTION -> {
                    val book: LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)

                    builder.setContentTitle("Adapting ${book.getName()}")
                    startForeground(NOTIFICATION_ID, builder.build());

                    stopBook(book)
                }
                SPLIT_ACTION -> {
                    val id = intent.getLongExtra(EXTRA_ID, 0)
                    val index = intent.getIntExtra(EXTRA_INDEX, 0)
                    val text = intent.getStringExtra(EXTRA_TEXT)

                    builder.setContentTitle("Processing ${index} chapter")
                    startForeground(NOTIFICATION_ID, builder.build());

                    splitText(id, index, text)
                }

            }
        }
    }

    private fun stopBook(book: LibraryBook) {
        Logger.debug("stopBook(${book.getId()})", TAG)

        val textSplitter = TextSplitter.getInstance()

        book.setAdapted(LibraryBook.ADAPTED)
        book.setAllWords(textSplitter.getAllFoundWordsCount())
        book.setUniqueWords(textSplitter.getUniqueWordsCount())

        book.setCurrentPartition(1)
        book.setCountPartitions(textSplitter.getPartitionsCount())

        textSplitter.clearCapital()
        textSplitter.clearWithApostrophe()
        textSplitter.clearWidelyUsed(getResources().getStringArray(R.array.widely_worlds))
        textSplitter.clearWithDuplicates()

        val dictionaries = DataContext.getLibraryDictionaryDao(this)
                .queryBuilder()
                .where(LibraryDictionaryDao.Properties.Use.eq(true))
                .list()

        dictionaries.forEach {
            textSplitter.clearFromDictionary(it.getPath())
        }

        val file = FileStorage.createWordsFile(book.getId());
        val bos = FileOutputStream(file).buffered().writer("UTF-8")
        val serializer = Gson()
        serializer.toJson(textSplitter.words, bos)

        bos.flush()
        bos.close()

        Logger.debug("stopBook(${book.getId()})", TAG)

        book.setUnknownWords(textSplitter.getUnknownWordsCount())

        textSplitter.release()

        DataContext.getLibraryBookDao(this).update(book)

        sendBroadcast(Intent(LibraryService.ACTION_PREPARED))
    }

    private fun startBook(book: LibraryBook) {
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
        textSplitter.findCapital(text)
        val configs = ConfigsContext.getConfigs(this)
        val partitions = textSplitter.toPartitions(index, text, configs.getCurrentParagraphsInStep())
        partitions.forEach {
            val partition = it.getValue()
            textSplitter.split(partition)

            val file = FileStorage.createChapterFile(id, textSplitter.getPartitionsCount())
            val bos = FileOutputStream(file).writer(Charsets.UTF_8).buffered()
            partition.forEach {
                bos.write(it)
                bos.newLine()
                bos.flush()
            }
            bos.close()
        }

    }

    companion object {

        private val TAG = javaClass<BookSplitService>().getSimpleName()

        private val OPEN_ACTION = "org.book2words.intent.action.OPEN"
        private val CLOSE_ACTION = "org.book2words.intent.action.CLOSE"
        private val SPLIT_ACTION = "org.book2words.intent.action.SPLIT"

        private val EXTRA_ID: String = "_id"
        private val EXTRA_INDEX: String = "_index"
        private val EXTRA_TEXT: String = "_text"

        private val EXTRA_BOOK: String = "_book"

        public fun save(context: Context, id: Long, index: Int, text: String) {
            val intent = Intent(context, javaClass<BookSplitService>());
            intent.setAction(SPLIT_ACTION)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_INDEX, index)
            intent.putExtra(EXTRA_TEXT, text)
            context.startService(intent)
        }

        public fun openBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookSplitService>());
            intent.setAction(OPEN_ACTION)
            intent.putExtra(EXTRA_BOOK, book)
            context.startService(intent)
        }

        public fun closeBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookSplitService>());
            intent.setAction(CLOSE_ACTION)
            intent.putExtra(EXTRA_BOOK, book)
            context.startService(intent)
        }
    }
}