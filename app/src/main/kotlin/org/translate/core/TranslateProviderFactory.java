package org.translate.core;


public class TranslateProviderFactory {

    public static TranslateProvider create(TranslateProvider.Provider provider, String from, String to){
        switch (provider){

            case YANDEX:
                return new YandexTranslateProvider(from, to);
            default:
                throw new RuntimeException("Couldn't create translate provider");
        }
    }
}
