package org.book2dictionary.core.dictionary

import org.book2dictionary.core.Provider
import org.book2dictionary.core.Dictionary

public class UserDictionaryFactory {
    class object {

        public fun create(provider: Provider, path: String): Dictionary {
            when (provider) {

                Provider.ExelFile -> return ExelUserDictionary(path)
                Provider.SQLite -> return SQLiteUserDictionary()
                Provider.File -> return FileUserDictionary(path)
                else -> throw RuntimeException("Couldn't create dictionary provider")
            }

        }
    }
}
