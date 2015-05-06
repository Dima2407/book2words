package org.book2dictionary.core.book

import org.book2dictionary.core.Provider
import org.book2dictionary.core.Dictionary

public class BookDictionaryFactory {
    class object {

        public fun create(provider: Provider, directory: String, name: String): Dictionary {
            return create(provider, directory, name, false)
        }

        public fun create(provider: Provider, directory: String, name: String, deleteExisted: Boolean): Dictionary {
            when (provider) {

                Provider.ExelFile -> return ExelBookDictionary(directory, name, deleteExisted)
                Provider.File -> return FileBookDictionary(directory, name, deleteExisted)
                Provider.SQLite -> return SQLiteBookDictionary()
                else -> throw RuntimeException()
            }
        }
    }
}
