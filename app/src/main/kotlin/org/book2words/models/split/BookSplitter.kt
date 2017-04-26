package org.book2words.models.split

import nl.siegmann.epublib.epub.EpubReader
import org.book2words.core.Logger
import org.book2words.database.models.LibraryBook
import java.io.FileInputStream

public class BookSplitter(private val libraryBook: LibraryBook) {

    private var reader: BookReader? = null

    private var size = 0

    fun prepare(onPrepared: (title: String, length: Int) -> Unit, onReleased: () -> Unit) {
        val inputStream = FileInputStream(libraryBook.path)

        var book = EpubReader().readEpub(inputStream, ENCODING)
        val title = book.title
        val spine = book.spine
        size = spine.size()
        reader = TagBookReader(spine, {
            onReleased()
        }, ENCODING)

        onPrepared(title, spine.size())
    }

    fun split(offset: Int = 0, length: Int = size, onSpiltProgress: (current: Int, length: Int, text: String) -> Unit) {
        var index = 0
        reader!!.start(offset, length, HtmlTagContentFetcher({
            if(!it.isBlank()){
                val current = index++
                Logger.debug("process() : $it")
                onSpiltProgress(current, length, it)
            }
            reader!!.next()

        }))
    }

    companion object {
        private val ENCODING = Charsets.UTF_8.name()
    }
}