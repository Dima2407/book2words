package org.book2words.models.book

import org.book2words.translate.core.Definition

public class WordAdapted(val start: Int,
                         val end: Int,
                         val word: String,
                         val color: Int,
                         public var translated: Boolean = false) : Comparable<WordAdapted> {

    private var definitions: Array<out Definition>? = null

    override fun compareTo(other: WordAdapted): Int {
        return start - other.start
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val o = other as WordAdapted
        return word.equalsIgnoreCase(o.word)
    }

    public fun setDefinitions(defs: Array<out Definition>?) {
        if (!translated) {
            definitions = defs
        }
        translated = true
    }

    fun getDefinitions(): Array<out Definition>? {
        return definitions
    }
}