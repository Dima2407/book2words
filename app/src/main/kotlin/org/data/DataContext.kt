package org.data

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.Context
import org.book2words.B2WApplication
import org.book2words.dao.*
import java.util.ArrayList

public class DataContext {

    companion object {
        public fun setup<T : Application> (context: T) where T : DaoHolder {
            val helper = DaoMaster.DevOpenHelper(context, "b2w", null)
            val db = helper.getWritableDatabase()
            val daoMaster = DaoMaster(db)
            context.setDaoSession(daoMaster.newSession())
        }

        private fun getSession<T : Application>(context: T): DaoSession {
            val application = context as DaoHolder
            return application.getDaoSession()
        }

        private fun getSession<T : Activity>(context: T): DaoSession {
            return getSession(context.getApplication())
        }

        private fun getSession<T : Service>(context: T): DaoSession {
            return getSession(context.getApplication())
        }

        private fun getSession<T : Fragment>(context: T): DaoSession {
            return getSession(context.getActivity())
        }

        public fun getLibraryBookDao<T : Activity>(context: T): LibraryBookDao {
            return getSession(context).getLibraryBookDao()
        }

        public fun getLibraryBookDao<T : Service>(context: T): LibraryBookDao {
            return getSession(context).getLibraryBookDao()
        }

        public fun getLibraryBookDao<T : Fragment>(context: T): LibraryBookDao {
            return getSession(context).getLibraryBookDao()
        }

        public fun getLibraryDictionaryDao<T : Activity>(context: T): LibraryDictionaryDao {
            return getSession(context).getLibraryDictionaryDao()
        }

        public fun getLibraryDictionaryDao<T : Service>(context: T): LibraryDictionaryDao {
            return getSession(context).getLibraryDictionaryDao()
        }

        public fun getLibraryDictionaryDao<T : Fragment>(context: T): LibraryDictionaryDao {
            return getSession(context).getLibraryDictionaryDao()
        }

        Deprecated
        public fun getUserDictionaries(): MutableList<LibraryDictionary> {
            val items: MutableList<LibraryDictionary> = ArrayList();
            items.add(LibraryDictionary(0, "The Shinning", true, 100, "/storage/sdcard0/Books/user_dictionary.txt"))
            items.add(LibraryDictionary(0, "The Girl", false, 100, "/storage/sdcard0/Books/user_dictionary.txt"))
            items.add(LibraryDictionary(0, "The Pet Samatary", false, 100, "/storage/sdcard0/Books/user_dictionary.txt"))
            return items
        }
    }
}
