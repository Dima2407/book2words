package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import org.book2words.core.FileStorage
import org.book2words.dao.DaoMaster
import org.book2words.dao.DaoSession
import org.book2words.dao.LibraryBookDao
import org.book2words.models.LibraryDictionary

class DataContext {

    companion object {
        fun <T> setup(context: T) where T : Application, T : DaoHolder {
            val helper = DaoMaster.DevOpenHelper(context, "b2w", null)
            val db = helper.writableDatabase
            val daoMaster = DaoMaster(db)
            context.setDaoSession(daoMaster.newSession())
        }

        private fun <T : Application> getSession(context: T): DaoSession {
            val application = context as DaoHolder
            return application.getDaoSession()
        }

        private fun <T : Activity> getSession(context: T): DaoSession {
            return getSession(context.application)
        }

        private fun <T : Service> getSession(context: T): DaoSession {
            return getSession(context.application)
        }

        private fun <T : Fragment> getSession(context: T): DaoSession {
            return getSession(context.activity)
        }

        fun <T : Activity> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        fun <T : Service> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        fun <T : Fragment> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        fun getDictionaries(): List<LibraryDictionary> {
            val dir = FileStorage.createDictionaryDirectory()
            val dictionaries = dir.listFiles().map { file ->
                val reader = file.bufferedReader()
                val readLine = reader.readLine()
                reader.close()
                LibraryDictionary(file.nameWithoutExtension, Integer.parseInt(readLine))
            }.toList();
            return dictionaries
        }
    }
}
