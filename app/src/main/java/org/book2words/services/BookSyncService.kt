package org.book2words.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import net.sf.jazzlib
import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubReader
import org.book2dictionary.Logger
import org.book2words.B2WApplication
import org.book2words.Storage
import org.book2words.dao.LibraryBook
import org.data.DataContext

import java.io.*
import java.util.zip.ZipFile


public class BookSyncService : IntentService(javaClass<BookSyncService>().getSimpleName()) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction();
            if (ACTION_PREPARE.equals(action)) {
                val path = intent.getStringExtra(EXTRA_PATH)
                prepareBook(path)
            } else if (ACTION_CLEAR.equals(action)) {
                clear()
            }
        }
    }

    private fun prepareBook(path: String) {
        Logger.debug("prepareBook() ${path}")

        try {
            val eBook = EpubReader().readEpubLazy(jazzlib.ZipFile(path), "utf-8")
            val libraryBook = LibraryBook()
            libraryBook.setName(eBook.getTitle())
            val authorsString = StringBuilder()
            val authors = eBook.getMetadata().getAuthors()
            for (i in authors.indices) {
                val a = authors.get(i)
                authorsString.append(a.getFirstname())
                authorsString.append(" ")
                authorsString.append(a.getLastname())
                if (i != authors.size() - 1) {
                    authorsString.append(", ")
                }
            }
            Logger.debug("prepareBook() ${authorsString}")
            libraryBook.setAuthors(authorsString.toString())
            libraryBook.setPath(path)

            val id = DataContext.getLibraryBookDao(this).insertOrReplace(libraryBook)

            Logger.debug("prepareBook() ${id}")
            val coverImage = eBook.getCoverImage()
            if (coverImage != null) {
                saveCover(id, coverImage)
            }
        } catch (e: IOException) {
            Logger.error(e)
        }

        val localIntent = Intent(ACTION_PREPARED)
        localIntent.putExtra(EXTRA_PATH, path)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private fun saveCover(id: Long, coverImage: Resource) {
        val coverFile = Storage.createCoverFile(id, coverImage.getMediaType().getDefaultExtension())
        Logger.debug("saveCover() ${coverFile.getAbsolutePath()}")
        val bis = coverImage.getInputStream().buffered()
        val bos = FileOutputStream(coverFile).buffered()
        try {
            bis.copyTo(bos, 2048)
        } catch (e: IOException) {
            Logger.error(e)
        } finally {
            bis.close()
            bos.close()
        }
    }

    private fun clear() {
        Logger.debug("clear()")
        Storage.clearCovers()
        DataContext.getLibraryBookDao(this).deleteAll()
    }

    companion object {

        private val ACTION_PREPARE = "org.book2words.intent.action.PREPARE"
        private val ACTION_CLEAR = "org.book2words.intent.action.CLEAR"

        public val ACTION_PREPARED: String = "org.book2words.intent.action.PREPARED"

        public val EXTRA_PATH: String = "_path"

        public fun prepareBook(context: Context, path: String) {
            val intent = Intent(context, javaClass<BookSyncService>())
            intent.setAction(ACTION_PREPARE)
            intent.putExtra(EXTRA_PATH, path)
            context.startService(intent)
        }

        fun clear(context: Context) {
            val intent = Intent(context, javaClass<BookSyncService>())
            intent.setAction(ACTION_CLEAR)
            context.startService(intent)
        }
    }
}
