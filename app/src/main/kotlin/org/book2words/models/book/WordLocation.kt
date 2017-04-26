package org.book2words.models.book

import java.io.Serializable

data class WordLocation(val book: Long,
                        val chapter: Int,
                        val paragraph: Int,
                        val start: Int,
                        val end: Int) : Serializable {

    override fun toString(): String {
        return "$book, $chapter, $paragraph, $start, $end"
    }
}