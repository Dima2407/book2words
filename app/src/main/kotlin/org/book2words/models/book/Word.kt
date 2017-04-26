package org.book2words.models.book

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.book2words.translate.core.Definition
import java.io.Serializable
import java.util.ArrayList

public open class Word(value: String) : Comparable<Word>, Serializable {
    var id: Int ?= null
    val locations: MutableList<WordLocation> = ArrayList()
    val value: String
    var definitions: Array<out Definition>? = null
    var translated: Boolean = false

    init {
        this.value = value.toLowerCase()
    }

    override fun compareTo(other: Word): Int {
        return value.compareTo(other.value.toLowerCase())
    }

    fun setId (id : Int){
        this.id = id
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        return value.equals(other.toString(), ignoreCase = true)
    }

    fun hasDefinitions(): Boolean {
        return definitions != null && definitions!!.isNotEmpty()
    }
}