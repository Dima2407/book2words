package org.book2dictionary.core

import org.book2dictionary.core.book.BookDictionary
import org.book2dictionary.core.book.BookDictionaryFactory
import org.book2dictionary.core.dictionary.UserDictionaryFactory

public trait Dictionary : Iterable<String> {
    public fun remove(word: String): Boolean {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    public fun add(word: String): Boolean {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    public fun contains(word: String): Boolean {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    public fun prepare(write: Boolean) {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    public fun release() {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    override public fun iterator(): Iterator<String> {
        throw UnsupportedOperationException("Dictionary isn't implemented")
    }

    class object {

        public fun <T : Dictionary> openUserDictionary(provider: Provider, path: String): T {
            return UserDictionaryFactory.create(provider, path) as T;
        }

        public fun <T : Dictionary> openBookDictionary(provider: Provider, directory: String, name: String): T {
            return BookDictionaryFactory.create(provider, directory, name) as T;
        }

        public fun <T : Dictionary> createBookDictionary(provider: Provider, directory: String, name: String): T {
            return BookDictionaryFactory.create(provider, directory, name, true) as T;
        }
    }
}
