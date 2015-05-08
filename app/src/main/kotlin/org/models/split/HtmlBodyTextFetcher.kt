package org.models.split

import android.webkit.JavascriptInterface
import org.book2dictionary.Logger

public class HtmlBodyTextFetcher(private val onProcess: (text : String)-> Unit) {

    JavascriptInterface
    public fun processContent(text: String){
        onProcess(text)
    }
}