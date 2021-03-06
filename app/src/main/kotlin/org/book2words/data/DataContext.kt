package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context
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


        fun <T : Activity> getUsedWordsDao(context: T): UsedWordsDao {
            return getSession(context).usedWordsDao
        }

        fun <T : Service> getUsedWordsDao(context: T): UsedWordsDao {
            return getSession(context).usedWordsDao
        }

        fun <T : Fragment> getUsedWordsDao(context: T): UsedWordsDao {
            return getSession(context).usedWordsDao
        }


        fun <T : Activity> getDictionaryDao(context: T): DictionaryDao {
            return getSession(context).dictionaryDao
        }

        fun <T : Service> getDictionaryDao(context: T): DictionaryDao {
            return getSession(context).dictionaryDao
        }

        fun <T : Fragment> getDictionaryDao(context: T): DictionaryDao {
            return getSession(context).dictionaryDao
        }

        fun <T : Application> getDictionaryDao(context: T): DictionaryDao {
            return getSession(context).dictionaryDao
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
