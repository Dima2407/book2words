package com.easydictionary.app.core.translate;

public interface TranslateProvider {
    enum Provider {
        YANDEX
    }

     void  translate(String input, TranslateHandler handler);
}
