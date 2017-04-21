package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import org.book2words.core.FileStorage
import org.book2words.database.*
import org.book2words.models.LibraryDictionary

class DataContext {

    companion object {
        fun <T> setup(context: T) where T : Application, T : DaoHolder {
            context.setDaoSession(DaoSession(context))
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


        /*fun <T : Activity> getPartitionsBookDao(context: T): PartitionsBookDao {
            return getSession(context).partitionsBookDao
        }

        fun <T : Service> getPartitionsBookDao(context: T): PartitionsBookDao {
            return getSession(context).partitionsBookDao
        }

        fun <T : Fragment> getPartitionsBookDao(context: T): PartitionsBookDao {
            return getSession(context).partitionsBookDao
        }*/


        fun <T : Activity> getWordsFoundDao(context: T): WordsFoundDao {
            return getSession(context).wordsFoundDao
        }

        fun <T : Service> getWordsFoundDao(context: T): WordsFoundDao {
            return getSession(context).wordsFoundDao
        }

        fun <T : Fragment> getWordsFoundDao(context: T): WordsFoundDao {
            return getSession(context).wordsFoundDao
        }


        fun <T : Activity> getPartsDao(context: T): PartsDao {
            return getSession(context).partsDao
        }

        fun <T : Service> getPartsDao(context: T): PartsDao {
            return getSession(context).partsDao
        }

        fun <T : Fragment> getPartsDao(context: T): PartsDao {
            return getSession(context).partsDao
        }

        fun getDictionaries(): List<LibraryDictionary> {
            val dir = FileStorage.createDictionaryDirectory()
            val dictionaries = dir.listFiles().map { file ->
                val reader = file.bufferedReader()
                val readLine = reader.readLine()
                reader.close()
                LibraryDictionary(file.nameWithoutExtension, 0)
            }.toList();
            return dictionaries
        }
    }
}
