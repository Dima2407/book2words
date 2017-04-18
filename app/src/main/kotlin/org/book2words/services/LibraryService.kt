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
import org.book2words.database.model.LibraryBook
import org.book2words.models.LibraryDictionary
import org.book2words.data.DataContext
import java.io.*
import java.util.TreeSet
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class LibraryService : IntentService(LibraryService::class.simpleName) {

    private val NOTIFICATION_ID = 100

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_SYNC == action) {

                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK)

                val builder = Notification.Builder(this)
                builder.setSmallIcon(R.drawable.ic_launcher)
                builder.setContentTitle("Book processing")
                builder.setProgress(100, 0, true)
                builder.setContentIntent(pendingIntent)

                startForeground(NOTIFICATION_ID, builder.build())

                val path = intent.getStringExtra(EXTRA_PATH)

                val libraryBook = LibraryBook()

                prepareBook(libraryBook, path)

                sendBroadcast(Intent(ACTION_PREPARED))
            } else if (ACTION_EXPORT == action) {
                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK)

                val builder = Notification.Builder(this)
                builder.setSmallIcon(R.drawable.ic_launcher)
                builder.setContentTitle("Dictionaries exporting")
                builder.setProgress(100, 0, true)
                builder.setContentIntent(pendingIntent)

                startForeground(NOTIFICATION_ID, builder.build())

                exportDictionaries()
            } else if (ACTION_IMPORT == action) {
                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK)

                val builder = Notification.Builder(this)
                builder.setSmallIcon(R.drawable.ic_launcher)
                builder.setContentTitle("Dictionaries importing")
                builder.setProgress(100, 0, true)
                builder.setContentIntent(pendingIntent)

                startForeground(NOTIFICATION_ID, builder.build())

                val path = intent.getStringExtra(EXTRA_PATH)

                importDictionaries(File(path))
            }
        }
    }

    private fun exportDictionaries() {
        val root = FileStorage.createDictionaryDirectory()
        val files = root.listFiles()
        if (!files.isEmpty()) {
            val stream = ZipOutputStream(FileOutputStream(FileStorage.createExportFile()))
            files.forEach {
                val name = it.getName()
                Logger.debug("export - ${name}")
                val entry = ZipEntry(name)
                stream.putNextEntry(entry)
                val fileStream = FileInputStream(it)
                val array = ByteArray(2048)
                var read = fileStream.read(array)
                while (read != -1) {
                    stream.write(array, 0, read)
                    read = fileStream.read(array)
                }
                fileStream.close()
                stream.closeEntry()
            }
            stream.close()
        }
    }

    private fun importDictionaries(file: File) {
        val stream = ZipInputStream(FileInputStream(file))
        var zipEntry = stream.getNextEntry()
        while (zipEntry != null) {
            val entryName = zipEntry.getName()
            Logger.debug("import - ${entryName}")

            val dictionaryFile = FileStorage.createDictionaryFile(entryName)

            val os = ByteArrayOutputStream()
            val array = ByteArray(2048)
            var read = stream.read(array)
            while (read != -1) {
                os.write(array, 0, read)
                read = stream.read(array)
            }
            stream.closeEntry()

            val result = os.toString()
            val lines = TreeSet(result.lines())

            if(dictionaryFile.exists()){
                val freader = FileInputStream(dictionaryFile).bufferedReader(Charsets.UTF_8)
                freader.forEachLine {
                    lines.add(it)
                }
                freader.close()
            }

            val writer = FileOutputStream(dictionaryFile).bufferedWriter(Charsets.UTF_8)
            lines.forEach {
                writer.appendln(it)
                writer.flush()
            }
            writer.close()

            sendBroadcast(Intent(LibraryDictionary.ACTION_MODIFIED))

            zipEntry = stream.getNextEntry()
        }
    }


    private fun prepareBook(libraryBook: LibraryBook, path: String) {
        Logger.debug("prepareBook() ${path}")
        try {
            val eBook = EpubReader().readEpubLazy(ZipFile(path), Charsets.UTF_8.name())

            libraryBook.setName(eBook.getTitle())
            val authorsString = StringBuilder()
            val authors = eBook.getMetadata().getAuthors()
            authors.forEachIndexed { i, author ->
                authorsString.append(author.getFirstname())
                authorsString.append(" ")
                authorsString.append(author.getLastname())
                if (i != authors.size - 1) {
                    authorsString.append(", ")
                }
            }
            val language = eBook.getMetadata().getLanguage()
            Logger.debug("prepareBook() ${authorsString}")

            libraryBook.setLanguage(language)
            libraryBook.setAuthors(authorsString.toString())
            libraryBook.setPath(path)

            val id = DataContext.getLibraryBookDao(this).insertOrReplace(libraryBook)

            Logger.debug("prepareBook() ${id}")
            val coverImage = eBook.getCoverImage()
            if (coverImage != null) {
                saveCover(id, coverImage)
            } else {
                FileStorage.deleteCover(id)
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

        val ACTION_PREPARED: String = "org.book2words.intent.action.PREPARED"

        val ACTION_CLEARED: String = "org.book2words.intent.action.CLEARED"

        private val ACTION_IMPORT = "org.book2words.intent.action.IMPORT"
        private val ACTION_EXPORT = "org.book2words.intent.action.EXPORT"
        private val ACTION_SYNC = "org.book2words.intent.action.SYNC"

        private val EXTRA_PATH = "_root"

        fun addBook(context: Context, path: File) {
            val intent = Intent(context, LibraryService::class.java)
            intent.action = ACTION_SYNC
            intent.putExtra(EXTRA_PATH, path.absolutePath)
            context.startService(intent)
        }

        fun export(context: Context) {
            val intent = Intent(context, LibraryService::class.java)
            intent.action = ACTION_EXPORT
            context.startService(intent)
        }

        fun import(context: Context, path: File) {
            val intent = Intent(context, LibraryService::class.java)
            intent.action = ACTION_IMPORT
            intent.putExtra(EXTRA_PATH, path.absolutePath)
            context.startService(intent)
        }
    }
}