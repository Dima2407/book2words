package org.book2dictionary.core.book

import org.book2dictionary.core.Dictionary

public trait BookDictionary : Dictionary {
    public fun moveToChapter(index: Int): Boolean{
        throw UnsupportedOperationException("BookDictionary isn't implemented")
    }

    public fun addChapter(name: String) {
        throw UnsupportedOperationException("BookDictionary isn't implemented")
    }
}
