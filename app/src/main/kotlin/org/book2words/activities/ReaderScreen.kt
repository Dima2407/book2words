package org.book2words.activities

import org.book2words.services.BookReaderBinder

public interface ReaderScreen {
    fun getReader() : BookReaderBinder
}
