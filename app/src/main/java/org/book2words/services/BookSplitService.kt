package org.book2words.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.easydictionary.app.R
import org.book2dictionary.Logger
import org.book2words.B2WApplication
import org.book2words.Storage
import org.book2words.dao.LibraryBook
import org.data.DataContext
import org.models.TextSplitter
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

public class BookSplitService : IntentService(javaClass<BookSplitService>().getSimpleName()) {
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction()

            when (action) {
                OPEN_ACTION -> {
                    val book : LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)
                    startBook(book)
                }
                CLOSE_ACTION -> {
                    val book : LibraryBook = intent.getParcelableExtra(EXTRA_BOOK)
                    stopBook(book)
                }
                SPLIT_ACTION -> {
                    val id = intent.getLongExtra(EXTRA_ID, 0)
                    val index = intent.getIntExtra(EXTRA_INDEX, 0)
                    val text = intent.getStringExtra(EXTRA_TEXT)
                    saveText(id, index, text)
                    splitText(index, text)
                }
            }
        }
    }

    private fun stopBook(book : LibraryBook) {
        Logger.debug(TAG, "stopBook(${book.getId()})")

        val textSplitter = TextSplitter.getInstance()

        textSplitter.clearCapital()
        textSplitter.clearWithApostrophe()
        textSplitter.clearWidelyUsed(getResources().getStringArray( R.array.widely_worlds))
        textSplitter.clearWithDuplicates()

        val dictionaries = DataContext.getUserDictionaries()

        dictionaries.forEach {
            if(it.getUse()) {
                textSplitter.clearFromDictionary(File(it.getPath()))
            }
        }

        textSplitter.release()

        val file = Storage.createWordsFile(book.getId());
        val bos = ObjectOutputStream(FileOutputStream(file))
        bos.writeObject(textSplitter.words)
        bos.flush()
        bos.close()

        Logger.debug(TAG, "stopBook(${book.getId()})")

        book.setAdapted(true)

        DataContext.getLibraryBookDao(this).update(book)
    }

    private fun startBook(book : LibraryBook) {
        Logger.debug(TAG, "startBook(${book.getId()})")
        TextSplitter.getInstance().release()
    }

    private fun saveText(id: Long, index: Int, text: String) {
        Logger.debug(TAG, "saveText(${id}) - ${index}")
        val file = Storage.createChapterFile(id, index);
        val bos = FileOutputStream(file).writer(Charsets.UTF_8)
        bos.write(text)
        bos.flush()
        bos.close()
    }

    private fun splitText(index: Int, text: String){
        val textSplitter = TextSplitter.getInstance()
        textSplitter.findCapital(text)
        textSplitter.split("${index}", text);
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
            intent.putExtra(EXTRA_ID, id);
            intent.putExtra(EXTRA_INDEX, index)
            intent.putExtra(EXTRA_TEXT, text)
            context.startService(intent)
        }

        public fun openBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookSplitService>());
            intent.setAction(OPEN_ACTION)
            intent.putExtra(EXTRA_BOOK, book);
            context.startService(intent)
        }

        public fun closeBook(context: Context, book: LibraryBook) {
            val intent = Intent(context, javaClass<BookSplitService>());
            intent.setAction(CLOSE_ACTION)
            intent.putExtra(EXTRA_BOOK, book);
            context.startService(intent)
        }
    }
}
