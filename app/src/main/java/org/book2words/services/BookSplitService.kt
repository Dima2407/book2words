package org.book2words.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import org.book2words.Storage
import java.io.FileOutputStream

public class BookSplitService : IntentService(javaClass<BookSplitService>().getSimpleName()) {
    override fun onHandleIntent(intent: Intent) {
        if(intent != null){
            val id = intent.getLongExtra(EXTRA_ID, 0)
            val index = intent.getIntExtra(EXTRA_INDEX, 0)
            val text = intent.getStringExtra(EXTRA_TEXT)
            saveText(id, index, text)
        }
    }

    private fun saveText(id: Long, index: Int, text: String) {
        val file = Storage.createChapterFile(id, index);
        val bos = FileOutputStream(file).writer(Charsets.UTF_8)
        bos.write(text)
        bos.flush()
        bos.close()
    }

    companion object {
        private val EXTRA_ID: String = "_id"
        private val EXTRA_INDEX: String = "_index"
        private val EXTRA_TEXT: String = "_text"

        public fun save(context : Context, id: Long, index: Int, text: String){
            val  intent = Intent(context, javaClass<BookSplitService>());
            intent.putExtra(EXTRA_ID, id);
            intent.putExtra(EXTRA_INDEX, index)
            intent.putExtra(EXTRA_TEXT, text)
            context.startService(intent)
        }
    }
}
