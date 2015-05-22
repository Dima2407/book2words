package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public class Paragraph(
        SerializedName("i") val index: Int,
        SerializedName("k") val key: String,
        SerializedName("s") val start: Int,
        SerializedName("e") val end: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val o = other as Paragraph
        return key.equals(o.key)
    }
}