package org.book2words.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.models.book.Word
import java.io.FileInputStream
import java.util.ArrayList
import java.util.regex.Pattern

public class BookReadService : Service() {
    private val binder = BookReaderBinder();
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    public class BookReaderBinder : Binder() {

        public fun take(book: LibraryBook, callback: (paragraphs: List<String>, words: List<Word>) -> Unit) {

            var file = FileStorage.createWordsFile(book.getId());
            val bos = FileInputStream(file).buffered().reader("UTF-8")
            val serializer = Gson()
            val words = serializer.fromJson<List<Word>>(bos,
                    object : TypeToken<List<Word>>() {}.getType())
            bos.close()

            val chapter = 7

            Logger.debug("words = ${chapter} - ${words.size()}")

            val ws = words.filter {
                it.keys.contains(chapter)
            }

            Logger.debug("words = ${chapter} - ${ws.size()}")

            file = FileStorage.createChapterFile(book.getId(), chapter)
            val stream = FileInputStream(file).reader("utf-8").buffered()
            val pars = ArrayList<String>()
            stream.forEachLine {

                var paragraph = it;
                ws.forEach {
                    val pattern = Pattern.compile("(${it.value})", Pattern.CASE_INSENSITIVE)
                    val matcher = pattern.matcher(paragraph)
                    if (matcher.find()) {
                        paragraph = matcher.replaceAll("<b><font color=red>$1</font></b>")
                    }
                }
                pars.add(paragraph)
            }
            callback(pars, ws)
        }
    }
}