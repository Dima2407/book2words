package org.book2words.models.book

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import java.io.Serializable
import java.util.TreeSet
import java.util.regex.Pattern

public class ParagraphAdapted(val original: String) : Serializable {

    private var adapted = SpannableStringBuilder("\t${original}")

    private val words: MutableSet<WordAdapted> = TreeSet()

    private val colors = Array(COLORS, { index ->
        val step = COLORS / 3
        val i = index % step
        if (index < step) {
            Color.rgb(255 - (i * 15), 0, 0)
        } else if (index <= step * 2) {
            Color.rgb(0, 255 - (i * 15), 0)
        } else {
            Color.rgb(0, 0, 255 - (i * 15))
        }
    })

    public fun getAdapted(): Spannable {
        return adapted
    }

    public fun modify(word: String) {
        val pattern = Pattern.compile("(${word})", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(adapted)
        val color = colors[words.size()]
        while (matcher.find()) {
            val start = matcher.start(1)
            val end = matcher.end(1)
            adapted.setSpan(BackgroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            words.add(WordAdapted(start, word, color))
        }
    }

    fun getWords(): MutableSet<WordAdapted> {
        return words
    }

    companion object {
        private val COLORS = 51
    }
}