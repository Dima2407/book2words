package org.book2words.translate

import org.book2words.data.CacheDictionary

public class TranslateProviderFactory {
    companion object {

        public fun create(provider: TranslateProvider.Provider, cache : CacheDictionary, from: String, to: String): TranslateProvider {
            when (provider) {

                TranslateProvider.Provider.YANDEX -> return YandexTranslateProvider(cache, from, to)
                else -> throw RuntimeException("Couldn't create translate provider")
            }
        }
    }
}
