package org.book2words.translate

import org.book2words.translate.core.DictionaryResult

public interface TranslateProvider {
    public enum class Provider {
        YANDEX
    }

    public fun translate(input: String, onTranslated: (input: String, result: DictionaryResult?) -> Unit)
}
