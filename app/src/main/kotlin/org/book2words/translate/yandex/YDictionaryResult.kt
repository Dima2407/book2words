package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName
import org.book2words.translate.core.Definition
import org.book2words.translate.core.DictionaryResult

class YDictionaryResult : DictionaryResult {
    SerializedName("def")
    val definitions: Array<YDefinition> = arrayOf()

    override fun getResults(): Array<out Definition> {
        return definitions
    }
}
