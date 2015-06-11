package org.book2words.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.dao.LibraryDictionary
import org.book2words.dao.LibraryDictionaryDao
import org.book2words.data.DataContext
import org.book2words.data.DictionaryContext
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.Word
import org.book2words.models.book.WordAdapted
import org.book2words.translate.TranslateProvider
import org.book2words.translate.TranslateProviderFactory
import org.book2words.translate.core.DictionaryResult
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

public class BookReaderBinder(
        private val book: LibraryBook,
        private val service: Service) : Binder(), BookBinder {
    override fun release() {
        val file = FileStorage.createWordsFile(book.getId());
        val bos = FileOutputStream(file).bufferedWriter(Charsets.UTF_8)

        words.forEach {
            bos.appendln(it.toSeparatedString(";"))
            bos.flush()
        }

        bos.close()
    }

    private val handler = Handler()

    private val executor = Executors.newSingleThreadExecutor()

    private var words: MutableList<Word> = ArrayList()
    private var dictionary: LibraryDictionary? = null

    public fun prepare(callback: () -> Unit) {
        executor.execute({

            dictionary = DataContext.getLibraryDictionaryDao(service)
                    .queryBuilder()
                    .where(LibraryDictionaryDao.Properties.Name.eq(book.getDictionaryName())).unique()

            var file = FileStorage.createWordsFile(book.getId())
            val bos = FileInputStream(file).bufferedReader(Charsets.UTF_8)
            bos.forEachLine {
                words.add(Word.fromSeparatedString(it, ";"))
            }
            bos.close()

            handler.post({
                callback()
            })
        })
    }

    public fun read(callback: (paragraphs: List<ParagraphAdapted>, words: List<Word>) -> Unit) {

        executor.execute({

            Logger.debug("words = ${book.getCurrentPartition()} - ${words!!.size()}")

            val ws = words!!.filter {
                it.paragraphs.any {
                    it.key == book.getCurrentPartition()
                }
            }

            Logger.debug("words = ${book.getCurrentPartition()} - ${ws.size()}")

            val file = FileStorage.createChapterFile(book.getId(), book.getCurrentPartition())
            val stream = FileInputStream(file).bufferedReader(Charsets.UTF_8)
            val pars = ArrayList<ParagraphAdapted>()
            var index = 0
            stream.forEachLine {
                Logger.debug("line(${index})- ${it}")
                val paragraph = ParagraphAdapted(it)

                ws.forEach {
                    Logger.debug("word(${index})- ${it}")
                    paragraph.modify(index, book.getCurrentPartition(), it)
                }

                pars.add(paragraph)
                index++
            }
            handler.post({
                callback(pars, ws)
            })
        })

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

    public fun translate(paragraph: ParagraphAdapted, onTranslated: () -> Unit) {
        val cacheDictionary = DictionaryContext.getConfigs(service)
        val translateProvider = TranslateProviderFactory.create(TranslateProvider.Provider.YANDEX, cacheDictionary, "en", "ru")
        executor.execute {
            val counter = CountDownLatch(paragraph.getWords().size())
            paragraph.getWords().forEach {
                if (!it.translated) {
                    translateProvider.translate(it.word, { input, result ->
                        it.setDefinitions(result!!.results())
                        counter.countDown()
                    })
                } else {
                    counter.countDown()
                }
            }
            counter.await()
            handler.post({
                onTranslated()
            })
        }

    }

    public fun add(word: WordAdapted) {

    }

    public fun remove(word: WordAdapted) {
        words.remove(word)
        dictionary!!.setSize(dictionary!!.getSize() + 1)
        DataContext.getLibraryDictionaryDao(service).update(dictionary)
        val writer = FileOutputStream(FileStorage.createDictionaryFile(book), true)
                .bufferedWriter(Charsets.UTF_8)
        writer.appendln(word.word)
        writer.flush()
        writer.close()
        service.sendBroadcast(Intent(LibraryDictionary.ACTION_UPDATED))
    }

    public fun previousPartition(): Boolean {
        val index = book.getCurrentPartition() - 1
        if (index >= 1) {
            book.setCurrentPartition(index)
            DataContext.getLibraryBookDao(service).update(book)
            return true
        }
        return false
    }

    public fun nextPartition(): Boolean {
        val index = book.getCurrentPartition() + 1
        if (index <= book.getCountPartitions()) {
            book.setCurrentPartition(index)
            DataContext.getLibraryBookDao(service).update(book)
            return true
        }
        return false
    }

    public fun getBook(): LibraryBook {
        return book
    }
}
