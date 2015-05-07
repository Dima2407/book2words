package org.book2words.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import org.book2dictionary.Logger

import java.io.File
import java.io.FileFilter

public class LibraryService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.getAction();
            if (ACTION_SYNC == action) {
                val path = intent.getStringExtra(EXTRA_ROOT)
                syncUserBooks(File(path))
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun syncUserBooks(root: File) {
        Logger.debug("syncUserBooks() ${root}")
        val files = root.listFiles {
            !it.isHidden() && (it.isDirectory() || it.getName().endsWith(".epub"))
        }
        files!!.forEach {
            if (it.isDirectory()) {
                syncUserBooks(it)
            } else {
                BookSyncService.prepareBook(this, it.getAbsolutePath())
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
