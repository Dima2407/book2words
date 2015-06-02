package org.book2words.models.book

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import java.io.Serializable
import java.util.TreeSet

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

    public fun modify(pIndex: Int, chapterId: Int, word: Word) {
        val color = colors[words.size()]
        val words = word.paragraphs.filter {
            pIndex == it.index && chapterId == it.key
        }
        words.forEach {
            val start = it.start
            val end = it.end
            adapted.setSpan(BackgroundColorSpan(color), start + 1, end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.words.add(WordAdapted(start, end, word.value, color))
        }
    }

    public fun removeWord(word: WordAdapted) {
        val removed = words.remove(word)
        if (removed) {
            adapted.clearSpans()
            words.forEach {
                val start = it.start
                val end = it.end
                adapted.setSpan(BackgroundColorSpan(it.color), start + 1, end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun getWords(): MutableSet<WordAdapted> {
        return words
    }

    companion object {
        private val COLORS = 51
    }
}