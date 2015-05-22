package org.book2words.translate.core

public trait Definition {
    public fun getText() : String?

    public fun getTranscription () : String?

    public fun getTranslates() : Array<out Translate> ?
}