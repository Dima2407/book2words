package org.book2words.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import org.book2words.dao.DaoMaster
import org.book2words.dao.DaoSession
import org.book2words.dao.LibraryBookDao
import org.book2words.dao.LibraryDictionaryDao

public class DataContext {

    companion object {
        public fun <T> setup(context: T) where T : Application, T : DaoHolder {
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

        public fun <T : Activity> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        public fun <T : Service> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        public fun <T : Fragment> getLibraryBookDao(context: T): LibraryBookDao {
            return getSession(context).libraryBookDao
        }

        public fun <T : Activity> getLibraryDictionaryDao(context: T): LibraryDictionaryDao {
            return getSession(context).libraryDictionaryDao
        }

        public fun <T : Service> getLibraryDictionaryDao(context: T): LibraryDictionaryDao {
            return getSession(context).libraryDictionaryDao
        }

        public fun <T : Fragment> getLibraryDictionaryDao(context: T): LibraryDictionaryDao {
            return getSession(context).libraryDictionaryDao
        }
    }
}
