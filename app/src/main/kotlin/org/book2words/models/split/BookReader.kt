package org.book2words.models.split

import android.text.TextUtils
import nl.siegmann.epublib.domain.Spine
import org.book2words.core.Logger

private abstract class BookReader(private val spine: Spine,
                                  private val encoding: String) {
    private var current = 0

    private var offset = 0

    private var length = 0

    abstract fun onChapter(chapter: Int, text: String)

    abstract fun onFinished()

    public fun getCurrent(): Int {
        return current + 1
    }

    fun next() {
        while (current++ < length) {
            if (current < length) {
                val text = getText()
                if (!TextUtils.isEmpty(text.trim())) {
                    onChapter(current + 1, text)
                    break
                }
            } else {
                current = length - 1
                onFinished()
                break
            }
        }
    }

    fun previous() {
        while (current-- >= offset) {
            if (current >= offset) {
                val text = getText()
                if (!TextUtils.isEmpty(text.trim())) {
                    onChapter(current + 1, text)
                    break
                }
            } else {
                current = offset
                onFinished()
                break
            }
        }
    }

    protected fun start(offset: Int = 0, length: Int = spine.size()) {
        this.current = offset
        this.offset = offset
        this.length = length
        onChapter(current + 1, getText())
    }

    public abstract fun start(offset: Int = 0, length: Int = spine.size(), fetcher : BodyTextFetcher? = null)

    private fun getText(): String {
        Logger.debug("getText() ${current}")
        val resource = spine.getResource(current)
        val string = StringBuilder()
        try {
            val stream = resource.getInputStream()
            try {
                val reader = stream.bufferedReader(encoding)
                reader.forEachLine {
                    string.append(it)
                }
            } catch (e: Exception) {
                Logger.error(e)
            } finally {
                stream.close()
            }

        } catch (e: Exception) {
            Logger.error(e)
        }finally{
            return string.toString()
        }
    }
}