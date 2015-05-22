package org.book2words.translate

public class TranslateProviderFactory {
    companion object {

        public fun create(provider: TranslateProvider.Provider, from: String, to: String): TranslateProvider {
            when (provider) {

                TranslateProvider.Provider.YANDEX -> return YandexTranslateProvider(from, to)
                else -> throw RuntimeException("Couldn't create translate provider")
            }
        }
    }
}
