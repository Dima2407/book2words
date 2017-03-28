package org.book2words.models.split

import android.webkit.WebView
import android.webkit.WebViewClient
import nl.siegmann.epublib.domain.Spine
import org.book2words.core.Logger


public class WebViewBookReader(private val surface: WebView,
                               private val spine: Spine,
                               private val onRelease: ()-> Unit,
                               private val encoding: String) : BookReader(spine ,encoding) {
    init {
        surface.post {
            val settings = surface.getSettings()
            settings!!.setJavaScriptEnabled(true)
            settings.setDefaultTextEncodingName(encoding)
            surface.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view!!.loadUrl(JAVASCRIPT_GET_BODY_INNER_TEXT)
                }
            })
        }

    }

    override fun onChapter(chapter: Int, text: String) {
        Logger.debug("onChapter(${chapter})")
        surface.post {
            Logger.debug("loadData(${text})")
            surface.loadData(text, MIME_TYPE, encoding)
        }
    }

    override fun onFinished() {
        Logger.debug("onFinished()")
        surface.post {
            surface.removeJavascriptInterface(JAVASCRIPT_INTERFACE)
            onRelease()
        }
    }

    override fun start(offset: Int, length: Int, fetcher: BodyTextFetcher?) {
        surface.post {
            surface.addJavascriptInterface(fetcher, JAVASCRIPT_INTERFACE)
            surface.reload()
            super.start(offset, length)
        }
    }

    companion object {
        private val JAVASCRIPT_INTERFACE = "INTERFACE"
        private val JAVASCRIPT_GET_BODY_INNER_TEXT = "javascript:window." + JAVASCRIPT_INTERFACE + ".processContent(document.getElementsByTagName('body')[0].innerText);"
        private val MIME_TYPE = "text/html; charset=UTF-8"
    }
}