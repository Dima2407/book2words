package org.book2words.models.book

import java.util.ArrayList

public class Partition(val key: String) {

    var id : Int? = null
    var paragraphs = ArrayList<String>()

    public fun add(p: String) {
        paragraphs.add(p)
    }


    public fun forEach(operation: (String) -> Unit): Unit {
        for (element in paragraphs) operation(element)
    }

    public fun forEachIndexed(operation: (Int, String) -> Unit): Unit {
        paragraphs.forEachIndexed { i, s ->
            operation(i, s)
        }
    }
}