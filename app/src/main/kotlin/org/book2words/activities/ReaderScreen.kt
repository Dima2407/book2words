package org.book2words.activities

import org.book2words.services.BookReadService

public interface ReaderScreen {
    fun getReader() : BookReadService.BookReaderBinder
}
