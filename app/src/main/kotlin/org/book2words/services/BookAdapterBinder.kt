package org.book2words.services

import android.app.Service
import android.os.Binder
import android.os.Handler
import android.webkit.WebView
import org.book2words.dao.LibraryBook
import org.book2words.models.split.BookSplitter
import java.util.concurrent.Executors

public class BookAdapterBinder(
        private val book: LibraryBook,
        private val service: Service) : Binder() {

    private val handler = Handler()

    private val executor = Executors.newSingleThreadExecutor()

    val splitter: BookSplitter

    init {
        splitter = BookSplitter(book)
    }

    public fun prepare(surface: WebView,
                       onPrepared: (title: String, length: Int) -> Unit,
                       onReleased: () -> Unit) {
        executor.execute {
            splitter.prepare(surface, onPrepared = { t, i ->
                handler.post({
                    onPrepared(t, i)
                })
                BookSplitService.openBook(service, book)
            }, onReleased = {
                handler.post({
                    onReleased()
                })
                BookSplitService.closeBook(service, book)
            })
        }
    }

    public fun start(onProgress: (progress: Int, limit: Int) -> Unit) {
        executor.execute {
            splitter.split(onSpiltProgress = {
                index, length, text ->
                handler.post({
                    onProgress(index, length)
                })
                BookSplitService.save(service, book.getId(), index, text)
            })
        }
    }
}