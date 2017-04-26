package org.book2words.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import org.book2words.core.FileStorage
import org.book2words.core.Logger
import org.book2words.data.DataContext
import org.book2words.database.models.LibraryBook
import org.book2words.models.LibraryDictionary
import org.book2words.models.book.ParagraphAdapted
import org.book2words.models.book.Word
import org.book2words.models.book.WordAdapted
import org.book2words.translate.Dictionary
import org.book2words.translate.EnglishDictionary
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
        book.currentPartition = currentPartition
        book.visibleParagraph = currentParagraph
        DataContext.getLibraryBookDao(service).save(book)
    }

    private val handler = Handler()

    private val executor = Executors.newSingleThreadExecutor()

    private var unknownWords: MutableList<Word> = ArrayList()
    private var currentPartition = 0;
    private var currentParagraph = 0;
    private var paragraphsCount = 0;

    fun prepare(callback: () -> Unit) {
        currentParagraph = book.visibleParagraph;
        currentPartition = book.currentPartition;
        executor.execute({
            DataContext.getWordsFoundDao(service).getAllFoundWordsInBook(book.id).forEach {
                unknownWords.add(it)
            }
            handler.post({
                callback()
            })
        })
    }

    fun read(callback: (paragraphs: List<ParagraphAdapted>) -> Unit) {

        executor.execute({

            Logger.debug("words = ${book.currentPartition} - ${unknownWords.size}")

            val partitions = DataContext.getPartsDao(service).getPartsInPartition(book.id!!, currentPartition, currentParagraph)
            paragraphsCount = partitions.size

            val words = DataContext.getWordsFoundDao(service).getWordsInBook(book.id!!, currentPartition, partitions.map { it.paragraphNumber });

            val pars = partitions.asSequence().map {
                val p = it
                val paragraph = ParagraphAdapted(p.text)

                words.forEach {
                    paragraph.modify(p.paragraphNumber, p.partitionNumber, it)
                }
                paragraph
            }.toList();
            handler.post({
                callback(pars)
            })
        })

    }

    fun translate(paragraph: ParagraphAdapted, onTranslated: () -> Unit) {
        executor.execute {
            val counter = CountDownLatch(paragraph.getNotTranslatedWords().size)
            paragraph.getNotTranslatedWords().forEach { word ->
                //Log.i("BookReaderBinder", "word : " + word.getValue())
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
        unknownWords.remove(word.word)
        DataContext.getWordsFoundDao(service).delete(word.word);

        val writer = FileOutputStream(FileStorage.createDictionaryFile(book), true)
                .bufferedWriter(Charsets.UTF_8)
        writer.appendln(word.getValue())
        writer.flush()
        writer.close()
        service.sendBroadcast(Intent(LibraryDictionary.ACTION_MODIFIED))
    }

    fun previousPartition(): Boolean {
        if (paragraphsCount > 10) {
            currentParagraph -= 10
            return true
        }
        currentPartition--
        if (currentPartition < 0) {
            currentPartition = 0
            return false
        }
        return true
    }

    fun nextPartition(): Boolean {
        if (paragraphsCount < 10) {
            currentPartition++
        } else {
            currentParagraph += paragraphsCount;
            return true
        }
        if (currentPartition <= book.countPartitions) {
            return true
        }
        return false
    }

    fun getBook(): LibraryBook {
        return book
    }
}
