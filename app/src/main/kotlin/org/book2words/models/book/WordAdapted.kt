package org.book2words.models.book

public class WordAdapted(private val position: Int, val word: String, val color: Int) : Comparable<WordAdapted> {
    override fun compareTo(other: WordAdapted): Int {
        return position - other.position
    }

}