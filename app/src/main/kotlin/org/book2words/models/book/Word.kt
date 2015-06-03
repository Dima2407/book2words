package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

public open class Word(value: String) : Comparable<Word>, Serializable {
    SerializedName("p")
    public val paragraphs: MutableList<Paragraph> = ArrayList()
    SerializedName("v")
    public val value: String;

    init {
        this.value = value.toLowerCase();
    }

    override fun compareTo(other: Word): Int {
        return value.compareTo(other.value.toLowerCase())
    }

    public fun addParagraph(index :Int, partition: Int, start: Int, end : Int) {
        paragraphs.add(Paragraph(index, partition, start, end ))
    }

    override fun toString(): String {
        return "${value}";
    }

    override fun equals(other: Any?): Boolean {
        return value.equals(other.toString(), ignoreCase = true)
    }
}