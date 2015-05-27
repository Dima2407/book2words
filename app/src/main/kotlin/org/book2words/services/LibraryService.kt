package org.book2words.services

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import net.sf.jazzlib.ZipFile
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubReader
import org.book2words.MainActivity
import org.book2words.R
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.data.DataContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

public class LibraryService : IntentService(javaClass<LibraryService>().getSimpleName()) {

    private val NOTIFICATION_ID = 100

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction()
            if (ACTION_SYNC == action) {

                val notificationIntent = Intent(this, javaClass<MainActivity>())
                val pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                val builder = Notification.Builder(this)
                builder.setSmallIcon(R.drawable.ic_launcher)
                builder.setContentTitle("Book processing")
                builder.setProgress(100, 0, true);
                builder.setContentIntent(pendingIntent)

                startForeground(NOTIFICATION_ID, builder.build());

                val path = intent.getStringExtra(EXTRA_ROOT)

                val libraryBook = LibraryBook()

                prepareBook(libraryBook, path)

                sendBroadcast(Intent(ACTION_PREPARED))
            }
        }
    }


    private fun prepareBook(libraryBook: LibraryBook, path: String) {
        Logger.debug("prepareBook() ${path}")
        try {
            val eBook = EpubReader().readEpubLazy(ZipFile(path), "utf-8")

            libraryBook.setName(eBook.getTitle())
            val authorsString = StringBuilder()
            val authors = eBook.getMetadata().getAuthors()
            authors.forEachIndexed { i, author ->
                authorsString.append(author.getFirstname())
                authorsString.append(" ")
                authorsString.append(author.getLastname())
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

    }

    private fun saveCover(id: Long, coverImage: Resource) {
        val coverFile = FileStorage.createCoverFile(id, coverImage.getMediaType().getDefaultExtension())
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

    companion object {

        public val ACTION_PREPARED: String = "org.book2words.intent.action.PREPARED"

        public val ACTION_CLEARED: String = "org.book2words.intent.action.CLEARED"

        private val ACTION_SYNC = "org.book2words.intent.action.SYNC"

        private val EXTRA_ROOT = "_root"

        public fun addBook(context: Context, path: File) {
            val intent = Intent(context, javaClass<LibraryService>())
            intent.setAction(ACTION_SYNC)
            intent.putExtra(EXTRA_ROOT, path.getAbsolutePath())
            context.startService(intent)
        }
    }
}