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
        if(other is Word){
            val o = other
            return word.equals(o.value, ignoreCase = true)
        }
        if(other is WordAdapted) {
            val o = other
            return word.equals(o.word, ignoreCase = true)
        }
        return false
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