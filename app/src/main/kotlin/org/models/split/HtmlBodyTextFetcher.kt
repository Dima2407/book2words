package org.models.split

import android.webkit.JavascriptInterface

public trait HtmlBodyTextFetcher {
    JavascriptInterface
    public fun processContent(aContent: String)
}