package org.book2words.models.split

import nl.siegmann.epublib.epub.EpubReader
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import java.io.FileInputStream

public class BookSplitter(private val libraryBook: LibraryBook) {

    private var reader: BookReader? = null

    private var size = 0

    public fun prepare(onPrepared: (title: String, length: Int) -> Unit, onReleased: () -> Unit) {
        val inputStream = FileInputStream(libraryBook.getPath())

        var book = EpubReader().readEpub(inputStream, ENCODING)
        val title = book.getTitle()
        val spine = book.getSpine()
        size = spine.size()
        reader = TagBookReader(spine, {
            onReleased()
        }, ENCODING)

        onPrepared(title, spine.size())
    }

    public fun split(offset: Int = 0, length: Int = size, onSpiltProgress: (current: Int, length: Int, text: String) -> Unit) {
        reader!!.start(offset, length, HtmlTagContentFetcher({
            val current = reader!!.getCurrent()
            Logger.debug("process() : ${it}")
            onSpiltProgress(current, length, it)
            reader!!.next()

        }))
    }

    companion object {
        private val ENCODING = Charsets.UTF_8.name()
    }
}