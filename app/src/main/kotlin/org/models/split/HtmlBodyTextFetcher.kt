package org.models.split

import android.webkit.JavascriptInterface
import org.book2dictionary.Logger

public class HtmlBodyTextFetcher(private val reader: BookReader) {
    JavascriptInterface
    public fun processContent(text: String){
        Logger.debug("processContent() - ${text}")
        reader.next()
    }
}