package org.models.split

import android.content.Context
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.easydictionary.app.TextSplitService
import nl.siegmann.epublib.domain.Spine
import nl.siegmann.epublib.epub.EpubReader
import org.book2dictionary.Logger
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

public class BookSplitter(private val path: String, private val surface: WebView) {

    private val spine: Spine

    val title: String

    init {
        val inputStream = FileInputStream(path)

        var book = EpubReader().readEpub(inputStream, ENCODING)
        title = book.getTitle()
        spine = book.getSpine()
    }

    public fun split(offset: Int = 0, length: Int = spine.size()) {
        val reader = WebViewBookReader(surface, spine, offset, length, ENCODING)
        reader.start(object : HtmlBodyTextFetcher {
            override fun processContent(text: String) {
                Logger.debug("processContent() - ${text}")
                reader.next()
            }
        })
    }

    companion object {
        private val ENCODING = Charset.forName("utf-8").name()
    }
}