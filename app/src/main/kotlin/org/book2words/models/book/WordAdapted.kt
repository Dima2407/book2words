package org.book2words.models.book

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import org.book2words.translate.core.Definition

class WordAdapted(private val start: Int,
                  private val end: Int,
                  val word: Word,
                  private val color: Int) : Comparable<WordAdapted> {

    private val foregroundSpan = ForegroundColorSpan(color)
    private val styleSpan = StyleSpan(Typeface.ITALIC)
    private val sizeSpan = RelativeSizeSpan(0.5f)
    private var clickSpan: ClickableSpan ? = null

    private var transcriptionStart = 0

    private var transcriptionEnd = 0

    override fun compareTo(other: WordAdapted): Int {
        return start - other.start
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is Word) {
            val o = other
            return word.equals(o)
        }
        if (other is WordAdapted) {
            val o = other
            return word.equals(o.word)
        }
        return false
    }

    fun setDefinitions(definitions: Array<out Definition>) {
        if (!word.translated) {
            word.definitions = definitions
        }
        word.translated = true
    }

    fun applySpannable(adapted: SpannableStringBuilder, offset: Int, onWordClickListener: ((word: WordAdapted) -> Unit)? = null): Int {
        val start = start + 1 + offset
        val end = end + 1 + offset
        if (word.hasDefinitions()) {
            val trans = " [${word.definitions?.get(0)?.getTranscription()}]"

            adapted.insert(end, trans)
            adapted.setSpan(foregroundSpan, start, end + trans.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (onWordClickListener != null) {
                clickSpan = WordClickSpan(this, onWordClickListener)
                adapted.setSpan(clickSpan, start, end + trans.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            adapted.setSpan(styleSpan, end, end + trans.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            adapted.setSpan(sizeSpan, end, end + trans.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            transcriptionStart = end
            transcriptionEnd = end + trans.length
            return offset + trans.length
        }
        return offset
    }

    fun removeSpannable(adapted: SpannableStringBuilder): IntRange {
        adapted.removeSpan(foregroundSpan)
        adapted.removeSpan(styleSpan)
        adapted.removeSpan(sizeSpan)
        adapted.removeSpan(clickSpan)
        adapted.replace(transcriptionStart, transcriptionEnd, "")
        return IntRange(transcriptionStart, transcriptionEnd)
    }

    private class WordClickSpan(private val wordAdapted: WordAdapted, private val function: (WordAdapted) -> Unit) : ClickableSpan() {
        override fun onClick(widget: View) {
            function(wordAdapted)
        }

        override fun updateDrawState(ds: TextPaint) {

        }
    }

    fun isTranslated(): Boolean {
        return word.translated
    }

    fun getDefinitionsFull(): String {
        val definitions = word.definitions
        val content = StringBuilder()
        if (definitions != null && definitions.isNotEmpty()) {
            definitions.forEachIndexed { i, it ->
                content.append(it.getText())
                content.append(" - ")
                content.append("<b>[ ")
                content.append(it.getTranscription())
                content.append(" ]</b>")
                content.append(" - ")
                content.append("(")
                content.append(it.getPos())
                content.append(")")
                content.append(" ")
                content.append("<i>")
                content.append(it.getTranslate())
                content.append("</i>")
                if (i < definitions.size - 1) {
                    content.append("<br/>")
                }
            }
        }
        return content.toString()
    }

    fun getDefinitionsShort(): String {
        val definitions = word.definitions
        val content = StringBuilder()
        if (definitions != null && definitions.isNotEmpty()) {
            definitions.forEachIndexed { i, it ->
                content.append("<i>")
                content.append(it.getTranslate())
                content.append("</i>")
                if (i < definitions.size - 1) {
                    content.append("<br/>")
                }
            }
        }
        return content.toString()
    }

    fun getValue(): String {
        return word.value
    }

    override fun toString(): String {
        return getValue()
    }

    fun getColor(): Int {
        return color
    }

    fun hasDefinitions(): Boolean {
        return word.hasDefinitions()
    }

    fun updateSpannable(offset: Int) {
        transcriptionStart -= offset
        transcriptionEnd -= offset
    }
}