package org.book2words.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.data.DictionaryContext
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.Word
import org.book2words.models.book.WordAdapted
import org.book2words.models.split.BookSplitter
import org.book2words.translate.TranslateProvider
import org.book2words.translate.TranslateProviderFactory
import org.book2words.translate.core.DictionaryResult
import java.io.FileInputStream
import java.util.ArrayList
import java.util.concurrent.Executors

public class BookReadService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return BookReaderBinder(this)
    }

    public class BookReaderBinder(private val service: Service) : Binder() {

        private val handler = Handler()

        private val executor = Executors.newSingleThreadExecutor()

        private var words: MutableList<Word>? = null

        public fun prepare(book: LibraryBook, callback: () -> Unit) {
            executor.execute({
                var file = FileStorage.createWordsFile(book.getId());
                val bos = FileInputStream(file).buffered().reader("UTF-8")
                val serializer = Gson()
                words = serializer.fromJson<MutableList<Word>>(bos,
                        object : TypeToken<MutableList<Word>>() {}.getType())
                bos.close()

                handler.post({
                    callback()
                })
            })
        }

        public fun take(bookId: Long, partition: Int, callback: (paragraphs: List<ParagraphAdapted>, words: List<Word>) -> Unit) {

            executor.execute({

                Logger.debug("words = ${partition} - ${words!!.size()}")

                val ws = words!!.filter {
                    it.paragraphs.any {
                        it.key == partition
                    }
                }

                Logger.debug("words = ${partition} - ${ws.size()}")

                val file = FileStorage.createChapterFile(bookId, partition)
                val stream = FileInputStream(file).reader("utf-8").buffered()
                val pars = ArrayList<ParagraphAdapted>()
                var index = 0
                stream.forEachLine {
                    Logger.debug("line(${index})- ${it}")
                    val paragraph = ParagraphAdapted(it)

                    ws.forEach {
                        Logger.debug("word(${index})- ${it}")
                        paragraph.modify(index, partition, it)
                    }

                    pars.add(paragraph)
                    index++
                }
                handler.post({
                    callback(pars, ws)
                })
            })

        }

        public fun split(libraryBook: LibraryBook,
                         surface: WebView,
                         onPrepared: (title: String, length: Int) -> Unit,
                         onProgress: (progress: Int, limit: Int) -> Unit,
                         onReleased: () -> Unit) {
            val context = surface.getContext()
            val splitter = BookSplitter(libraryBook);
            executor.execute {
                splitter.prepare(surface, onPrepared = { t, i ->
                    handler.post({
                        onPrepared(t, i)
                    })
                    BookSplitService.openBook(context, libraryBook)
                }, onReleased = {
                    handler.post({
                        onReleased()
                    })
                    BookSplitService.closeBook(context, libraryBook)
                })
                splitter.split(onSpiltProgress = {
                    index, length, text ->
                    handler.post({
                        onProgress(index, length)
                    })
                    BookSplitService.save(context, libraryBook.getId(), index, text)
                })
            }
        }

        public fun translate(word: String, onTranslated: (input: String, result: DictionaryResult?) -> Unit) {
            val cacheDictionary = DictionaryContext.getConfigs(service)
            val translateProvider = TranslateProviderFactory.create(TranslateProvider.Provider.YANDEX, cacheDictionary, "en", "ru")
            translateProvider.translate(word, { input, result ->
                handler.post({
                    onTranslated(input, result)
                })
            })
        }

        public fun addToDictionary(word: WordAdapted) {

        }

        public fun remove(word: WordAdapted) {
            words!!.remove(word)
        }
    }
}