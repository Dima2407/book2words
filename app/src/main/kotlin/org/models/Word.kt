package org.models

import java.io.Serializable
import java.util.ArrayList

public class Word(value: String, paragraph: Paragraph? = null) : Comparable<Word>, Serializable {
    private val paragraphs = ArrayList<Paragraph>()
    public val value : String;
    init {
        this.value = value.toLowerCase();
        if (paragraph != null) {
            paragraphs.add(paragraph)
        }
    }

    override fun compareTo(other: Word): Int {
        return value.compareTo(other.value.toLowerCase())
    }

    public fun bindParagraph(paragraph: Paragraph) {
        paragraphs.add(paragraph)
    }

    override fun toString(): String {
        return "${value}";
    }

    override fun equals(other: Any?): Boolean {
        return value.equalsIgnoreCase(other.toString())
    }
}