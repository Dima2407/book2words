package org.book2words.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.easydictionary.app.R
import org.book2dictionary.Logger
import org.book2words.Storage

import java.io.File
import java.io.FileFilter
import java.util.TreeSet

public class LibraryService : Service() {

    private val books = TreeSet<String>();

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.getAction();
            if (ACTION_SYNC == action) {
                val path = intent.getStringExtra(EXTRA_ROOT)
                findUserBooks(File(path))
                syncUserBooks()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun syncUserBooks() {
        Logger.debug("syncUserBooks()")
        BookSyncService.clear(this)

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val maxBooks = books.size();
        val builder = Notification.Builder(this)
        builder.setSmallIcon(R.drawable.ic_launcher)
        builder.setContentTitle("Books Processing")
        builder.setProgress(maxBooks, 0, false);

        mNotifyMgr.notify(100, builder.build())

        books.size()

        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val path = intent.getStringExtra(BookSyncService.EXTRA_PATH)
                if(books.remove(path)){
                    Logger.debug("syncCompleted() ${path}")
                    val progress = maxBooks - books.size()
                    Logger.debug("syncCompleted() ${progress}")
                    builder.setProgress(maxBooks, progress, false)
                    mNotifyMgr.notify(100, builder.build())
                }
                if (books.isEmpty()) {
                    Logger.debug("syncCompleted()")
                    mNotifyMgr.cancel(100)
                }
            }
        }, IntentFilter(BookSyncService.ACTION_PREPARED))

        books.forEach {
            BookSyncService.prepareBook(this, it)
        }
    }

    private fun findUserBooks(root: File) {
        Logger.debug("findUserBooks() ${root}")
        val files = root.listFiles {
            !it.isHidden() && (it.isDirectory() || it.getName().endsWith(".epub"))
        }

        files!!.forEach {
            if (it.isDirectory()) {
                findUserBooks(it)
            } else {
                books.add(it.getAbsolutePath())
            }
        }

    }

    companion object {

        private val ACTION_SYNC = "org.book2words.intent.action.SYNC"

        private val EXTRA_ROOT = "_root"

        public fun syncBooks(context: Context, path: String) {
            val intent = Intent(context, javaClass<LibraryService>())
            intent.setAction(ACTION_SYNC)
            intent.putExtra(EXTRA_ROOT, path)
            context.startService(intent)
        }
    }
}
