package org.book2words.models.book

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import java.io.Serializable
import java.util.ArrayList
import java.util.TreeSet

public class ParagraphAdapted(val original: String, var ready: Boolean = false) : Serializable {
    var translated: Boolean = false

    private var adapted = SpannableStringBuilder("\t${original}")

    private val words: MutableSet<WordAdapted> = TreeSet()

    private var onWordClickListener: ((word: WordAdapted) -> Unit)? = null

    private companion object {
        private val COLORS = arrayOf(
                "#E57373",
                "#9575CD",
                "#4FC3F7",
                "#81C784",
                "#FFF176",
                "#FF8A65",
                "#F06292",
                "#7986CB",
                "#4DD0E1",
                "#AED581",
                "#FFD54F",
                "#BA68C8",
                "#64B5F6",
                "#4DB6AC",
                "#DCE775",
                "#FFB74D",
                "#F44336",
                "#673AB7",
                "#03A9F4",
                "#4CAF50",
                "#FFEB3B",
                "#FF5722",
                "#E91E63",
                "#3F51B5",
                "#00BCD4",
                "#8BC34A",
                "#FFC107",
                "#9C27B0",
                "#2196F3",
                "#009688",
                "#CDDC39",
                "#FF9800",
                "#D32F2F",
                "#512DA8",
                "#0288D1",
                "#388E3C",
                "#FBC02D",
                "#E64A19",
                "#C2185B",
                "#303F9F",
                "#0097A7",
                "#689F38",
                "#FFA000",
                "#7B1FA2",
                "#1976D2",
                "#00796B",
                "#AFB42B",
                "#F57C00",
                "#B71C1C",
                "#311B92",
                "#01579B",
                "#1B5E20",
                "#F57F17",
                "#BF360C",
                "#880E4F",
                "#1A237E",
                "#006064",
                "#33691E",
                "#FF6F00",
                "#4A148C",
                "#0D47A1",
                "#004D40",
                "#827717",
                "#E65100",
                "#D50000",
                "#6200EA",
                "#01579B",
                "#00C853",
                "#FFD600",
                "#DD2C00",
                "#C51162",
                "#304FFE",
                "#00B8D4",
                "#64DD17",
                "#FFAB00",
                "#AA00FF",
                "#2962FF",
                "#00BFA5",
                "#AEEA00",
                "#FF6D00",
                "#FF1744",
                "#651FFF",
                "#00B0FF",
                "#00E676",
                "#FFEA00",
                "#FF3D00",
                "#F50057",
                "#3D5AFE",
                "#00E5FF",
                "#76FF03",
                "#FFC400",
                "#D500F9",
                "#2979FF",
                "#1DE9B6",
                "#C6FF00",
                "#FF9100",
                "#FF8A80",
                "#B388FF",
                "#80D8FF",
                "#B9F6CA",
                "#FFFF8D",
                "#FF9E80",
                "#FF80AB",
                "#8C9EFF",
                "#84FFFF",
                "#CCFF90",
                "#FFE57F",
                "#EA80FC",
                "#82B1FF",
                "#A7FFEB",
                "#F4FF81",
                "#FFD180"
        )
    }

    public fun getAdapted(): Spannable {
        if (!ready) {
            prepareTextWithWords()
        }
        return adapted
    }

    fun modify(pIndex: Int, word: Word) {
        var index = words.size % COLORS.size
        val color = Color.parseColor(COLORS[index])
        val founds = word.locations.filter {
            pIndex == it.paragraph
        }
        founds.forEach {
            val start = it.start
            val end = it.end
            val wordAdapted = WordAdapted(start, end, word, color)
            this.words.add(wordAdapted)
        }
    }

    public fun prepareTextWithWords() {
        var offset = 0
        adapted.clearSpans()
        words.forEach {
            offset = it.applySpannable(adapted, offset, onWordClickListener)
        }
        ready = true
    }

    public fun removeWord(word: WordAdapted) {
        val removed = TreeSet(words.filter { it.equals(word) }).reversed()
        var offsets = ArrayList<Int>()
        removed.forEach {
            val success = words.remove(it)
            if (success) {
                offsets + it.removeSpannable(adapted)
            }
        }
    }

    fun getWords(): Collection<WordAdapted> {
        val unique = ArrayList<WordAdapted>()
        words.forEach {
            if (!unique.contains(it) && it.hasDefinitions()) {
                unique.add(it)
            }
        }
        return unique
    }

    fun getNotTranslatedWords(): Collection<WordAdapted> {
        val unique = ArrayList<WordAdapted>()
        words.forEach {
            if (!unique.contains(it) && !it.isTranslated()) {
                unique.add(it)
            }
        }
        return unique
    }

    fun hasWords(): Boolean {
        return words.isEmpty();
    }

    public fun setOnWordClickListener(onWordClickListener: ((word: WordAdapted) -> Unit)?) {
        this.onWordClickListener = onWordClickListener
    }
}