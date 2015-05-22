package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList
import java.util.TreeSet

public class Word(value: String) : Comparable<Word>, Serializable {
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

    public fun addParagraph(chapter: Int, section: Int, start: Int, end : Int) {
        paragraphs.add(Paragraph("${chapter}-${section}", start, end ))
    }

    override fun toString(): String {
        return "${value}";
    }

    override fun equals(other: Any?): Boolean {
        return value.equalsIgnoreCase(other.toString())
    }
}