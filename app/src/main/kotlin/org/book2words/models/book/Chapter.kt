package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public class Chapter(SerializedName("key") val key: Int, SerializedName("count") val size: Int) : Serializable {
    override fun toString(): String {
        return "${key}";
    }
}