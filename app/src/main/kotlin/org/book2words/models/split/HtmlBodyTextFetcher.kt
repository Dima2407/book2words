package org.book2words.models.split

import android.webkit.JavascriptInterface
import org.book2words.core.Logger

public class HtmlBodyTextFetcher(private val onProcess: (text : String)-> Unit) {

    JavascriptInterface
    public fun processContent(text: String){
        Logger.debug("processContent() - ${text}")
        onProcess(text)
    }
}