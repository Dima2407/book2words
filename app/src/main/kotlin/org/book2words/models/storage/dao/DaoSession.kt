package org.book2words.models.storage.dao

import android.content.Context
import org.book2words.models.storage.LibraryBook

public class DaoSession(private val context: Context) {
    private val libraryBookDao: CoreDao<LibraryBook>

    init {
        libraryBookDao = LibraryBookDao(context)
    }

    public fun getLibraryBookDao(): CoreDao<LibraryBook> = libraryBookDao

    public fun load(){
        libraryBookDao.load()
    }

    public fun save(){
        libraryBookDao.commit()
    }
}
