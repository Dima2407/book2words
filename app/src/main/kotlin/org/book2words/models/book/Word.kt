package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList
import java.util.TreeSet

public class Word(value: String) : Comparable<Word>, Serializable {
    SerializedName("i")
    public val keys: MutableSet<Int> = TreeSet()
    SerializedName("v")
    public val value: String;

    init {
        this.value = value.toLowerCase();
    }

    override fun compareTo(other: Word): Int {
        return value.compareTo(other.value.toLowerCase())
    }

    public fun addKey(key: Int) {
        keys.add(key)
    }

    override fun toString(): String {
        return "${value}";
    }

    override fun equals(other: Any?): Boolean {
        return value.equalsIgnoreCase(other.toString())
    }
}