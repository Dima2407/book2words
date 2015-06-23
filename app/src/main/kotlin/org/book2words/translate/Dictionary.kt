package org.book2words.translate

import android.content.Context
import org.book2words.data.CacheDictionary
import org.book2words.translate.core.Definition

public interface Dictionary {
    public fun find(input: String, onFound: (input: String, definitions: Array<out Definition>) -> Unit)

    public fun find(input: String): Array<out Definition>

    companion object {
        private val TARGET = "ru"
        public fun createOnline(cache: CacheDictionary, from: String): Dictionary {
            return OnlineDictionary(cache, from, TARGET)
        }

        public fun createVerbs(context: Context): Dictionary {
            return WrongVerbsDictionary(context.getResources())
        }

        public fun createOffline(cache: CacheDictionary, from: String): Dictionary {
            return OnlineDictionary(cache, from, TARGET)
        }
    }
}
