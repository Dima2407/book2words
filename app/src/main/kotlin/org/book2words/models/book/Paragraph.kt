package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public class Paragraph(SerializedName("holder") val chapter: Chapter,
                       SerializedName("count") private var size: Int = 0) : Serializable {

    override fun toString(): String {
        return "${chapter} - ${size}";
    }

    fun setSize(wordsCount: Int) {
        size = wordsCount
    }
}