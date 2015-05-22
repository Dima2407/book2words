package org.book2words.translate.core

public trait Translate {
    public fun getText() : String?

    public fun getMeans() : Array<out Mean> ?
}