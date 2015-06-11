package org.book2words.models.book

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
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

    override fun compareTo(other: WordAdapted): Int {
        return start - other.start
    }

    override fun equals(other: Any?): Boolean {
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

    fun applySpannable(adapted: SpannableStringBuilder, offset: Int): Int {
        val start = start + 1 + offset
        val end = end + 1 + offset
        if (hasDefinition) {
            val trans = " [${getTranscription()}]"

            adapted.insert(end, trans)
            adapted.setSpan(foregroundSpan, start, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            adapted.setSpan(styleSpan, end, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            adapted.setSpan(sizeSpan, end, end + trans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return offset + trans.length()
        }
        return offset
    }

    fun removeSpannable(adapted: SpannableStringBuilder) {
        adapted.removeSpan(foregroundSpan)
        adapted.removeSpan(styleSpan)
        //adapted.removeSpan(sizeSpan)
    }
}