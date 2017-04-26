package org.book2words.models.split

import nl.siegmann.epublib.domain.Spine
import org.book2words.core.Logger

public class TagBookReader(private val spine: Spine,
                               private val onRelease: ()-> Unit,
                               private val encoding: String) : BookReader(spine ,encoding) {
    private var fetcher: BodyTextFetcher? = null

    override fun start(offset: Int, length: Int, fetcher: BodyTextFetcher?) {
        this.fetcher = fetcher
        super.start(offset, length)
    }

    override fun onChapter(chapter: Int, text: String) {
        Logger.debug("onChapter($chapter)")
        Logger.debug("loadData($text)")
        fetcher?.processContent(text)
    }

    override fun onFinished() {
        Logger.debug("onFinished()")
        onRelease()
    }
}