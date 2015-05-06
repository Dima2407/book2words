package org.translate.core;

public interface TranslateHandler {

    void onTranslate(String word, DictionaryResult result);
}
