package org.book2dictionary.core.dictionary

abstract class BaseUserDictionary : UserDictionary {

    protected abstract fun sort()

    class object {

        protected val USER_DICTIONARY: String = "user_dictionary"
    }
}
