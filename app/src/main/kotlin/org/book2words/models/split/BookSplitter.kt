package org.book2words.models.split

import android.webkit.WebView
import nl.siegmann.epublib.epub.EpubReader
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import java.io.FileInputStream
import java.nio.charset.Charset

public class BookSplitter(private val libraryBook: LibraryBook) {

    private var reader: WebViewBookReader? = null

    private var size = 0

    public fun prepare(surface: WebView, onPrepared: (title: String, length: Int) -> Unit, onReleased: () -> Unit) {
        val inputStream = FileInputStream(libraryBook.getPath())

        var book = EpubReader().readEpub(inputStream, ENCODING)
        val title = book.getTitle()
        val spine = book.getSpine()
        size = spine.size()
        reader = WebViewBookReader(surface, spine, {
            onReleased()
        }, ENCODING)

        onPrepared(title, spine.size())
    }

    public fun split(offset: Int = 0, length: Int = size, onSpiltProgress: (current: Int, length: Int, text: String) -> Unit) {
        reader!!.start(offset, length, HtmlBodyTextFetcher({
            val current = reader!!.getCurrent()
            Logger.debug("process() : ${current}")
            onSpiltProgress(current, length, it)
            reader!!.next()

        }))
    }

    companion object {
        private val ENCODING = Charset.forName("utf-8").name()
    }
}