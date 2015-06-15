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
import org.book2words.core.Logger
import org.book2words.translate.core.Definition

public class WordAdapted(val start: Int,
                         val end: Int,
                         val word: String,
                         val color: Int,
                         var translated: Boolean = false,
                         var hasDefinition: Boolean = false) : Comparable<WordAdapted> {

    private var definitions: Array<out Definition>? = null

    private val foregroundSpan = ForegroundColorSpan(color)
    private val styleSpan = StyleSpan(Typeface.ITALIC)
    private val sizeSpan = RelativeSizeSpan(0.5f)
    private var clickSpan: ClickableSpan ? = null

    private var transcriptionStart = 0

    private var transcriptionEnd = 0;

    override fun compareTo(other: WordAdapted): Int {
        Logger.debug("compareTo " + word)
        return start - other.start
    }

    override fun equals(other: Any?): Boolean {
        Logger.debug("equals " + word)
        if (other == null) {
            return false
        }
        if (other is Word) {
            val o = other
            return word.equals(o.value, ignoreCase = true)
        }
        if (other is WordAdapted) {
            val o = other
            return word.equals(o.word, ignoreCase = true)
        }
        return false
    }

    public fun setDefinitions(defs: Array<out Definition>?) {
        if (!translated) {
            definitions = defs
        }
        hasDefinition = defs != null && defs.isNotEmpty()
        translated = true
    }

    fun getDefinitions(): Array<out Definition>? {
        return definitions
    }

    fun getTranscription(): String? {
        return definitions?.get(0)?.getTranscription()
    }

    fun applySpannable(adapted: SpannableStringBuilder, offset: Int, onWordClickListener: ((word: WordAdapted) -> Unit)? = null): Int {
        val start = start + 1 + offset
        val end = end + 1 + offset
        if (hasDefinition) {
            val trans = " [${getTranscription()}]"

            adapted.insert(end, trans)
            adapted.setSpan(foregroundSpan, start, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (onWordClickListener != null) {
                clickSpan = WordClickSpan(this, onWordClickListener)
                adapted.setSpan(clickSpan, start, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            adapted.setSpan(styleSpan, end, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            adapted.setSpan(sizeSpan, end, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            transcriptionStart = end
            transcriptionEnd = end + trans.length()
            return offset + trans.length()
        }
        return offset
    }

    fun removeSpannable(adapted: SpannableStringBuilder) {
        adapted.removeSpan(foregroundSpan)
        adapted.removeSpan(styleSpan)
        adapted.removeSpan(sizeSpan)
        adapted.removeSpan(clickSpan)
        adapted.replace(transcriptionStart, transcriptionEnd, "")
    }

    private class WordClickSpan(private val wordAdapted: WordAdapted, private val function: (WordAdapted) -> Unit) : ClickableSpan() {
        override fun onClick(widget: View) {
            function(wordAdapted)
        }

        override fun updateDrawState(ds: TextPaint) {

        }
    }
}