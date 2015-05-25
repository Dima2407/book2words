package org.book2words.translate.yandex

import com.google.gson.annotations.SerializedName
import org.book2words.translate.core.DictionaryResult

class YandexDictionaryResult : DictionaryResult {
    SerializedName("def")
    private val definitions: Array<Definition>? = null

    override fun results(): Array<out org.book2words.translate.core.Definition>? {
        return definitions
    }

}
