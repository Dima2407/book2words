package org.book2words.translate.core

public interface Translate {
    public fun getText() : String?

    public fun getMeans() : Array<out Mean> ?
}