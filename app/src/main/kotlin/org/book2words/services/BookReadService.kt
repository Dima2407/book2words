package org.book2words.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.Word
import org.book2words.models.book.WordAdapted
import org.book2words.translate.TranslateProvider
import org.book2words.translate.TranslateProviderFactory
import java.io.FileInputStream
import java.util.ArrayList
import java.util.concurrent.Executors

public class BookReadService : Service() {
    private val binder = BookReaderBinder();
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    public class BookReaderBinder : Binder() {

        private val handler = Handler()

        private val executor = Executors.newSingleThreadExecutor()

        public fun take(book: LibraryBook, callback: (paragraphs: List<ParagraphAdapted>, words: List<Word>) -> Unit) {

            executor.execute({
                var file = FileStorage.createWordsFile(book.getId());
                val bos = FileInputStream(file).buffered().reader("UTF-8")
                val serializer = Gson()
                val words = serializer.fromJson<List<Word>>(bos,
                        object : TypeToken<List<Word>>() {}.getType())
                bos.close()

                val chapter = 7

                Logger.debug("words = ${chapter} - ${words.size()}")

                val ws = words.filter {
                    it.paragraphs.contains(chapter)
                }

                Logger.debug("words = ${chapter} - ${ws.size()}")

                file = FileStorage.createChapterFile(book.getId(), chapter, 0)
                val stream = FileInputStream(file).reader("utf-8").buffered()
                val pars = ArrayList<ParagraphAdapted>()
                stream.forEachLine {

                    if (it.trim().length() != 0) {
                        val paragraph = ParagraphAdapted(it)
                        ws.forEach {
                            paragraph.modify(it.value)
                        }
                        pars.add(paragraph)
                    }
                }
                handler.post({
                    callback(pars, ws)
                })
            })

        }

        public fun translate(word: String) {
            val translateProvider = TranslateProviderFactory.create(TranslateProvider.Provider.YANDEX, "en", "ru")
            translateProvider.translate(word, { input, result ->

            })
        }

        public fun add(word: String) {

        }

        public fun remove(word: WordAdapted) {

        }
    }
}