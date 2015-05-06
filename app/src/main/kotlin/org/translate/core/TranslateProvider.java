package org.translate.core;

public interface TranslateProvider {
    enum Provider {
        YANDEX
    }

     void  translate(String input, TranslateHandler handler);
}
