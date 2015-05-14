package org.book2words.models

import java.io.Serializable

public class Paragraph(val chapter : Chapter, private var size : Int = 0) : Serializable {

    override fun toString(): String {
        return "${chapter} - ${size}";
    }

    fun setSize(wordsCount: Int) {
        size = wordsCount
    }
}