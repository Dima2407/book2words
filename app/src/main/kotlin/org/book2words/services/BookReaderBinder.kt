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
import org.book2words.translate.Dictionary
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

public class BookReaderBinder(
        private val book: LibraryBook,
        private val service: Service) : Binder(), BookBinder {
    val onlineDictionary: Dictionary
    val verbsDictionary: Dictionary

    init {
        val cacheDictionary = DictionaryContext.getConfigs(service)
        onlineDictionary = Dictionary.createOnline(cacheDictionary, book.getLanguage())
        verbsDictionary = Dictionary.createVerbs(service)
    }

    override fun release() {
        val file = FileStorage.createWordsFile(book.getId());
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

    public fun prepare(callback: () -> Unit) {
        executor.execute({

            dictionary = DataContext.getLibraryDictionaryDao(service)
                    .queryBuilder()
                    .where(LibraryDictionaryDao.Properties.Name.eq(book.getDictionaryName())).unique()

            var file = FileStorage.createWordsFile(book.getId())
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

    public fun read(callback: (paragraphs: List<ParagraphAdapted>) -> Unit) {

        executor.execute({

            Logger.debug("words = ${book.getCurrentPartition()} - ${unknownWords.size()}")

            val words = unknownWords.filter {
                it.paragraphs.any {
                    it.key == book.getCurrentPartition()
                }
            }

            Logger.debug("words = ${book.getCurrentPartition()} - ${words.size()}")

            val file = FileStorage.createChapterFile(book.getId(), book.getCurrentPartition())
            val stream = FileInputStream(file).bufferedReader(Charsets.UTF_8)
            val pars = ArrayList<ParagraphAdapted>()
            var index = 0
            stream.forEachLine {
                Logger.debug("line(${index})- ${it}")

                if (!it.trim().isEmpty()) {
                    val paragraph = ParagraphAdapted(it)

                    words.forEach {
                        Logger.debug("word(${index})- ${it}")
                        paragraph.modify(index, book.getCurrentPartition(), it)
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

    public fun translate(paragraph: ParagraphAdapted, onTranslated: () -> Unit) {
        executor.execute {
            val counter = CountDownLatch(paragraph.getWords().size())
            paragraph.getWords().forEach {
                val word = it
                if (!word.isTranslated()) {
                    verbsDictionary.find(word.getValue(), { input, result ->
                        if (result.isNotEmpty()) {
                            word.setDefinitions(result)
                            counter.countDown()
                        } else {
                            if (!word.isTranslated()) {
                                onlineDictionary.find(word.getValue(), { input, result ->
                                    word.setDefinitions(result)
                                    counter.countDown()
                                })
                            }
                        }
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
        unknownWords.remove(word)
        dictionary!!.setSize(dictionary!!.getSize() + 1)
        DataContext.getLibraryDictionaryDao(service).update(dictionary)
        val writer = FileOutputStream(FileStorage.createDictionaryFile(book), true)
                .bufferedWriter(Charsets.UTF_8)
        writer.appendln(word.getValue())
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
