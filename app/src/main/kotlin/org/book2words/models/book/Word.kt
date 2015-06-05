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

    public fun addParagraph(index: Int, partition: Int, start: Int, end: Int) {
        paragraphs.add(Paragraph(index, partition, start, end))
    }

    override fun toString(): String {
        return "${value}";
    }

    override fun equals(other: Any?): Boolean {
        return value.equals(other.toString(), ignoreCase = true)
    }

    public fun toSeparatedString(separator: String): String {
        return "${value}${separator}${paragraphs.joinToString(separator)}"
    }

    companion object {
        public fun fromSeparatedString(input: String, separator: String): Word {
            val sequence = input.splitToSequence(separator)
            val value = sequence.elementAt(0)
            val word = Word(value)
            val regex = ",".toRegex()
            sequence.forEachIndexed { i, s ->
                if (i >= 1) {
                    val strings = s.split(regex)
                    word.addParagraph(strings[0].toInt(),
                            strings[1].toInt(),
                            strings[2].toInt(), strings[3].toInt())
                }
            }
            return word
        }
    }
}