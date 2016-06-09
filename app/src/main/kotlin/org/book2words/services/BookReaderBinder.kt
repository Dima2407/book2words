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
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.Word
import org.book2words.models.book.WordAdapted
import org.book2words.translate.Dictionary
import org.book2words.translate.EnglishDictionary
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class BookReaderBinder(
        private val book: LibraryBook,
        private val service: Service) : Binder(), BookBinder {
    val wordsDictionary: Dictionary

    init {
        wordsDictionary = EnglishDictionary(service)
    }

    override fun release() {
        val file = FileStorage.createWordsFile(book.id)
        val bos = FileOutputStream(file).bufferedWriter(Charsets.UTF_8)

        unknownWords.forEach {
            bos.appendln(it.toSeparatedString(";"))
            bos.flush()
        }

        bos.close()
    }

    private val handler = Handler()

    private val executor = Executors.newSingleThreadExecutor()

    private var unknownWords: MutableList<Word> = ArrayList()
    private var dictionary: LibraryDictionary? = null

    fun prepare(callback: () -> Unit) {
        executor.execute({

            dictionary = DataContext.getLibraryDictionaryDao(service)
                    .queryBuilder()
                    .where(LibraryDictionaryDao.Properties.Name.eq(book.getDictionaryName())).unique()

            var file = FileStorage.createWordsFile(book.id)
            val bos = FileInputStream(file).bufferedReader(Charsets.UTF_8)
            bos.forEachLine {
                unknownWords.add(Word.fromSeparatedString(it, ";"))
            }
            bos.close()

            handler.post({
                callback()
            })
        })
    }

    fun read(callback: (paragraphs: List<ParagraphAdapted>) -> Unit) {

        executor.execute({

            Logger.debug("words = ${book.currentPartition} - ${unknownWords.size}")

            val words = unknownWords.filter {
                it.paragraphs.any {
                    it.key == book.currentPartition
                }
            }

            Logger.debug("words = ${book.currentPartition} - ${words.size}")

            val file = FileStorage.createChapterFile(book.id, book.currentPartition)
            val stream = FileInputStream(file).bufferedReader(Charsets.UTF_8)
            val pars = ArrayList<ParagraphAdapted>()
            var index = 0
            stream.forEachLine {
                Logger.debug("line($index)- $it")

                if (!it.trim().isEmpty()) {
                    val paragraph = ParagraphAdapted(it)

                    words.forEach {
                        Logger.debug("word($index)- $it")
                        paragraph.modify(index, book.currentPartition, it)
                    }

                    pars.add(paragraph)
                }
                index++
            }
            handler.post({
                callback(pars)
            })
        })

    }

    fun translate(paragraph: ParagraphAdapted, onTranslated: () -> Unit) {
        executor.execute {
            val counter = CountDownLatch(paragraph.getNotTranslatedWords().size)
            paragraph.getNotTranslatedWords().forEach { word ->
                wordsDictionary.find(word.getValue(), { input, result ->
                    word.setDefinitions(result)
                    counter.countDown()
                })
            }
            counter.await()
            handler.post({
                onTranslated()
            })
        }

    }

    fun add(word: WordAdapted) {

    }

    fun remove(word: WordAdapted) {
        unknownWords.remove(word as Word)
        dictionary?.size = dictionary!!.size + 1
        DataContext.getLibraryDictionaryDao(service).update(dictionary)
        val writer = FileOutputStream(FileStorage.createDictionaryFile(book), true)
                .bufferedWriter(Charsets.UTF_8)
        writer.appendln(word.getValue())
        writer.flush()
        writer.close()
        service.sendBroadcast(Intent(LibraryDictionary.ACTION_UPDATED))
    }

    fun previousPartition(): Boolean {
        val index = book.currentPartition - 1
        if (index >= 1) {
            book.currentPartition = index
            DataContext.getLibraryBookDao(service).update(book)
            return true
        }
        return false
    }

    fun nextPartition(): Boolean {
        val index = book.currentPartition + 1
        if (index <= book.countPartitions) {
            book.currentPartition = index
            DataContext.getLibraryBookDao(service).update(book)
            return true
        }
        return false
    }

    fun getBook(): LibraryBook {
        return book
    }
}
