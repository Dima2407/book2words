package org.book2words.models.split

import android.content.Context
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.book2words.services.TextSplitService
import nl.siegmann.epublib.domain.Spine
import nl.siegmann.epublib.epub.EpubReader
import org.book2words.core.Logger
import org.book2words.dao.LibraryBook
import org.book2words.services.BookSplitService
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

public class BookSplitter(private val libraryBook: LibraryBook, private val surface: WebView) {

    private val spine: Spine

    val title: String

    val reader: WebViewBookReader

    init {
        val inputStream = FileInputStream(libraryBook.getPath())

        var book = EpubReader().readEpub(inputStream, ENCODING)
        title = book.getTitle()
        spine = book.getSpine()
        reader = WebViewBookReader(surface, spine, {
            BookSplitService.closeBook(surface.getContext(), libraryBook)
        }, ENCODING)
        BookSplitService.openBook(surface.getContext(), libraryBook)
    }

    public fun split(offset: Int = 0, length: Int = spine.size()) {
        reader.start(offset, length, HtmlBodyTextFetcher({
            val current = reader.getCurrent()
            Logger.debug("process() : ${current} - ${it}")
            BookSplitService.save(surface.getContext(), libraryBook.getId(), current, it)
            reader.next()

        }))
    }

    companion object {
        private val ENCODING = Charset.forName("utf-8").name()
    }
}