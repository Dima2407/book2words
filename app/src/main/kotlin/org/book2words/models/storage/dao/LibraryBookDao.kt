package org.book2words.models.storage.dao

import android.content.Context
import org.book2words.models.storage.LibraryBook
import java.io.File

private class LibraryBookDao(private val context: Context) : CoreDao<LibraryBook>() {
    override fun getPath(): File =
            File(context.getCacheDir(), "books.json")
}