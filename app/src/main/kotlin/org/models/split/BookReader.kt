package org.models.split

import android.text.TextUtils
import nl.siegmann.epublib.domain.Spine
import org.book2dictionary.Logger
import java.io.IOException
import java.io.InputStreamReader

private abstract class BookReader(private val spine: Spine,
                          private val offset: Int = 0,
                          private val length: Int = spine.size(),
                          private val encoding: String) {
    private var current = offset

    abstract fun onChapter(chapter: Int, text: String);

    abstract fun onFinished();

    public fun getCurrent() : Int {
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

    protected fun start() {
        onChapter(current + 1, getText())
    }

    private fun getText(): String {
        Logger.debug("getText() ${current}")
        val resource = spine.getResource(current)
        val string = StringBuilder()
        try {
            val stream = resource.getInputStream().buffered()
            try {
                val reader = InputStreamReader(stream, encoding);
                reader.forEachLine {
                    string.append(it)
                }

            } catch (e: IOException) {
                Logger.error(e)
            } finally {
                stream.close()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return string.toString()
    }
}