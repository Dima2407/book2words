package org.book2dictionary.core.dictionary

public class UserDictionaries {
    class object {

        public fun sort(userDictionary: UserDictionary) {

            val dictionary = userDictionary as BaseUserDictionary
            dictionary.sort()
        }

        public fun export(from: UserDictionary, to: UserDictionary) {
            from.prepare(false)
            to.prepare(true)
            for (word in from) {
                to.add(word)
            }
            from.release()
            to.release()
        }
    }
}
