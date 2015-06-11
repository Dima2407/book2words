package org.book2words.models.book

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import org.book2words.core.Logger
import java.io.Serializable
import java.util.TreeSet

public class ParagraphAdapted(val original: String, var ready: Boolean = false) : Serializable {

    private var adapted = SpannableStringBuilder("\t${original}")

    private val words: MutableSet<WordAdapted> = TreeSet()

    private var onWordClickListener: ((word: WordAdapted?) -> Unit)? = null

    private val colors = arrayOf(
            "#E57373",
            "#F06292",
            "#BA68C8",
            "#9575CD",
            "#7986CB",
            "#64B5F6",
            "#4FC3F7",
            "#4DD0E1",
            "#4DB6AC",
            "#81C784",
            "#AED581",
            "#DCE775",
            "#FFF176",
            "#FFD54F",
            "#FFB74D",
            "#FF8A65",
            "#FFCDD2",
            "#F8BBD0",
            "#E1BEE7",
            "#D1C4E9",
            "#C5CAE9",
            "#BBDEFB",
            "#B3E5FC",
            "#B2EBF2",
            "#B2DFDB",
            "#C8E6C9",
            "#DCEDC8",
            "#F0F4C3",
            "#FFF9C4",
            "#FFECB3",
            "#FFE0B2",
            "#FFCCBC",
            "#EF9A9A",
            "#F48FB1",
            "#CE93D8",
            "#B39DDB",
            "#9FA8DA",
            "#90CAF9",
            "#81D4FA",
            "#80DEEA",
            "#80CBC4",
            "#A5D6A7",
            "#C5E1A5",
            "#E6EE9C",
            "#FFF59D",
            "#FFE082",
            "#FFCC80",
            "#FFAB91")

    public fun getAdapted(): Spannable {
        if (!ready) {
            prepareTextWithWords()
        }
        return adapted
    }

    public fun modify(pIndex: Int, chapterId: Int, word: Word) {
        val color = Color.parseColor(colors[words.size()])
        Logger.debug("color " + color)
        val words = word.paragraphs.filter {
            pIndex == it.index && chapterId == it.key
        }
        words.forEach {
            val start = it.start
            val end = it.end
            this.words.add(WordAdapted(start, end, word.value, color))
        }
    }

    public fun prepareTextWithWords() {
        var offset = 0;
        adapted.clearSpans()
        words.forEach {
            offset = it.applySpannable(adapted, offset, onWordClickListener)
        }
        ready = true
    }

    public fun removeWord(word: WordAdapted): Boolean {
        val removed = words.remove(word)
        if (removed) {
            word.removeSpannable(adapted)
        }
        return removed
    }

    fun getWords(): MutableSet<WordAdapted> {
        return words
    }

    public fun setOnWordClickListener(onWordClickListener: ((word: WordAdapted?) -> Unit)?) {
        this.onWordClickListener = onWordClickListener
    }
}