package org.models.split

import android.webkit.WebView
import android.webkit.WebViewClient
import com.easydictionary.app.SplitActivity
import nl.siegmann.epublib.domain.Spine
import org.book2dictionary.Logger
import java.nio.charset.Charset


public class WebViewBookReader(private val surface: WebView,
                               private val spine: Spine,
                               private val offset: Int = 0,
                               private val length: Int = spine.size(),
                               private val encoding: String) : BookReader(spine, offset, length ,encoding) {
    init {
        val settings = surface.getSettings()
        settings!!.setJavaScriptEnabled(true)
        settings!!.setDefaultTextEncodingName(encoding)

        surface.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view!!.loadUrl(JAVASCRIPT_GET_BODY_INNER_TEXT)
            }
        })
    }

    override fun onChapter(chapter: Int, text: String) {
        Logger.debug("onChapter(${chapter}) - ${text}")
        surface.post {
            Logger.debug("loadData(${encoding}) - ${text}")
            surface.loadData(text, MIME_TYPE, encoding);
        }
    }

    override fun onFinished() {
        Logger.debug("onFinished()")
        surface.post {
            surface.removeJavascriptInterface(JAVASCRIPT_INTERFACE);
        };
    }

    public fun start(fetcher : HtmlBodyTextFetcher){
        surface.addJavascriptInterface(fetcher, JAVASCRIPT_INTERFACE);
        surface.reload();
        super.start()
    }

    companion object {
        private val JAVASCRIPT_INTERFACE = "INTERFACE"
        private val JAVASCRIPT_GET_BODY_INNER_TEXT = "javascript:window." + JAVASCRIPT_INTERFACE + ".processContent(document.getElementsByTagName('body')[0].innerText);"
        private val MIME_TYPE = "text/html; charset=UTF-8"
    }
}