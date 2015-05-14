package org.book2words.models

import java.io.Serializable

public class Chapter(val key: String, val size: Int) : Serializable {
    override fun toString(): String {
        return "${key}";
    }
}